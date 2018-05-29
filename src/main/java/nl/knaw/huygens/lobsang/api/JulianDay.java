package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.MoreObjects.toStringHelper;

public class JulianDay {
  private int value;

  private JulianDay() {
    // Jackson deserialization
  }

  public JulianDay(int value) {
    this.value = value;
  }

  @JsonProperty
  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return toStringHelper(this)
      .add("value", value)
      .toString();
  }
}
