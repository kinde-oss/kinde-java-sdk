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
 * UpdateRolePermissionsResponse
 */

@JsonTypeName("update_role_permissions_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateRolePermissionsResponse {

  private String code;

  private String message;

  @Valid
  private List<String> permissionsAdded;

  @Valid
  private List<String> permissionsRemoved;

  public UpdateRolePermissionsResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public UpdateRolePermissionsResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  */
  
  @Schema(name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public UpdateRolePermissionsResponse permissionsAdded(List<String> permissionsAdded) {
    this.permissionsAdded = permissionsAdded;
    return this;
  }

  public UpdateRolePermissionsResponse addPermissionsAddedItem(String permissionsAddedItem) {
    if (this.permissionsAdded == null) {
      this.permissionsAdded = new ArrayList<>();
    }
    this.permissionsAdded.add(permissionsAddedItem);
    return this;
  }

  /**
   * Get permissionsAdded
   * @return permissionsAdded
  */
  
  @Schema(name = "permissions_added", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("permissions_added")
  public List<String> getPermissionsAdded() {
    return permissionsAdded;
  }

  public void setPermissionsAdded(List<String> permissionsAdded) {
    this.permissionsAdded = permissionsAdded;
  }

  public UpdateRolePermissionsResponse permissionsRemoved(List<String> permissionsRemoved) {
    this.permissionsRemoved = permissionsRemoved;
    return this;
  }

  public UpdateRolePermissionsResponse addPermissionsRemovedItem(String permissionsRemovedItem) {
    if (this.permissionsRemoved == null) {
      this.permissionsRemoved = new ArrayList<>();
    }
    this.permissionsRemoved.add(permissionsRemovedItem);
    return this;
  }

  /**
   * Get permissionsRemoved
   * @return permissionsRemoved
  */
  
  @Schema(name = "permissions_removed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("permissions_removed")
  public List<String> getPermissionsRemoved() {
    return permissionsRemoved;
  }

  public void setPermissionsRemoved(List<String> permissionsRemoved) {
    this.permissionsRemoved = permissionsRemoved;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRolePermissionsResponse updateRolePermissionsResponse = (UpdateRolePermissionsResponse) o;
    return Objects.equals(this.code, updateRolePermissionsResponse.code) &&
        Objects.equals(this.message, updateRolePermissionsResponse.message) &&
        Objects.equals(this.permissionsAdded, updateRolePermissionsResponse.permissionsAdded) &&
        Objects.equals(this.permissionsRemoved, updateRolePermissionsResponse.permissionsRemoved);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, permissionsAdded, permissionsRemoved);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRolePermissionsResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    permissionsAdded: ").append(toIndentedString(permissionsAdded)).append("\n");
    sb.append("    permissionsRemoved: ").append(toIndentedString(permissionsRemoved)).append("\n");
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

