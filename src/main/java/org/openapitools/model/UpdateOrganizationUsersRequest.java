package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.UpdateOrganizationUsersRequestUsersInner;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateOrganizationUsersRequest
 */

@JsonTypeName("UpdateOrganizationUsers_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateOrganizationUsersRequest {

  @Valid
  private List<@Valid UpdateOrganizationUsersRequestUsersInner> users;

  public UpdateOrganizationUsersRequest users(List<@Valid UpdateOrganizationUsersRequestUsersInner> users) {
    this.users = users;
    return this;
  }

  public UpdateOrganizationUsersRequest addUsersItem(UpdateOrganizationUsersRequestUsersInner usersItem) {
    if (this.users == null) {
      this.users = new ArrayList<>();
    }
    this.users.add(usersItem);
    return this;
  }

  /**
   * Users to add, update or remove from the organization.
   * @return users
  */
  @Valid 
  @Schema(name = "users", description = "Users to add, update or remove from the organization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("users")
  public List<@Valid UpdateOrganizationUsersRequestUsersInner> getUsers() {
    return users;
  }

  public void setUsers(List<@Valid UpdateOrganizationUsersRequestUsersInner> users) {
    this.users = users;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateOrganizationUsersRequest updateOrganizationUsersRequest = (UpdateOrganizationUsersRequest) o;
    return Objects.equals(this.users, updateOrganizationUsersRequest.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(users);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateOrganizationUsersRequest {\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
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

