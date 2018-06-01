package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;

@JsonPropertyOrder({"year", "month", "day"})
public class YearMonthDay {
  private final int day;
  private final int month;
  private final int year;

  public YearMonthDay(int year, int month, int day) {

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
    return MoreObjects.toStringHelper(this)
                      .add("year", year)
                      .add("month", month)
                      .add("day", day)
                      .toString();
  }

}
