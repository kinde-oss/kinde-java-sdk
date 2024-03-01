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
 * UpdateRolePermissionsRequestPermissionsInner
 */

@JsonTypeName("UpdateRolePermissions_request_permissions_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateRolePermissionsRequestPermissionsInner {

  private String id;

  private String operation;

  public UpdateRolePermissionsRequestPermissionsInner id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The permission id.
   * @return id
  */
  
  @Schema(name = "id", description = "The permission id.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UpdateRolePermissionsRequestPermissionsInner operation(String operation) {
    this.operation = operation;
    return this;
  }

  /**
   * Optional operation, set to 'delete' to remove the permission from the role.
   * @return operation
  */
  
  @Schema(name = "operation", description = "Optional operation, set to 'delete' to remove the permission from the role.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operation")
  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRolePermissionsRequestPermissionsInner updateRolePermissionsRequestPermissionsInner = (UpdateRolePermissionsRequestPermissionsInner) o;
    return Objects.equals(this.id, updateRolePermissionsRequestPermissionsInner.id) &&
        Objects.equals(this.operation, updateRolePermissionsRequestPermissionsInner.operation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, operation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRolePermissionsRequestPermissionsInner {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
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

