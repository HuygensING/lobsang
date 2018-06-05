package nl.knaw.huygens.lobsang.resources;

import nl.knaw.huygens.lobsang.api.CalendarPeriod;
import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DateResult;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.ConverterRegistry;
import nl.knaw.huygens.lobsang.core.PlaceRegistry;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("convert")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConversionResource {
  private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final Logger LOG = LoggerFactory.getLogger(ConversionResource.class);

  private final ConverterRegistry converters;
  private final PlaceRegistry places;

  public ConversionResource(ConverterRegistry converters, PlaceRegistry places) {
    this.converters = checkNotNull(converters);
    this.places = checkNotNull(places);
  }

  @POST
  public DateResult convert(@NotNull DateRequest dateRequest) {
    LOG.info("dateRequest: {}", dateRequest);

    final String location = dateRequest.getLocation();
    final String[] searchTerms = location.toLowerCase().split("\\s");

    final List<Place> candidates = Lists.newArrayList();

    final Map<YearMonthDay, Set<String>> suggestions = matchingPlaces(searchTerms)
      .peek(candidates::add)
      .map(convertForPlace(dateRequest))
      .flatMap(Function.identity())
      .collect(Collectors.toMap(ymd -> ymd, YearMonthDay::getNotes, (s1, s2) -> {
        s1.addAll(s2);
        return s1;
      }));

    // collate notes
    suggestions.keySet().forEach(yearMonthDay -> yearMonthDay.setNotes(suggestions.get(yearMonthDay)));

    LOG.debug("suggestions: {}", suggestions);

    final DateResult result;
    if (suggestions.isEmpty()) {
      int defaultDate = converters.defaultConverter().toJulianDay(asYearMonthDay(dateRequest));
      result = new DateResult(converters.get(dateRequest.getType()).fromJulianDay(defaultDate));
      result.addHint("Requested date lies outside all defined calendar ranges, assuming default calendar was in use.");
    } else {
      LOG.debug("suggestions (size {}): {}", suggestions.size(), suggestions);
      result = new DateResult(Lists.newArrayList(suggestions.keySet()));
    }

    if (candidates.size() > 1) {
      final List<String> candidateNames = candidates.stream()
                                                    .map(place -> String.format("\"%s\"", place.getName()))
                                                    .sorted()
                                                    .collect(Collectors.toList());
      final String format = "Multiple places matched '%s': %s. Being more specific may increase accuracy.";
      result.addHint(String.format(format, dateRequest.getLocation(), candidateNames));
    }

    return result;
  }

  private Function<Place, Stream<YearMonthDay>> convertForPlace(DateRequest dateRequest) {
    return place -> place.getCalendars().stream()
                         .map(tryDateConversion(dateRequest))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .peek(it -> it.addNote(String.format("Based on data for place: '%s'", place.getName())));
  }

  private Stream<Place> matchingPlaces(String[] terms) {
    return places.stream().filter(isMatchingLocation(terms)).map(places::get);
  }

  private Predicate<String> isMatchingLocation(String[] terms) {
    return location -> Arrays.stream(terms)
                             .allMatch(locationContainsTerm(location));
  }

  private Predicate<String> locationContainsTerm(String location) {
    return term -> location.toLowerCase().contains(term);
  }

  private YearMonthDay asYearMonthDay(String dateAsString) {
    final LocalDate date = LocalDate.parse(dateAsString, YYYY_MM_DD);
    return new YearMonthDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
  }

  private YearMonthDay asYearMonthDay(DateRequest dateRequest) {
    return new YearMonthDay(dateRequest.getYear(), dateRequest.getMonth(), dateRequest.getDay());
  }

  private Function<CalendarPeriod, Optional<YearMonthDay>> tryDateConversion(DateRequest dateRequest) {
    return calendarPeriod -> {
      final CalendarConverter requestConverter = converters.get(calendarPeriod.getCalendar());
      final int requestDate = requestConverter.toJulianDay(asYearMonthDay(dateRequest));

      // Assuming this calendar is applicable, 'result' is the requestDate converted to the desired calendar
      final CalendarConverter resultConverter = converters.get(dateRequest.getType());
      final YearMonthDay result = resultConverter.fromJulianDay(requestDate);

      // Determine if this calendar is applicable for the given date and annotate result as appropriate
      final String startDateAsString = calendarPeriod.getStartDate();
      final String endDateAsString = calendarPeriod.getEndDate();
      if (startDateAsString != null && endDateAsString != null) {
        final int startDate = requestConverter.toJulianDay(asYearMonthDay(startDateAsString));
        final int endDate = requestConverter.toJulianDay(asYearMonthDay(endDateAsString));
        if (requestDate >= startDate && requestDate <= endDate) {
          result.addNote(String.format("Date within %s calendar start and end bounds",
            calendarPeriod.getCalendar()));
          return Optional.of(result);
        }
      } else if (startDateAsString != null) {
        if (requestDate >= requestConverter.toJulianDay(asYearMonthDay(startDateAsString))) {
          result.addNote(String.format("Date on or after start of %s calendar",
            calendarPeriod.getCalendar()));
          return Optional.of(result);
        }
      } else if (endDateAsString != null) {
        if (requestDate <= requestConverter.toJulianDay(asYearMonthDay(endDateAsString))) {
          result.addNote(String.format("Date on or before end of %s calendar",
            calendarPeriod.getCalendar()));
          return Optional.of(result);
        }
      } else {
        result.addNote(String.format("No start or end defined for %s calendar (always in use?)",
          calendarPeriod.getCalendar()));
        return Optional.of(result);
      }

      return Optional.empty();
    };
  }
}
