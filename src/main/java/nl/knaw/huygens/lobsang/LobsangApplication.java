package nl.knaw.huygens.lobsang;

import io.dropwizard.Application;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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

import static java.util.jar.Attributes.Name.IMPLEMENTATION_TITLE;

public class LobsangApplication extends Application<LobsangConfiguration> {
  private static final Logger LOG = LoggerFactory.getLogger(LobsangApplication.class);

  private static Manifest findManifest(String name) throws IOException {
    Enumeration<URL> resources = Thread.currentThread().getContextClassLoader()
                                       .getResources("META-INF/MANIFEST.MF");
    while (resources.hasMoreElements()) {
      final URL manifestUrl = resources.nextElement();
      final Manifest manifest = new Manifest(manifestUrl.openStream());
      final Attributes mainAttributes = manifest.getMainAttributes();
      final String implementationTitle = mainAttributes.getValue(IMPLEMENTATION_TITLE);
      if (name.equals(implementationTitle)) {
        return manifest;
      }
    }

    return null;
  }

  public static void main(String[] args) throws Exception {
    new LobsangApplication().run(args);
  }

  @Override
  public String getName() {
    return "lobsang";
  }

  @Override
  public void initialize(Bootstrap<LobsangConfiguration> bootstrap) {
    LOG.info("initializing");
  }

  public void run(LobsangConfiguration lobsangConfiguration, Environment environment) throws IOException {
    setupLogging(environment);
    registerResources(environment.jersey());
    LOG.warn("registered calendars: {}", lobsangConfiguration.getLocationInfo());
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
