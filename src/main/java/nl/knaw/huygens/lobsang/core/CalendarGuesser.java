package nl.knaw.huygens.lobsang.core;

import nl.knaw.huygens.lobsang.api.CalendarInfo;
import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.LocationInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarGuesser {
  private final Map<String, List<CalendarInfo>> calendarsByLocation = new HashMap<>();

  public CalendarGuesser(List<LocationInfo> locationInfos) {
    locationInfos.forEach(this::addLocationInfo);
  }

  private void addLocationInfo(LocationInfo locationInfo) {
    calendarsByLocation.put(locationInfo.getLocation(), locationInfo.getCalendars());
  }

  public List<CalendarType> guesstimate(String location, DateRequest request) {
    final List<CalendarInfo> calendarInfos = calendarsByLocation.get(request.getLocation());
    if (calendarInfos != null) {
      calendarInfos.forEach(calendarInfo -> {
        // converterSupplier.getSupplier(calendarInfo.getType()).convert(...)
        switch (calendarInfo.getType()) {
          case gregorian:
            break;
          case julian:
            break;
          default:
            break;
        }
      });
    }

    return Collections.emptyList();
  }
}
