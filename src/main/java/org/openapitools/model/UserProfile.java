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
 * UserProfile
 */

@JsonTypeName("user_profile")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UserProfile {

  private String id;

  private String preferredEmail;

  private JsonNullable<String> providedId = JsonNullable.undefined();

  private String lastName;

  private String firstName;

  private String picture;

  public UserProfile id(String id) {
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

  public UserProfile preferredEmail(String preferredEmail) {
    this.preferredEmail = preferredEmail;
    return this;
  }

  /**
   * Default email address of the user in Kinde.
   * @return preferredEmail
  */
  
  @Schema(name = "preferred_email", description = "Default email address of the user in Kinde.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferred_email")
  public String getPreferredEmail() {
    return preferredEmail;
  }

  public void setPreferredEmail(String preferredEmail) {
    this.preferredEmail = preferredEmail;
  }

  public UserProfile providedId(String providedId) {
    this.providedId = JsonNullable.of(providedId);
    return this;
  }

  /**
   * Value of the user's id in a third-party system when the user is imported into Kinde.
   * @return providedId
  */
  
  @Schema(name = "provided_id", description = "Value of the user's id in a third-party system when the user is imported into Kinde.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("provided_id")
  public JsonNullable<String> getProvidedId() {
    return providedId;
  }

  public void setProvidedId(JsonNullable<String> providedId) {
    this.providedId = providedId;
  }

  public UserProfile lastName(String lastName) {
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

  public UserProfile firstName(String firstName) {
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

  public UserProfile picture(String picture) {
    this.picture = picture;
    return this;
  }

  /**
   * URL that point's to the user's picture or avatar
   * @return picture
  */
  
  @Schema(name = "picture", description = "URL that point's to the user's picture or avatar", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("picture")
  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserProfile userProfile = (UserProfile) o;
    return Objects.equals(this.id, userProfile.id) &&
        Objects.equals(this.preferredEmail, userProfile.preferredEmail) &&
        equalsNullable(this.providedId, userProfile.providedId) &&
        Objects.equals(this.lastName, userProfile.lastName) &&
        Objects.equals(this.firstName, userProfile.firstName) &&
        Objects.equals(this.picture, userProfile.picture);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, preferredEmail, hashCodeNullable(providedId), lastName, firstName, picture);
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
    sb.append("class UserProfile {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    preferredEmail: ").append(toIndentedString(preferredEmail)).append("\n");
    sb.append("    providedId: ").append(toIndentedString(providedId)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
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

