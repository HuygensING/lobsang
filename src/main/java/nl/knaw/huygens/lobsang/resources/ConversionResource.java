package nl.knaw.huygens.lobsang.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DateResult;
import nl.knaw.huygens.lobsang.api.Place;
import nl.knaw.huygens.lobsang.api.StartOfYear;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.ConversionService;
import nl.knaw.huygens.lobsang.core.places.PlaceMatcher;
import nl.knaw.huygens.lobsang.core.places.SearchTermBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Comparator.comparing;

@Path("convert")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConversionResource {
  private static final MonthDay JANUARY_FIRST = MonthDay.of(1, 1);

  private static final DateTimeFormatter MM_DD = DateTimeFormatter.ofPattern("MM-dd");

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

    final List<Place> candidates = new ArrayList<>();

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
      result.addHint("Requested date lies outside all defined calendar periods.");
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
    final String targetCalendar = dateRequest.getTargetCalendar();
    final YearMonthDay requestDate = asYearMonthDay(dateRequest);
    return place -> place.getCalendarPeriods().stream()
                         .map(calendarPeriod -> conversions.convert(calendarPeriod, requestDate, targetCalendar))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .map(resultDate -> addPlaceNameNote(resultDate, place))
                         .map(resultDate -> adjustForYearStart(resultDate, requestDate, place.getStartOfYearList()));
  }

  private YearMonthDay addPlaceNameNote(YearMonthDay result, Place place) {
    result.addNote(String.format("Based on data for place: '%s'", place.getName()));
    return result;
  }

  private YearMonthDay adjustForYearStart(YearMonthDay result, YearMonthDay subject, List<StartOfYear> startOfYears) {
    final Year subjectYear = Year.of(subject.getYear());
    return startOfYears.stream()
                       .filter(startOfYear -> startOfYear.getSince().compareTo(subjectYear) <= 0)
                       .peek(startOfYear -> LOG.debug("in the race: {}", startOfYear))
                       .max(comparing(StartOfYear::getSince))
                       .map(startOfYear -> {
                         if (!JANUARY_FIRST.equals(startOfYear.getWhen()) && compare(subject, startOfYear) < 0) {
                           final YearMonthDay adjusted =
                             new YearMonthDay(result.getYear() + 1, result.getMonth(), result.getDay());
                           adjusted.setNotes(result.getNotes());
                           adjusted.addNote(
                             String.format(
                               "Date after 1 January, but in this period, New Year started on %s, so one year was " +
                                 "added",
                               startOfYear.getWhen().format(MM_DD)));
                           return adjusted;
                         } else {
                           result.addNote("No adjustment necessary based on start of new year.");
                           return result;
                         }
                       })
                       .orElseGet(() -> {
                         result.addNote("Insufficient info about when new year started");
                         return result;
                       });
  }

  private int compare(YearMonthDay subject, StartOfYear startOfYear) {
    return MonthDay.of(subject.getMonth(), subject.getDay()).compareTo(startOfYear.getWhen());
  }

  private YearMonthDay defaultConversion(DateRequest dateRequest) {
    return conversions.defaultConversion(asYearMonthDay(dateRequest), dateRequest.getTargetCalendar());
  }

  private YearMonthDay asYearMonthDay(DateRequest dateRequest) {
    return new YearMonthDay(dateRequest.getYear(), dateRequest.getMonth(), dateRequest.getDay());
  }
}
