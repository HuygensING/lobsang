package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Place {
  private String name;
  private List<CalendarPeriod> calendars;

  @JsonProperty
  public String getName() {
    return name;
  }

  @JsonProperty
  public List<CalendarPeriod> getCalendars() {
    return calendars;
  }


  @Override
  public String toString() {
    return toStringHelper(this)
      .add("name", name)
      .add("calendars", calendars)
      .toString();
  }

}
