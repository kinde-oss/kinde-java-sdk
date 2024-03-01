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
 * UserProfileV2
 */

@JsonTypeName("user_profile_v2")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UserProfileV2 {

  private String id;

  private String sub;

  private JsonNullable<String> providedId = JsonNullable.undefined();

  private String name;

  private String givenName;

  private String familyName;

  private Integer updatedAt;

  private String email;

  private String picture;

  public UserProfileV2 id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique id of the user in Kinde (deprecated).
   * @return id
  */
  
  @Schema(name = "id", description = "Unique id of the user in Kinde (deprecated).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserProfileV2 sub(String sub) {
    this.sub = sub;
    return this;
  }

  /**
   * Unique id of the user in Kinde.
   * @return sub
  */
  
  @Schema(name = "sub", description = "Unique id of the user in Kinde.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sub")
  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public UserProfileV2 providedId(String providedId) {
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

  public UserProfileV2 name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Users's first and last name separated by a space.
   * @return name
  */
  
  @Schema(name = "name", description = "Users's first and last name separated by a space.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserProfileV2 givenName(String givenName) {
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

  public UserProfileV2 familyName(String familyName) {
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

  public UserProfileV2 updatedAt(Integer updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Date the user was last updated at (In Unix time).
   * @return updatedAt
  */
  
  @Schema(name = "updated_at", description = "Date the user was last updated at (In Unix time).", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updated_at")
  public Integer getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Integer updatedAt) {
    this.updatedAt = updatedAt;
  }

  public UserProfileV2 email(String email) {
    this.email = email;
    return this;
  }

  /**
   * User's email address if available.
   * @return email
  */
  
  @Schema(name = "email", description = "User's email address if available.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserProfileV2 picture(String picture) {
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
    UserProfileV2 userProfileV2 = (UserProfileV2) o;
    return Objects.equals(this.id, userProfileV2.id) &&
        Objects.equals(this.sub, userProfileV2.sub) &&
        equalsNullable(this.providedId, userProfileV2.providedId) &&
        Objects.equals(this.name, userProfileV2.name) &&
        Objects.equals(this.givenName, userProfileV2.givenName) &&
        Objects.equals(this.familyName, userProfileV2.familyName) &&
        Objects.equals(this.updatedAt, userProfileV2.updatedAt) &&
        Objects.equals(this.email, userProfileV2.email) &&
        Objects.equals(this.picture, userProfileV2.picture);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sub, hashCodeNullable(providedId), name, givenName, familyName, updatedAt, email, picture);
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
    sb.append("class UserProfileV2 {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    sub: ").append(toIndentedString(sub)).append("\n");
    sb.append("    providedId: ").append(toIndentedString(providedId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
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

