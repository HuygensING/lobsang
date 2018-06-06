package nl.knaw.huygens.lobsang.core.places;

import nl.knaw.huygens.lobsang.api.Place;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class ContainsAllTermsMatcher implements PlaceMatcher {
  private final PlaceRegistry placeRegistry;

  public ContainsAllTermsMatcher(PlaceRegistry placeRegistry) {
    this.placeRegistry = checkNotNull(placeRegistry);
  }

  @Override
  public Stream<Place> match(String[] searchTerms) {
    return placeRegistry.stream().filter(matchesAll(searchTerms)).map(placeRegistry::get);
  }

  private Predicate<String> matchesAll(String[] terms) {
    return place -> Arrays.stream(terms).allMatch(place::contains);
  }

}
