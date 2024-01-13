package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.AddOrganizationUsersRequestUsersInner;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AddOrganizationUsersRequest
 */

@JsonTypeName("AddOrganizationUsers_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class AddOrganizationUsersRequest {

  @Valid
  private List<@Valid AddOrganizationUsersRequestUsersInner> users;

  public AddOrganizationUsersRequest users(List<@Valid AddOrganizationUsersRequestUsersInner> users) {
    this.users = users;
    return this;
  }

  public AddOrganizationUsersRequest addUsersItem(AddOrganizationUsersRequestUsersInner usersItem) {
    if (this.users == null) {
      this.users = new ArrayList<>();
    }
    this.users.add(usersItem);
    return this;
  }

  /**
   * Users to be added to the organization.
   * @return users
  */
  @Valid 
  @Schema(name = "users", description = "Users to be added to the organization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("users")
  public List<@Valid AddOrganizationUsersRequestUsersInner> getUsers() {
    return users;
  }

  public void setUsers(List<@Valid AddOrganizationUsersRequestUsersInner> users) {
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
    AddOrganizationUsersRequest addOrganizationUsersRequest = (AddOrganizationUsersRequest) o;
    return Objects.equals(this.users, addOrganizationUsersRequest.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(users);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddOrganizationUsersRequest {\n");
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

