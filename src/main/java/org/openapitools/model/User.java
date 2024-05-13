package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Arrays;
import org.openapitools.jackson.nullable.JsonNullable;
import java.util.NoSuchElementException;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * User
 */

@JsonTypeName("user")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class User {

  private String id;

  private String providedId;

  private String email;

  private String lastName;

  private String firstName;

  private String fullName;

  private Boolean isSuspended;

  private String picture;

  private JsonNullable<Boolean> isPasswordResetRequested = JsonNullable.undefined();

  private JsonNullable<Integer> totalSignIns = JsonNullable.undefined();

  private JsonNullable<Integer> failedSignIns = JsonNullable.undefined();

  private JsonNullable<String> lastSignedIn = JsonNullable.undefined();

  private JsonNullable<String> createdOn = JsonNullable.undefined();

  public User id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique id of the user in Kinde.
   * @return id
  */
  
  @Schema(name = "id", description = "Unique id of the user in Kinde.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public User providedId(String providedId) {
    this.providedId = providedId;
    return this;
  }

  /**
   * External id for user.
   * @return providedId
  */
  
  @Schema(name = "provided_id", description = "External id for user.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("provided_id")
  public String getProvidedId() {
    return providedId;
  }

  public void setProvidedId(String providedId) {
    this.providedId = providedId;
  }

  public User email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Default email address of the user in Kinde.
   * @return email
  */
  
  @Schema(name = "email", description = "Default email address of the user in Kinde.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public User lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * User's last name.
   * @return lastName
  */
  
  @Schema(name = "last_name", description = "User's last name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("last_name")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public User firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * User's first name.
   * @return firstName
  */
  
  @Schema(name = "first_name", description = "User's first name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("first_name")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public User fullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  /**
   * User's full name.
   * @return fullName
  */
  
  @Schema(name = "full_name", description = "User's full name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("full_name")
  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public User isSuspended(Boolean isSuspended) {
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

  public User picture(String picture) {
    this.picture = picture;
    return this;
  }

  /**
   * User's profile picture URL.
   * @return picture
  */
  
  @Schema(name = "picture", description = "User's profile picture URL.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("picture")
  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public User isPasswordResetRequested(Boolean isPasswordResetRequested) {
    this.isPasswordResetRequested = JsonNullable.of(isPasswordResetRequested);
    return this;
  }

  /**
   * Whether the user has been asked to reset their password.
   * @return isPasswordResetRequested
  */
  
  @Schema(name = "is_password_reset_requested", description = "Whether the user has been asked to reset their password.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("is_password_reset_requested")
  public JsonNullable<Boolean> getIsPasswordResetRequested() {
    return isPasswordResetRequested;
  }

  public void setIsPasswordResetRequested(JsonNullable<Boolean> isPasswordResetRequested) {
    this.isPasswordResetRequested = isPasswordResetRequested;
  }

  public User totalSignIns(Integer totalSignIns) {
    this.totalSignIns = JsonNullable.of(totalSignIns);
    return this;
  }

  /**
   * Total number of user sign ins.
   * @return totalSignIns
  */
  
  @Schema(name = "total_sign_ins", description = "Total number of user sign ins.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_sign_ins")
  public JsonNullable<Integer> getTotalSignIns() {
    return totalSignIns;
  }

  public void setTotalSignIns(JsonNullable<Integer> totalSignIns) {
    this.totalSignIns = totalSignIns;
  }

  public User failedSignIns(Integer failedSignIns) {
    this.failedSignIns = JsonNullable.of(failedSignIns);
    return this;
  }

  /**
   * Number of consecutive failed user sign ins.
   * @return failedSignIns
  */
  
  @Schema(name = "failed_sign_ins", description = "Number of consecutive failed user sign ins.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("failed_sign_ins")
  public JsonNullable<Integer> getFailedSignIns() {
    return failedSignIns;
  }

  public void setFailedSignIns(JsonNullable<Integer> failedSignIns) {
    this.failedSignIns = failedSignIns;
  }

  public User lastSignedIn(String lastSignedIn) {
    this.lastSignedIn = JsonNullable.of(lastSignedIn);
    return this;
  }

  /**
   * Last sign in date in ISO 8601 format.
   * @return lastSignedIn
  */
  
  @Schema(name = "last_signed_in", description = "Last sign in date in ISO 8601 format.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("last_signed_in")
  public JsonNullable<String> getLastSignedIn() {
    return lastSignedIn;
  }

  public void setLastSignedIn(JsonNullable<String> lastSignedIn) {
    this.lastSignedIn = lastSignedIn;
  }

  public User createdOn(String createdOn) {
    this.createdOn = JsonNullable.of(createdOn);
    return this;
  }

  /**
   * Date of user creation in ISO 8601 format.
   * @return createdOn
  */
  
  @Schema(name = "created_on", description = "Date of user creation in ISO 8601 format.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created_on")
  public JsonNullable<String> getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(JsonNullable<String> createdOn) {
    this.createdOn = createdOn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(this.id, user.id) &&
        Objects.equals(this.providedId, user.providedId) &&
        Objects.equals(this.email, user.email) &&
        Objects.equals(this.lastName, user.lastName) &&
        Objects.equals(this.firstName, user.firstName) &&
        Objects.equals(this.fullName, user.fullName) &&
        Objects.equals(this.isSuspended, user.isSuspended) &&
        Objects.equals(this.picture, user.picture) &&
        equalsNullable(this.isPasswordResetRequested, user.isPasswordResetRequested) &&
        equalsNullable(this.totalSignIns, user.totalSignIns) &&
        equalsNullable(this.failedSignIns, user.failedSignIns) &&
        equalsNullable(this.lastSignedIn, user.lastSignedIn) &&
        equalsNullable(this.createdOn, user.createdOn);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, providedId, email, lastName, firstName, fullName, isSuspended, picture, hashCodeNullable(isPasswordResetRequested), hashCodeNullable(totalSignIns), hashCodeNullable(failedSignIns), hashCodeNullable(lastSignedIn), hashCodeNullable(createdOn));
  }

  private static <T> int hashCodeNullable(JsonNullable<T> a) {
    if (a == null) {
      return 1;
    }
    return a.isPresent() ? Arrays.deepHashCode(new Object[]{a.get()}) : 31;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    providedId: ").append(toIndentedString(providedId)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    fullName: ").append(toIndentedString(fullName)).append("\n");
    sb.append("    isSuspended: ").append(toIndentedString(isSuspended)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    isPasswordResetRequested: ").append(toIndentedString(isPasswordResetRequested)).append("\n");
    sb.append("    totalSignIns: ").append(toIndentedString(totalSignIns)).append("\n");
    sb.append("    failedSignIns: ").append(toIndentedString(failedSignIns)).append("\n");
    sb.append("    lastSignedIn: ").append(toIndentedString(lastSignedIn)).append("\n");
    sb.append("    createdOn: ").append(toIndentedString(createdOn)).append("\n");
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

