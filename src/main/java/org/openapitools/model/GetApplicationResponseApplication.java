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
 * GetApplicationResponseApplication
 */

@JsonTypeName("get_application_response_application")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class GetApplicationResponseApplication {

  private String id;

  private String name;

  private String type;

  private String clientId;

  private String clientSecret;

  public GetApplicationResponseApplication id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The application's identifier.
   * @return id
  */
  
  @Schema(name = "id", description = "The application's identifier.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GetApplicationResponseApplication name(String name) {
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

  public GetApplicationResponseApplication type(String type) {
    this.type = type;
    return this;
  }

  /**
   * The application's type.
   * @return type
  */
  
  @Schema(name = "type", description = "The application's type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public GetApplicationResponseApplication clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  /**
   * The application's client id.
   * @return clientId
  */
  
  @Schema(name = "client_id", description = "The application's client id.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_id")
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public GetApplicationResponseApplication clientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
    return this;
  }

  /**
   * The application's client secret.
   * @return clientSecret
  */
  
  @Schema(name = "client_secret", description = "The application's client secret.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_secret")
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetApplicationResponseApplication getApplicationResponseApplication = (GetApplicationResponseApplication) o;
    return Objects.equals(this.id, getApplicationResponseApplication.id) &&
        Objects.equals(this.name, getApplicationResponseApplication.name) &&
        Objects.equals(this.type, getApplicationResponseApplication.type) &&
        Objects.equals(this.clientId, getApplicationResponseApplication.clientId) &&
        Objects.equals(this.clientSecret, getApplicationResponseApplication.clientSecret);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, type, clientId, clientSecret);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetApplicationResponseApplication {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    clientId: ").append(toIndentedString(clientId)).append("\n");
    sb.append("    clientSecret: ").append(toIndentedString(clientSecret)).append("\n");
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

