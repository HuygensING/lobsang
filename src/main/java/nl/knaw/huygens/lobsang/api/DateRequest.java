package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.MoreObjects.toStringHelper;

public class DateRequest {
  private static final Logger LOG = LoggerFactory.getLogger(DateRequest.class);

  private int year;

  private int month;

  private int day;

  public DateRequest() {
    // Jackson deserialization
  }

  public DateRequest(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  @JsonProperty
  public int getYear() {
    return year;
  }

  @JsonProperty
  public int getMonth() {
    return month;
  }

  @JsonProperty
  public int getDay() {
    return day;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("year", year)
      .add("month", month)
      .add("day", day)
      .toString();
  }
}
