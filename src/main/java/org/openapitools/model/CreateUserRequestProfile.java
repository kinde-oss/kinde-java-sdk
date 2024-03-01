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
 * Basic information required to create a user.
 */

@Schema(name = "createUser_request_profile", description = "Basic information required to create a user.")
@JsonTypeName("createUser_request_profile")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreateUserRequestProfile {

  private String givenName;

  private String familyName;

  public CreateUserRequestProfile givenName(String givenName) {
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

  public CreateUserRequestProfile familyName(String familyName) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateUserRequestProfile createUserRequestProfile = (CreateUserRequestProfile) o;
    return Objects.equals(this.givenName, createUserRequestProfile.givenName) &&
        Objects.equals(this.familyName, createUserRequestProfile.familyName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(givenName, familyName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateUserRequestProfile {\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
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

