package nl.knaw.huygens.lobsang.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("julianDay")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
/**
 * See also:
 * https://stackoverflow.com/questions/47001216/how-to-convert-a-gregorian-date-to-julian-date-with-the-java-8-date
 * -time-api
 * http://time4j.net/javadoc-en/net/time4j/calendar/HistoricCalendar.html
 * http://www.time4j.net/tutorial/introduction.html
 * https://stevemorse.org/jcal/julian.html
 * https://en.wikipedia.org/wiki/Julian_day
 */
public class DateConversionResource {

  @POST
  @Path("fromGregorian")
  // The Julian day number can be calculated using the following formulas (integer division is used exclusively,
  // that is, the remainder of all divisions are dropped)!
  public JulianDay fromGregorian(Date date) {
    final int Y = date.year;
    final int M = date.month;
    final int D = date.day;

    return new JulianDay(
      (1461 * (Y + 4800 + (M - 14) / 12)) / 4 +
        (367 * (M - 2 - 12 * ((M - 14) / 12))) / 12 -
        (3 * ((Y + 4900 + (M - 14) / 12) / 100)) / 4 +
        D - 32075);
  }

  @POST
  @Path("fromJulian")
  public JulianDay fromJulian(Date date) {
    final int Y = date.year;
    final int M = date.month;
    final int D = date.day;

    return new JulianDay(
      367 * Y -
        (7 * (Y + 5001 + (M - 9) / 7)) / 4 +
        (275 * M) / 9 +
        D + 1729777);
  }

  @POST
  @Path("toJulian")
  public Date toJulian(JulianDay julianDay) {
    final int J = julianDay.value;

    // 1. For Julian calendar: f = J + j
    final int f = J + 1401;

    return findJulianGregorianDate(f);
  }

  @POST
  @Path("toGregorian")
  public Date toGregorian(JulianDay julianDay) {
    final int J = julianDay.value;

    // 1. For Gregorian calendar: f = J + j + (((4 × J + B) div 146097) × 3) div 4 + C
    final int f = J + 1401 + (((4 * J + 274277) / 146097) * 3) / 4 + -38;

    return findJulianGregorianDate(f);
  }

  private Date findJulianGregorianDate(int julGregF) {
    // 2. e = r × f + v
    final int e = 4 * julGregF + 3;

    // 3. g = mod(e, p) div r
    final int g = (e % 1461) / 4;

    // 4. h = u × g + w
    final int h = 5 * g + 2;

    // 5. D = (mod(h, s)) div u + 1
    final int D = ((h % 153) / 5) + 1;

    // 6. M = mod(h div s + m, n) + 1
    final int M = (((h / 153) + 2) % 12) + 1;

    // 7. Y = (e div p) - y + (n + m - M) div n
    final int Y = (e / 1461) - 4716 + ((12 + 2 - M) / 12);

    // D, M, and Y are the numbers of the day, month, and year respectively for the afternoon at the beginning of the
    // given Julian day.

    return new Date(Y, M, D);
  }

  static class Date {
    @JsonProperty
    int year;

    @JsonProperty
    int month;

    @JsonProperty
    int day;

    Date(){}

    Date(int year, int month, int day) {
      this.year = year;
      this.month = month;
      this.day = day;
    }
  }

  static class JulianDay {
    @JsonProperty
    int value;

    JulianDay(){}

    JulianDay(int value) {
      this.value = value;
    }
  }
}
