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
 * CreatePermissionRequest
 */

@JsonTypeName("CreatePermission_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreatePermissionRequest {

  private String name;

  private String description;

  private String key;

  public CreatePermissionRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The permission's name.
   * @return name
  */
  
  @Schema(name = "name", description = "The permission's name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CreatePermissionRequest description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The permission's description.
   * @return description
  */
  
  @Schema(name = "description", description = "The permission's description.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CreatePermissionRequest key(String key) {
    this.key = key;
    return this;
  }

  /**
   * The permission identifier to use in code.
   * @return key
  */
  
  @Schema(name = "key", description = "The permission identifier to use in code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("key")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreatePermissionRequest createPermissionRequest = (CreatePermissionRequest) o;
    return Objects.equals(this.name, createPermissionRequest.name) &&
        Objects.equals(this.description, createPermissionRequest.description) &&
        Objects.equals(this.key, createPermissionRequest.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, key);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreatePermissionRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
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

