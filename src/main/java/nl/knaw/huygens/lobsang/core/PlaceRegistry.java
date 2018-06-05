package nl.knaw.huygens.lobsang.core;

import nl.knaw.huygens.lobsang.api.Place;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class PlaceRegistry {
  private final Map<String, Place> placesByName = new HashMap<>();

  public PlaceRegistry(List<Place> places) {
    places.forEach(this::addPlace);
  }

  private void addPlace(Place place) {
    placesByName.put(place.getName(), place);
  }


  public Set<String> list() {
    return placesByName.keySet();
  }

  public Stream<String> stream() {
    return list().stream();
  }

  public Place get(String name) {
    return placesByName.get(name);
  }
}
