package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.OrganizationUser;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetOrganizationUsersResponse
 */

@JsonTypeName("get_organization_users_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class GetOrganizationUsersResponse {

  private String code;

  private String message;

  @Valid
  private List<@Valid OrganizationUser> organizationUsers;

  private String nextToken;

  public GetOrganizationUsersResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Response code.
   * @return code
  */
  
  @Schema(name = "code", description = "Response code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public GetOrganizationUsersResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Response message.
   * @return message
  */
  
  @Schema(name = "message", description = "Response message.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public GetOrganizationUsersResponse organizationUsers(List<@Valid OrganizationUser> organizationUsers) {
    this.organizationUsers = organizationUsers;
    return this;
  }

  public GetOrganizationUsersResponse addOrganizationUsersItem(OrganizationUser organizationUsersItem) {
    if (this.organizationUsers == null) {
      this.organizationUsers = new ArrayList<>();
    }
    this.organizationUsers.add(organizationUsersItem);
    return this;
  }

  /**
   * Get organizationUsers
   * @return organizationUsers
  */
  @Valid 
  @Schema(name = "organization_users", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("organization_users")
  public List<@Valid OrganizationUser> getOrganizationUsers() {
    return organizationUsers;
  }

  public void setOrganizationUsers(List<@Valid OrganizationUser> organizationUsers) {
    this.organizationUsers = organizationUsers;
  }

  public GetOrganizationUsersResponse nextToken(String nextToken) {
    this.nextToken = nextToken;
    return this;
  }

  /**
   * Pagination token.
   * @return nextToken
  */
  
  @Schema(name = "next_token", description = "Pagination token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("next_token")
  public String getNextToken() {
    return nextToken;
  }

  public void setNextToken(String nextToken) {
    this.nextToken = nextToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetOrganizationUsersResponse getOrganizationUsersResponse = (GetOrganizationUsersResponse) o;
    return Objects.equals(this.code, getOrganizationUsersResponse.code) &&
        Objects.equals(this.message, getOrganizationUsersResponse.message) &&
        Objects.equals(this.organizationUsers, getOrganizationUsersResponse.organizationUsers) &&
        Objects.equals(this.nextToken, getOrganizationUsersResponse.nextToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, organizationUsers, nextToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetOrganizationUsersResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    organizationUsers: ").append(toIndentedString(organizationUsers)).append("\n");
    sb.append("    nextToken: ").append(toIndentedString(nextToken)).append("\n");
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

