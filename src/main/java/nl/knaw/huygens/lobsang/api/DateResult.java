package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"date", "notes", "hints"})
public class DateResult {
  private final List<String> hints;
  private final List<String> notes;
  private final DayMonthYear date;

  public DateResult(DayMonthYear date) {
    this.date = date;
    this.hints = new ArrayList<>();
    this.notes = new ArrayList<>();
  }

  @JsonProperty("date")
  public DayMonthYear getDate() {
    return date;
  }

  public void addHint(String hint) {
    hints.add(hint);
  }

  @JsonProperty
  public List<String> getHints() {
    return hints;
  }

  public void addNote(String note) {
    notes.add(note);
  }

  @JsonProperty
  public List<String> getNotes() {
    return notes;
  }
}
