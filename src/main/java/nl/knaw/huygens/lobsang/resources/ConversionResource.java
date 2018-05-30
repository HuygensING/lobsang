package nl.knaw.huygens.lobsang.resources;

import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DateResult;
import nl.knaw.huygens.lobsang.api.YearMonthDay;
import nl.knaw.huygens.lobsang.core.ConverterRegistry;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("convert")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConversionResource {
  private final ConverterRegistry converters;

  public ConversionResource(ConverterRegistry converters) {
    this.converters = converters;
  }

  @POST
  public DateResult convert(@NotNull DateRequest dateRequest) {
    // base converter usage on DateRequest.location, assume Julian for now
    final int julianDay = converters.get("julian").toJulianDay(dateRequest);
    final YearMonthDay date = converters.get("gregorian").fromJulianDay(julianDay);
    return new DateResult(date);
  }
}
