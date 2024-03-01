package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.Role;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetOrganizationsResponse
 */

@JsonTypeName("get_organizations_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class GetOrganizationsResponse {

  private String code;

  private String message;

  @Valid
  private List<@Valid Role> roles;

  private String nextToken;

  public GetOrganizationsResponse code(String code) {
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

  public GetOrganizationsResponse message(String message) {
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

  public GetOrganizationsResponse roles(List<@Valid Role> roles) {
    this.roles = roles;
    return this;
  }

  public GetOrganizationsResponse addRolesItem(Role rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * Get roles
   * @return roles
  */
  @Valid 
  @Schema(name = "roles", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roles")
  public List<@Valid Role> getRoles() {
    return roles;
  }

  public void setRoles(List<@Valid Role> roles) {
    this.roles = roles;
  }

  public GetOrganizationsResponse nextToken(String nextToken) {
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
    GetOrganizationsResponse getOrganizationsResponse = (GetOrganizationsResponse) o;
    return Objects.equals(this.code, getOrganizationsResponse.code) &&
        Objects.equals(this.message, getOrganizationsResponse.message) &&
        Objects.equals(this.roles, getOrganizationsResponse.roles) &&
        Objects.equals(this.nextToken, getOrganizationsResponse.nextToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, roles, nextToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetOrganizationsResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
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

