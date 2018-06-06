package nl.knaw.huygens.lobsang.resources;

import com.google.common.collect.Sets;
import nl.knaw.huygens.lobsang.api.CalendarPeriod;
import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DateResult;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.ConversionService;
import nl.knaw.huygens.lobsang.core.places.PlaceMatcher;
import nl.knaw.huygens.lobsang.core.places.SearchTermBuilder;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("convert")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConversionResource {
  private static final Logger LOG = LoggerFactory.getLogger(ConversionResource.class);

  private final PlaceMatcher places;
  private final SearchTermBuilder termBuilder;
  private final ConversionService conversions;

  public ConversionResource(ConversionService conversions, PlaceMatcher places, SearchTermBuilder termBuilder) {
    this.places = checkNotNull(places);
    this.termBuilder = checkNotNull(termBuilder);
    this.conversions = conversions;
  }

  private static Function<String, String> asQuotedString() {
    return str -> String.format("\"%s\"", str);
  }

  @POST
  public DateResult convert(@NotNull DateRequest dateRequest) {
    LOG.info("dateRequest: {}", dateRequest);

    final List<Place> candidates = Lists.newArrayList();

    final Map<YearMonthDay, Set<String>> results
      = places.match(termBuilder.build(dateRequest))
              .peek(candidates::add)
              .map(convertForPlace(dateRequest))
              .flatMap(Function.identity())
              .collect(Collectors.toMap(ymd -> ymd, YearMonthDay::getNotes, Sets::union));

    // collate notes
    results.keySet().forEach(yearMonthDay -> yearMonthDay.setNotes(results.get(yearMonthDay)));

    LOG.debug("results: {}", results);

    final DateResult result;
    if (results.isEmpty()) {
      result = new DateResult(defaultConversion(dateRequest));
      result.addHint("Requested date lies outside all defined calendar periods, assuming default calendar was in use.");
    } else {
      LOG.debug("results (size {}): {}", results.size(), results);
      result = new DateResult(Lists.newArrayList(results.keySet()));
    }

    if (candidates.size() > 1) {
      final String candidateNames = joinPlaces(candidates);
      final String format = "Multiple places matched '%s': %s. Being more specific may increase accuracy.";
      result.addHint(String.format(format, dateRequest.getPlaceTerms(), candidateNames));
    }

    return result;
  }

  private String joinPlaces(List<Place> places) {
    return places.stream()
                 .map(Place::getName)
                 .map(asQuotedString())
                 .sorted()
                 .collect(Collectors.joining(",", "{", "}"));
  }

  private Function<Place, Stream<YearMonthDay>> convertForPlace(DateRequest dateRequest) {
    return place -> place.getCalendars().stream()
                         .map(tryConversion(asYearMonthDay(dateRequest), dateRequest.getTargetCalendar()))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .peek(it -> it.addNote(String.format("Based on data for place: '%s'", place.getName())));
  }

  private Function<CalendarPeriod, Optional<YearMonthDay>> tryConversion(YearMonthDay date, String targetCalendar) {
    return calendarPeriod -> conversions.convert(calendarPeriod, date, targetCalendar);
  }

  private YearMonthDay defaultConversion(DateRequest dateRequest) {
    return conversions.defaultConversion(asYearMonthDay(dateRequest), dateRequest.getTargetCalendar());
  }

  private YearMonthDay asYearMonthDay(DateRequest dateRequest) {
    return new YearMonthDay(dateRequest.getYear(), dateRequest.getMonth(), dateRequest.getDay());
  }
}
