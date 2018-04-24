package nl.knaw.huygens.lobsang;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import nl.knaw.huygens.lobsang.config.LobsangConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Client;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
class LobsangIntegrationTest {

  private static final DropwizardAppExtension<LobsangConfig> DROPWIZARD = new DropwizardAppExtension<>(
    Server.class, ResourceHelpers.resourceFilePath("config-test.yml")
  );

  @Test
  void runServerTest() {
    Client client = new JerseyClientBuilder().build();
    String result = client.target(
      String.format("http://localhost:%d/about", DROPWIZARD.getLocalPort())).request().get(String.class);
    assertThat(result).contains("Manifest");
  }
}
