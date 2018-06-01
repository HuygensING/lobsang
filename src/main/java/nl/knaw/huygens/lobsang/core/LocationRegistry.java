package nl.knaw.huygens.lobsang.core;

import nl.knaw.huygens.lobsang.api.CalendarInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocationRegistry {
  private final Map<String, List<CalendarInfo>> calendarsByLocation = new HashMap<>();

  public void addLocationCalendar(String location, List<CalendarInfo> calendars) {
    calendarsByLocation.put(location, calendars);
  }

  public Set<String> list() {
    return calendarsByLocation.keySet();
  }

  public List<CalendarInfo> get(String location) {
    return calendarsByLocation.get(location);
  }
}
