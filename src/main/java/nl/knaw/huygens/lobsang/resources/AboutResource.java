package nl.knaw.huygens.lobsang.resources;

import io.dropwizard.jersey.caching.CacheControl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.concurrent.TimeUnit;

@Path("/about")
@Produces(MediaType.APPLICATION_JSON)
public class AboutResource {

  @GET
  @CacheControl(maxAge = 6, maxAgeUnit = TimeUnit.HOURS)
  public String hello() {
    return "Hello world2";
  }

}
