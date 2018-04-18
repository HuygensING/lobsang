package nl.knaw.huygens.lobsang.resources;

import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;

import static org.assertj.core.api.Assertions.assertThat;

public class AboutResourceTest {
  private AboutResource resource;

  @Before
  public void setUp() {
    resource = new AboutResource();
  }

  @Test
  public void aboutReturnsManifestAttributes() {
    assertThat(resource.about()).containsExactly(new SimpleEntry<>("Manifest", "not found"));
  }
}
