package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.User;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UsersResponse
 */

@JsonTypeName("users_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UsersResponse {

  private String code;

  private String message;

  @Valid
  private List<@Valid User> users;

  private String nextToken;

  public UsersResponse code(String code) {
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

  public UsersResponse message(String message) {
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

  public UsersResponse users(List<@Valid User> users) {
    this.users = users;
    return this;
  }

  public UsersResponse addUsersItem(User usersItem) {
    if (this.users == null) {
      this.users = new ArrayList<>();
    }
    this.users.add(usersItem);
    return this;
  }

  /**
   * Get users
   * @return users
  */
  @Valid 
  @Schema(name = "users", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("users")
  public List<@Valid User> getUsers() {
    return users;
  }

  public void setUsers(List<@Valid User> users) {
    this.users = users;
  }

  public UsersResponse nextToken(String nextToken) {
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
    UsersResponse usersResponse = (UsersResponse) o;
    return Objects.equals(this.code, usersResponse.code) &&
        Objects.equals(this.message, usersResponse.message) &&
        Objects.equals(this.users, usersResponse.users) &&
        Objects.equals(this.nextToken, usersResponse.nextToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, users, nextToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UsersResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
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

