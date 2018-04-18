package nl.knaw.huygens.lobsang;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.lobsang.config.LobsangConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;

import static org.assertj.core.api.Assertions.assertThat;

public class LobsangIntegrationTest {
  @Rule
  public final DropwizardAppRule<LobsangConfig> rule =
    new DropwizardAppRule<>(Server.class, ResourceHelpers.resourceFilePath("config-test.yml"));

  @Test
  public void runServerTest() {
    Client client = new JerseyClientBuilder().build();
    String result = client.target(
      String.format("http://localhost:%d/about", rule.getLocalPort())).request().get(String.class);
    assertThat(result).contains("Manifest");
  }
}
