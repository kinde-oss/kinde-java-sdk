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
 * The result of the user creation operation.
 */

@Schema(name = "user_identity_result", description = "The result of the user creation operation.")
@JsonTypeName("user_identity_result")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UserIdentityResult {

  private Boolean created;

  public UserIdentityResult created(Boolean created) {
    this.created = created;
    return this;
  }

  /**
   * True if the user identity was successfully created.
   * @return created
  */
  
  @Schema(name = "created", description = "True if the user identity was successfully created.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created")
  public Boolean getCreated() {
    return created;
  }

  public void setCreated(Boolean created) {
    this.created = created;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserIdentityResult userIdentityResult = (UserIdentityResult) o;
    return Objects.equals(this.created, userIdentityResult.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(created);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserIdentityResult {\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
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

