package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateUserRequest
 */

@JsonTypeName("updateUser_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateUserRequest {

  private String givenName;

  private String familyName;

  private Boolean isSuspended;

  private Boolean isPasswordResetRequested;

  public UpdateUserRequest givenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

  /**
   * User's first name.
   * @return givenName
  */
  
  @Schema(name = "given_name", description = "User's first name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("given_name")
  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public UpdateUserRequest familyName(String familyName) {
    this.familyName = familyName;
    return this;
  }

  /**
   * User's last name.
   * @return familyName
  */
  
  @Schema(name = "family_name", description = "User's last name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("family_name")
  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public UpdateUserRequest isSuspended(Boolean isSuspended) {
    this.isSuspended = isSuspended;
    return this;
  }

  /**
   * Whether the user is currently suspended or not.
   * @return isSuspended
  */
  
  @Schema(name = "is_suspended", description = "Whether the user is currently suspended or not.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_suspended")
  public Boolean getIsSuspended() {
    return isSuspended;
  }

  public void setIsSuspended(Boolean isSuspended) {
    this.isSuspended = isSuspended;
  }

  public UpdateUserRequest isPasswordResetRequested(Boolean isPasswordResetRequested) {
    this.isPasswordResetRequested = isPasswordResetRequested;
    return this;
  }

  /**
   * Prompt the user to change their password on next sign in.
   * @return isPasswordResetRequested
  */
  
  @Schema(name = "is_password_reset_requested", description = "Prompt the user to change their password on next sign in.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_password_reset_requested")
  public Boolean getIsPasswordResetRequested() {
    return isPasswordResetRequested;
  }

  public void setIsPasswordResetRequested(Boolean isPasswordResetRequested) {
    this.isPasswordResetRequested = isPasswordResetRequested;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateUserRequest updateUserRequest = (UpdateUserRequest) o;
    return Objects.equals(this.givenName, updateUserRequest.givenName) &&
        Objects.equals(this.familyName, updateUserRequest.familyName) &&
        Objects.equals(this.isSuspended, updateUserRequest.isSuspended) &&
        Objects.equals(this.isPasswordResetRequested, updateUserRequest.isPasswordResetRequested);
  }

  @Override
  public int hashCode() {
    return Objects.hash(givenName, familyName, isSuspended, isPasswordResetRequested);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateUserRequest {\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
    sb.append("    isSuspended: ").append(toIndentedString(isSuspended)).append("\n");
    sb.append("    isPasswordResetRequested: ").append(toIndentedString(isPasswordResetRequested)).append("\n");
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

