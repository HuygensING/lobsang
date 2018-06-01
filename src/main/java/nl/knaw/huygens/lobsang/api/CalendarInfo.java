package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.MoreObjects.toStringHelper;

public class CalendarInfo {
  @JsonProperty("type")
  private String type;

  @JsonProperty("start")
  private String startDate;

  @JsonProperty("end")
  private String endDate;

  public String getType() {
    return type;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("type", type)
      .add("startDate", startDate)
      .add("endDate", endDate)
      .toString();
  }
}
