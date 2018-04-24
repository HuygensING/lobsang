package nl.knaw.huygens.lobsang.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_TITLE;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
class AboutEndpointTest {
  private static final Manifest manifest = mock(Manifest.class);

  private final ResourceExtension resources = ResourceExtension.builder()
                                                               .addResource(new AboutResource(manifest))
                                                               .build();

  @BeforeAll
  static void setUp() {
    final Attributes attributes = new Attributes();
    attributes.put(IMPLEMENTATION_VERSION, "1.0");
    attributes.put(IMPLEMENTATION_TITLE, "about-endpoint-test");

    when(manifest.getMainAttributes()).thenReturn(attributes);
  }

  @Test
  void aboutShowsManifestAttributesInJson() throws IOException {
    final ObjectReader reader = resources.getObjectMapper().reader();

    final JsonNode actual = reader.readTree(resources.client().target("/about").request().get(String.class));
    final JsonNode expected = reader.readTree(fixture("fixtures/about.json"));

    assertThat(actual).isEqualTo(expected);

  }
}
