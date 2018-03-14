package nl.knaw.huygens.lobsang;

import nl.knaw.huygens.lobsang.logging.RequestLoggingFilter;
import nl.knaw.huygens.lobsang.config.LobsangConfig;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.huygens.lobsang.resources.AboutResource;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.logging.Level;

public class Server extends Application<LobsangConfig> {
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);

  public static void main(String[] args) throws Exception {
    new Server().run(args);
  }

  @Override
  public String getName() {
    return "lobsang";
  }

  @Override
  public void initialize(Bootstrap<LobsangConfig> bootstrap) {
    LOG.info("initializing");
  }

  public void run(LobsangConfig lobsangConfig, Environment environment) {
    setupLogging(environment);
    environment.jersey().register(new AboutResource());
  }

  private void setupLogging(Environment environment) {
    final String commitHash = "0xdeadbeef"; // TODO: extract build properties
    MDC.put("commit_hash", commitHash);
    environment.jersey().register(new RequestLoggingFilter(commitHash));

    environment.jersey().register(new LoggingFeature(java.util.logging.Logger.getLogger(getClass().getName()),
      Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, 1024));
  }
}
