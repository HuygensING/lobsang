package nl.knaw.huygens.lobsang;

import io.dropwizard.Application;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.huygens.lobsang.config.LobsangConfig;
import nl.knaw.huygens.lobsang.resources.AboutResource;
import nl.knaw.huygens.lobsang.resources.DateConversionResource;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;

public class Server extends Application<LobsangConfig> {
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);

  private static String findVersionInfo(String applicationName) throws IOException {
    Enumeration<URL> resources = Thread.currentThread().getContextClassLoader()
                                       .getResources("META-INF/MANIFEST.MF");
    while (resources.hasMoreElements()) {
      URL manifestUrl = resources.nextElement();
      Manifest manifest = new Manifest(manifestUrl.openStream());
      Attributes mainAttributes = manifest.getMainAttributes();
      String implementationTitle = mainAttributes.getValue("Implementation-Title");
      if (implementationTitle != null && implementationTitle.equals(applicationName)) {
        LOG.debug("lobsang mainAttributes: {}", mainAttributes);
        mainAttributes.forEach((key,value) -> LOG.debug(" - {}: {}", key, value));
        String implementationVersion = mainAttributes.getValue("Implementation-Version");
        String buildTime = mainAttributes.getValue("Build-Time");
        return implementationVersion + " (" + buildTime + ")";
      }
    }
    return "Current Version";
  }

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

  public void run(LobsangConfig lobsangConfig, Environment environment) throws IOException {
    setupLogging(environment);
    registerResources(environment.jersey());
  }

  private void registerResources(JerseyEnvironment jersey) throws IOException {
    jersey.register(new AboutResource(findManifest(getName())));
    jersey.register(new DateConversionResource());
  }

  private void setupLogging(Environment environment) {
    final String commitHash = "0xdeadbeef"; // TODO: extract build properties
    MDC.put("commit_hash", commitHash);

    environment.jersey().register(new LoggingFeature(java.util.logging.Logger.getLogger(getClass().getName()),
      Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, 1024));
  }
}
