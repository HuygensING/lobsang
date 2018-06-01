package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;
import org.assertj.core.util.Lists;

import java.util.List;
import java.util.Objects;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"year", "month", "day", "notes"})
public class YearMonthDay {
  private final int day;
  private final int month;
  private final int year;

  private List<String> notes;

  public YearMonthDay(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  @JsonProperty
  public int getYear() {
    return year;
  }

  @JsonProperty
  public int getMonth() {
    return month;
  }

  @JsonProperty
  public int getDay() {
    return day;
  }

  @JsonProperty
  public List<String> getNotes() {
    return notes;
  }

  public void addNote(String note) {
    if (notes == null) {
      notes = Lists.newArrayList(note);
    } else {
      notes.add(note);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("year", year)
                      .add("month", month)
                      .add("day", day)
                      .add("notes", notes)
                      .toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    YearMonthDay that = (YearMonthDay) obj;
    return day == that.day &&
      month == that.month &&
      year == that.year &&
      Objects.equals(notes, that.notes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(day, month, year, notes);
  }
}
