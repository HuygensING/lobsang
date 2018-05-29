package nl.knaw.huygens.lobsang;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import nl.knaw.huygens.lobsang.api.LocationInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

public class LobsangConfiguration extends Configuration {
  @Valid
  @NotNull
  private List<LocationInfo> locationInfo = Collections.emptyList();

  @JsonProperty
  public List<LocationInfo> getLocationInfo() {
    return locationInfo;
  }

}
