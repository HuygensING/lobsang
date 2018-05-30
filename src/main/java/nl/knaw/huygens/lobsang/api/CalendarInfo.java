package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.lobsang.core.CalendarType;

import static com.google.common.base.MoreObjects.toStringHelper;

public class CalendarInfo {
  @JsonProperty("type")
  private CalendarType type;

  @JsonProperty("start")
  private String startDate;

  public CalendarType getType() {
    return type;
  }

  public String getStartDate() {
    return startDate;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("type", type)
      .add("startDate", startDate)
      .toString();
  }
}
