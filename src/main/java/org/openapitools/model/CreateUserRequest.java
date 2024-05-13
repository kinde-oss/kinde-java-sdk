package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.CreateUserRequestIdentitiesInner;
import org.openapitools.model.CreateUserRequestProfile;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreateUserRequest
 */

@JsonTypeName("createUser_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreateUserRequest {

  private CreateUserRequestProfile profile;

  @Valid
  private List<@Valid CreateUserRequestIdentitiesInner> identities;

  public CreateUserRequest profile(CreateUserRequestProfile profile) {
    this.profile = profile;
    return this;
  }

  /**
   * Get profile
   * @return profile
  */
  @Valid 
  @Schema(name = "profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profile")
  public CreateUserRequestProfile getProfile() {
    return profile;
  }

  public void setProfile(CreateUserRequestProfile profile) {
    this.profile = profile;
  }

  public CreateUserRequest identities(List<@Valid CreateUserRequestIdentitiesInner> identities) {
    this.identities = identities;
    return this;
  }

  public CreateUserRequest addIdentitiesItem(CreateUserRequestIdentitiesInner identitiesItem) {
    if (this.identities == null) {
      this.identities = new ArrayList<>();
    }
    this.identities.add(identitiesItem);
    return this;
  }

  /**
   * Array of identities to assign to the created user
   * @return identities
  */
  @Valid 
  @Schema(name = "identities", description = "Array of identities to assign to the created user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("identities")
  public List<@Valid CreateUserRequestIdentitiesInner> getIdentities() {
    return identities;
  }

  public void setIdentities(List<@Valid CreateUserRequestIdentitiesInner> identities) {
    this.identities = identities;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateUserRequest createUserRequest = (CreateUserRequest) o;
    return Objects.equals(this.profile, createUserRequest.profile) &&
        Objects.equals(this.identities, createUserRequest.identities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profile, identities);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateUserRequest {\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    identities: ").append(toIndentedString(identities)).append("\n");
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

