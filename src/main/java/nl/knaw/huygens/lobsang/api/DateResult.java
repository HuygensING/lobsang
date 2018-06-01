package nl.knaw.huygens.lobsang.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.assertj.core.util.Lists;

import java.util.List;

@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"date", "hints"})
public class DateResult {
  private final YearMonthDay date;

  private List<String> hints;

  public DateResult(YearMonthDay date) {
    this.date = date;
  }

  @JsonProperty
  public YearMonthDay getDate() {
    return date;
  }

  @JsonProperty
  public List<String> getHints() {
    return hints;
  }

  public void addHint(String hint) {
    if (hints == null) {
      hints = Lists.newArrayList(hint);
    } else {
      hints.add(hint);
    }
  }
}
