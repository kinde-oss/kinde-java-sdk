package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateApplicationRequest
 */

@JsonTypeName("updateApplication_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateApplicationRequest {

  private String name;

  private String languageKey;

  @Valid
  private List<String> logoutUris;

  @Valid
  private List<String> redirectUris;

  public UpdateApplicationRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The application's name.
   * @return name
  */
  
  @Schema(name = "name", description = "The application's name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UpdateApplicationRequest languageKey(String languageKey) {
    this.languageKey = languageKey;
    return this;
  }

  /**
   * The application's language key.
   * @return languageKey
  */
  
  @Schema(name = "language_key", description = "The application's language key.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language_key")
  public String getLanguageKey() {
    return languageKey;
  }

  public void setLanguageKey(String languageKey) {
    this.languageKey = languageKey;
  }

  public UpdateApplicationRequest logoutUris(List<String> logoutUris) {
    this.logoutUris = logoutUris;
    return this;
  }

  public UpdateApplicationRequest addLogoutUrisItem(String logoutUrisItem) {
    if (this.logoutUris == null) {
      this.logoutUris = new ArrayList<>();
    }
    this.logoutUris.add(logoutUrisItem);
    return this;
  }

  /**
   * The application's logout uris.
   * @return logoutUris
  */
  
  @Schema(name = "logout_uris", description = "The application's logout uris.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logout_uris")
  public List<String> getLogoutUris() {
    return logoutUris;
  }

  public void setLogoutUris(List<String> logoutUris) {
    this.logoutUris = logoutUris;
  }

  public UpdateApplicationRequest redirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
    return this;
  }

  public UpdateApplicationRequest addRedirectUrisItem(String redirectUrisItem) {
    if (this.redirectUris == null) {
      this.redirectUris = new ArrayList<>();
    }
    this.redirectUris.add(redirectUrisItem);
    return this;
  }

  /**
   * The application's redirect uris.
   * @return redirectUris
  */
  
  @Schema(name = "redirect_uris", description = "The application's redirect uris.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("redirect_uris")
  public List<String> getRedirectUris() {
    return redirectUris;
  }

  public void setRedirectUris(List<String> redirectUris) {
    this.redirectUris = redirectUris;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateApplicationRequest updateApplicationRequest = (UpdateApplicationRequest) o;
    return Objects.equals(this.name, updateApplicationRequest.name) &&
        Objects.equals(this.languageKey, updateApplicationRequest.languageKey) &&
        Objects.equals(this.logoutUris, updateApplicationRequest.logoutUris) &&
        Objects.equals(this.redirectUris, updateApplicationRequest.redirectUris);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, languageKey, logoutUris, redirectUris);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateApplicationRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    languageKey: ").append(toIndentedString(languageKey)).append("\n");
    sb.append("    logoutUris: ").append(toIndentedString(logoutUris)).append("\n");
    sb.append("    redirectUris: ").append(toIndentedString(redirectUris)).append("\n");
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

