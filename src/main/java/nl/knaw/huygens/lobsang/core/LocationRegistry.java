package nl.knaw.huygens.lobsang.core;

import nl.knaw.huygens.lobsang.api.CalendarPeriod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class LocationRegistry {
  private final Map<String, List<CalendarPeriod>> calendarsByLocation = new HashMap<>();

  public void addLocationCalendar(String location, List<CalendarPeriod> calendars) {
    calendarsByLocation.put(location, calendars);
  }

  public Set<String> list() {
    return calendarsByLocation.keySet();
  }

  public Stream<String> stream() {
    return list().stream();
  }

  public List<CalendarPeriod> get(String location) {
    return calendarsByLocation.get(location);
  }
}
