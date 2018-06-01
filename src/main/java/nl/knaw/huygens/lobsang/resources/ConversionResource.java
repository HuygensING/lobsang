package nl.knaw.huygens.lobsang.resources;

import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DateResult;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.ConverterRegistry;
import nl.knaw.huygens.lobsang.core.LocationRegistry;
import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("convert")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConversionResource {
  private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final Logger LOG = LoggerFactory.getLogger(ConversionResource.class);

  private final ConverterRegistry converters;
  private final LocationRegistry locations;

  public ConversionResource(ConverterRegistry converters, LocationRegistry locations) {
    this.converters = checkNotNull(converters);
    this.locations = checkNotNull(locations);
  }

  @POST
  public DateResult convert(@NotNull DateRequest dateRequest) {
    LOG.info("dateRequest: {}", dateRequest);

    final String location = dateRequest.getLocation();
    final String[] terms = location.toLowerCase().split("\\s");
    final List<String> matchingLocations = findMatchingLocations(terms);
    LOG.info("matchingLocations: {}", matchingLocations);

    final List<YearMonthDay> suggestedDates = new ArrayList<>();

    matchingLocations.forEach(loc -> {
      locations.get(loc).forEach(calendarInfo -> {
        final String type = calendarInfo.getType();
        final YearMonthDay calendarDate = asYearMonthDay(calendarInfo.getStartDate());
        final YearMonthDay requestedDate = asYearMonthDay(dateRequest);
        LOG.trace("calendarDate: {}, requestedDate: {}", calendarDate, requestedDate);

        final CalendarConverter converter = converters.get(type);
        final int started = converter.toJulianDay(calendarDate);
        final int request = converter.toJulianDay(requestedDate);
        LOG.trace("started: {}, request: {}", started, request);

        final YearMonthDay finalDate;
        if (request < started) {
          LOG.debug("request < started, so converting");
          finalDate = converter.fromJulianDay(converters.defaultConverter().toJulianDay(requestedDate));
        } else {
          finalDate = requestedDate;
        }
        LOG.trace("finalDate: {}", finalDate);
        suggestedDates.add(finalDate);
      });
    });

    return new DateResult(suggestedDates.get(0)); // just the first one for now!
  }

  private List<String> findMatchingLocations(final String[] terms) {
    return locations.list().stream()
                    .filter(location
                      -> Arrays.stream(terms)
                               .allMatch(term -> location.toLowerCase().contains(term)))
                    .collect(Collectors.toList());
  }

  private YearMonthDay asYearMonthDay(String dateAsString) {
    final LocalDate date = LocalDate.parse(dateAsString, YYYY_MM_DD);
    return new YearMonthDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
  }

  private YearMonthDay asYearMonthDay(DateRequest dateRequest) {
    return new YearMonthDay(dateRequest.getYear(), dateRequest.getMonth(), dateRequest.getDay());
  }
}
