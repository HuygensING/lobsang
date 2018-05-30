package nl.knaw.huygens.lobsang.core;

import nl.knaw.huygens.lobsang.core.converters.CalendarConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConverterRegistry {
  private final Map<String, CalendarConverter> convertersByType = new HashMap<>();

  public Set<String> list() {
    return convertersByType.keySet();
  }

  public CalendarConverter get(String type) {
    return convertersByType.get(type);
  }

  public void register(String type, CalendarConverter converter) {
    convertersByType.put(checkNotNull(type), checkNotNull(converter));
  }
}
