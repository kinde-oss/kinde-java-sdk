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
 * ConnectedAppsAccessToken
 */

@JsonTypeName("connected_apps_access_token")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class ConnectedAppsAccessToken {

  private String accessToken;

  private String accessTokenExpiry;

  public ConnectedAppsAccessToken accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * The access token to access a third-party provider.
   * @return accessToken
  */
  
  @Schema(name = "access_token", description = "The access token to access a third-party provider.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("access_token")
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public ConnectedAppsAccessToken accessTokenExpiry(String accessTokenExpiry) {
    this.accessTokenExpiry = accessTokenExpiry;
    return this;
  }

  /**
   * The date and time that the access token expires.
   * @return accessTokenExpiry
  */
  
  @Schema(name = "access_token_expiry", description = "The date and time that the access token expires.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("access_token_expiry")
  public String getAccessTokenExpiry() {
    return accessTokenExpiry;
  }

  public void setAccessTokenExpiry(String accessTokenExpiry) {
    this.accessTokenExpiry = accessTokenExpiry;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConnectedAppsAccessToken connectedAppsAccessToken = (ConnectedAppsAccessToken) o;
    return Objects.equals(this.accessToken, connectedAppsAccessToken.accessToken) &&
        Objects.equals(this.accessTokenExpiry, connectedAppsAccessToken.accessTokenExpiry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessToken, accessTokenExpiry);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnectedAppsAccessToken {\n");
    sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
    sb.append("    accessTokenExpiry: ").append(toIndentedString(accessTokenExpiry)).append("\n");
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

