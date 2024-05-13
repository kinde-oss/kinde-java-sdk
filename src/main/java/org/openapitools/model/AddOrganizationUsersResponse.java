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
 * AddOrganizationUsersResponse
 */

@JsonTypeName("add_organization_users_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class AddOrganizationUsersResponse {

  private String code;

  private String message;

  @Valid
  private List<String> usersAdded;

  public AddOrganizationUsersResponse code(String code) {
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

  public AddOrganizationUsersResponse message(String message) {
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

  public AddOrganizationUsersResponse usersAdded(List<String> usersAdded) {
    this.usersAdded = usersAdded;
    return this;
  }

  public AddOrganizationUsersResponse addUsersAddedItem(String usersAddedItem) {
    if (this.usersAdded == null) {
      this.usersAdded = new ArrayList<>();
    }
    this.usersAdded.add(usersAddedItem);
    return this;
  }

  /**
   * Get usersAdded
   * @return usersAdded
  */
  
  @Schema(name = "users_added", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("users_added")
  public List<String> getUsersAdded() {
    return usersAdded;
  }

  public void setUsersAdded(List<String> usersAdded) {
    this.usersAdded = usersAdded;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddOrganizationUsersResponse addOrganizationUsersResponse = (AddOrganizationUsersResponse) o;
    return Objects.equals(this.code, addOrganizationUsersResponse.code) &&
        Objects.equals(this.message, addOrganizationUsersResponse.message) &&
        Objects.equals(this.usersAdded, addOrganizationUsersResponse.usersAdded);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, usersAdded);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddOrganizationUsersResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    usersAdded: ").append(toIndentedString(usersAdded)).append("\n");
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

