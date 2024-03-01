package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ConnectedAppsAuthUrl
 */

@JsonTypeName("connected_apps_auth_url")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class ConnectedAppsAuthUrl {

  private String url;

  private String sessionId;

  public ConnectedAppsAuthUrl url(String url) {
    this.url = url;
    return this;
  }

  /**
   * A URL that is used to authenticate an end-user against a connected app.
   * @return url
  */
  
  @Schema(name = "url", description = "A URL that is used to authenticate an end-user against a connected app.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ConnectedAppsAuthUrl sessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * A unique identifier for the login session.
   * @return sessionId
  */
  
  @Schema(name = "session_id", description = "A unique identifier for the login session.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("session_id")
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConnectedAppsAuthUrl connectedAppsAuthUrl = (ConnectedAppsAuthUrl) o;
    return Objects.equals(this.url, connectedAppsAuthUrl.url) &&
        Objects.equals(this.sessionId, connectedAppsAuthUrl.sessionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, sessionId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnectedAppsAuthUrl {\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

