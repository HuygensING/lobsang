import config.LobsangConfig;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class LobsangApplication extends Application<LobsangConfig> {
  public static void main(String[] args) throws Exception {
    new LobsangApplication().run(args);
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
