package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.DateRequest;
import nl.knaw.huygens.lobsang.api.YearMonthDay;

public interface CalendarConverter {
  int toJulianDay(DateRequest dateRequest);

  YearMonthDay fromJulianDay(int julianDay);
}
