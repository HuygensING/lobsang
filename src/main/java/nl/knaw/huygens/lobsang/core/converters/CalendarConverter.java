package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.DayMonthYear;

public interface CalendarConverter {
  int toJulianDay(DateRequest dateRequest);

  DayMonthYear fromJulianDay(int julianDay);
}
