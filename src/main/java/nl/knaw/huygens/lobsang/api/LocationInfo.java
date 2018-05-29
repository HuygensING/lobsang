package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

public class LocationInfo {
  private String location;
  private List<CalendarInfo> calendars;

  @JsonProperty
  public String getLocation() {
    return location;
  }

  @JsonProperty
  public List<CalendarInfo> getCalendars() {
    return calendars;
  }


  @Override
  public String toString() {
    return toStringHelper(this)
      .add("location", location)
      .add("calendars", calendars)
      .toString();
  }

}
