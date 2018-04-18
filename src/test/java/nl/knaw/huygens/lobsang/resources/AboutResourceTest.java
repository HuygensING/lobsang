package nl.knaw.huygens.lobsang.resources;

import org.junit.Before;
import org.junit.Test;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AboutResourceTest {
  private static final Manifest manifest = mock(Manifest.class);
  private static final Attributes attributes = mock(Attributes.class);

  private AboutResource resource;

  @Before
  public void setUp() {
    resource = new AboutResource(manifest);
    when(manifest.getMainAttributes()).thenReturn(attributes);
  }

  @Test
  public void about() {
    assertThat(resource.about()).isEqualTo(attributes);
  }
}
