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
 * UpdateOrganizationUsersRequestUsersInner
 */

@JsonTypeName("UpdateOrganizationUsers_request_users_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateOrganizationUsersRequestUsersInner {

  private String id;

  private String operation;

  @Valid
  private List<String> roles;

  @Valid
  private List<String> permissions;

  public UpdateOrganizationUsersRequestUsersInner id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The users id.
   * @return id
  */
  
  @Schema(name = "id", description = "The users id.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UpdateOrganizationUsersRequestUsersInner operation(String operation) {
    this.operation = operation;
    return this;
  }

  /**
   * Optional operation, set to 'delete' to remove the user from the organization.
   * @return operation
  */
  
  @Schema(name = "operation", description = "Optional operation, set to 'delete' to remove the user from the organization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operation")
  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public UpdateOrganizationUsersRequestUsersInner roles(List<String> roles) {
    this.roles = roles;
    return this;
  }

  public UpdateOrganizationUsersRequestUsersInner addRolesItem(String rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * Role keys to assign to the user.
   * @return roles
  */
  
  @Schema(name = "roles", description = "Role keys to assign to the user.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roles")
  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public UpdateOrganizationUsersRequestUsersInner permissions(List<String> permissions) {
    this.permissions = permissions;
    return this;
  }

  public UpdateOrganizationUsersRequestUsersInner addPermissionsItem(String permissionsItem) {
    if (this.permissions == null) {
      this.permissions = new ArrayList<>();
    }
    this.permissions.add(permissionsItem);
    return this;
  }

  /**
   * Permission keys to assign to the user.
   * @return permissions
  */
  
  @Schema(name = "permissions", description = "Permission keys to assign to the user.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("permissions")
  public List<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<String> permissions) {
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
    UpdateOrganizationUsersRequestUsersInner updateOrganizationUsersRequestUsersInner = (UpdateOrganizationUsersRequestUsersInner) o;
    return Objects.equals(this.id, updateOrganizationUsersRequestUsersInner.id) &&
        Objects.equals(this.operation, updateOrganizationUsersRequestUsersInner.operation) &&
        Objects.equals(this.roles, updateOrganizationUsersRequestUsersInner.roles) &&
        Objects.equals(this.permissions, updateOrganizationUsersRequestUsersInner.permissions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, operation, roles, permissions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateOrganizationUsersRequestUsersInner {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
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

