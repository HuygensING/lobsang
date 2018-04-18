package nl.knaw.huygens.lobsang.resources;

import io.dropwizard.jersey.caching.CacheControl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Path("about")
@Produces(MediaType.APPLICATION_JSON)
public class AboutResource {

  private final Manifest manifest;

  public AboutResource(Manifest manifest) {
    this.manifest = manifest;
  }

  @GET
  @CacheControl(maxAge = 6, maxAgeUnit = TimeUnit.HOURS)
  public Attributes about() {
    return manifest.getMainAttributes();
  }

}
