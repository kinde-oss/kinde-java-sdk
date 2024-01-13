package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.UpdateRolePermissionsRequestPermissionsInner;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateRolePermissionsRequest
 */

@JsonTypeName("UpdateRolePermissions_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateRolePermissionsRequest {

  @Valid
  private List<@Valid UpdateRolePermissionsRequestPermissionsInner> permissions;

  public UpdateRolePermissionsRequest permissions(List<@Valid UpdateRolePermissionsRequestPermissionsInner> permissions) {
    this.permissions = permissions;
    return this;
  }

  public UpdateRolePermissionsRequest addPermissionsItem(UpdateRolePermissionsRequestPermissionsInner permissionsItem) {
    if (this.permissions == null) {
      this.permissions = new ArrayList<>();
    }
    this.permissions.add(permissionsItem);
    return this;
  }

  /**
   * Permissions to add or remove from the role.
   * @return permissions
  */
  @Valid 
  @Schema(name = "permissions", description = "Permissions to add or remove from the role.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("permissions")
  public List<@Valid UpdateRolePermissionsRequestPermissionsInner> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<@Valid UpdateRolePermissionsRequestPermissionsInner> permissions) {
    this.permissions = permissions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRolePermissionsRequest updateRolePermissionsRequest = (UpdateRolePermissionsRequest) o;
    return Objects.equals(this.permissions, updateRolePermissionsRequest.permissions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(permissions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRolePermissionsRequest {\n");
    sb.append("    permissions: ").append(toIndentedString(permissions)).append("\n");
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

