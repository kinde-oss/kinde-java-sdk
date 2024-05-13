package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.UserIdentity;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreateUserResponse
 */

@JsonTypeName("create_user_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreateUserResponse {

  private String id;

  private Boolean created;

  @Valid
  private List<@Valid UserIdentity> identities;

  public CreateUserResponse id(String id) {
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

  public CreateUserResponse created(Boolean created) {
    this.created = created;
    return this;
  }

  /**
   * True if the user was successfully created.
   * @return created
  */
  
  @Schema(name = "created", description = "True if the user was successfully created.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created")
  public Boolean getCreated() {
    return created;
  }

  public void setCreated(Boolean created) {
    this.created = created;
  }

  public CreateUserResponse identities(List<@Valid UserIdentity> identities) {
    this.identities = identities;
    return this;
  }

  public CreateUserResponse addIdentitiesItem(UserIdentity identitiesItem) {
    if (this.identities == null) {
      this.identities = new ArrayList<>();
    }
    this.identities.add(identitiesItem);
    return this;
  }

  /**
   * Get identities
   * @return identities
  */
  @Valid 
  @Schema(name = "identities", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("identities")
  public List<@Valid UserIdentity> getIdentities() {
    return identities;
  }

  public void setIdentities(List<@Valid UserIdentity> identities) {
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
    CreateUserResponse createUserResponse = (CreateUserResponse) o;
    return Objects.equals(this.id, createUserResponse.id) &&
        Objects.equals(this.created, createUserResponse.created) &&
        Objects.equals(this.identities, createUserResponse.identities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, created, identities);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateUserResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
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

