package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"year", "month", "day"})
public class DayMonthYear {
  private final int day;
  private final int month;
  private final int year;

  public DayMonthYear(int day, int month, int year) {

    this.day = day;
    this.month = month;
    this.year = year;
  }

  @JsonProperty
  public int getDay() {
    return day;
  }

  @JsonProperty
  public int getMonth() {
    return month;
  }

  @JsonProperty
  public int getYear() {
    return year;
  }
}
