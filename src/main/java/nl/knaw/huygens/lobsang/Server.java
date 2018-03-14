package nl.knaw.huygens.lobsang;

import nl.knaw.huygens.lobsang.config.LobsangConfig;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Server extends Application<LobsangConfig> {
  public static void main(String[] args) throws Exception {
    new Server().run(args);
  }

  @Override
  public String getName() {
    return "lobsang";
  }

  @Override
  public void initialize(Bootstrap<LobsangConfig> bootstrap) {
    // nothing to do yet
  }

  public void run(LobsangConfig lobsangConfig, Environment environment) {
    // nothing to do yet
  }
}
