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
 * UpdateOrganizationUsersResponse
 */

@JsonTypeName("update_organization_users_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateOrganizationUsersResponse {

  private String message;

  @Valid
  private List<String> usersAdded;

  @Valid
  private List<String> usersUpdated;

  @Valid
  private List<String> usersRemoved;

  public UpdateOrganizationUsersResponse message(String message) {
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

  public UpdateOrganizationUsersResponse usersAdded(List<String> usersAdded) {
    this.usersAdded = usersAdded;
    return this;
  }

  public UpdateOrganizationUsersResponse addUsersAddedItem(String usersAddedItem) {
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

  public UpdateOrganizationUsersResponse usersUpdated(List<String> usersUpdated) {
    this.usersUpdated = usersUpdated;
    return this;
  }

  public UpdateOrganizationUsersResponse addUsersUpdatedItem(String usersUpdatedItem) {
    if (this.usersUpdated == null) {
      this.usersUpdated = new ArrayList<>();
    }
    this.usersUpdated.add(usersUpdatedItem);
    return this;
  }

  /**
   * Get usersUpdated
   * @return usersUpdated
  */
  
  @Schema(name = "users_updated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("users_updated")
  public List<String> getUsersUpdated() {
    return usersUpdated;
  }

  public void setUsersUpdated(List<String> usersUpdated) {
    this.usersUpdated = usersUpdated;
  }

  public UpdateOrganizationUsersResponse usersRemoved(List<String> usersRemoved) {
    this.usersRemoved = usersRemoved;
    return this;
  }

  public UpdateOrganizationUsersResponse addUsersRemovedItem(String usersRemovedItem) {
    if (this.usersRemoved == null) {
      this.usersRemoved = new ArrayList<>();
    }
    this.usersRemoved.add(usersRemovedItem);
    return this;
  }

  /**
   * Get usersRemoved
   * @return usersRemoved
  */
  
  @Schema(name = "users_removed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("users_removed")
  public List<String> getUsersRemoved() {
    return usersRemoved;
  }

  public void setUsersRemoved(List<String> usersRemoved) {
    this.usersRemoved = usersRemoved;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateOrganizationUsersResponse updateOrganizationUsersResponse = (UpdateOrganizationUsersResponse) o;
    return Objects.equals(this.message, updateOrganizationUsersResponse.message) &&
        Objects.equals(this.usersAdded, updateOrganizationUsersResponse.usersAdded) &&
        Objects.equals(this.usersUpdated, updateOrganizationUsersResponse.usersUpdated) &&
        Objects.equals(this.usersRemoved, updateOrganizationUsersResponse.usersRemoved);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, usersAdded, usersUpdated, usersRemoved);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateOrganizationUsersResponse {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    usersAdded: ").append(toIndentedString(usersAdded)).append("\n");
    sb.append("    usersUpdated: ").append(toIndentedString(usersUpdated)).append("\n");
    sb.append("    usersRemoved: ").append(toIndentedString(usersRemoved)).append("\n");
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

