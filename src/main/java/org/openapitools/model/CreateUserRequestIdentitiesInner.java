package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.model.CreateUserRequestIdentitiesInnerDetails;
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

@Schema(name = "createUser_request_identities_inner", description = "The result of the user creation operation.")
@JsonTypeName("createUser_request_identities_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreateUserRequestIdentitiesInner {

  /**
   * The type of identity to create, for e.g. email.
   */
  public enum TypeEnum {
    EMAIL("email");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TypeEnum type;

  private CreateUserRequestIdentitiesInnerDetails details;

  public CreateUserRequestIdentitiesInner type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * The type of identity to create, for e.g. email.
   * @return type
  */
  
  @Schema(name = "type", description = "The type of identity to create, for e.g. email.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public CreateUserRequestIdentitiesInner details(CreateUserRequestIdentitiesInnerDetails details) {
    this.details = details;
    return this;
  }

  /**
   * Get details
   * @return details
  */
  @Valid 
  @Schema(name = "details", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("details")
  public CreateUserRequestIdentitiesInnerDetails getDetails() {
    return details;
  }

  public void setDetails(CreateUserRequestIdentitiesInnerDetails details) {
    this.details = details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateUserRequestIdentitiesInner createUserRequestIdentitiesInner = (CreateUserRequestIdentitiesInner) o;
    return Objects.equals(this.type, createUserRequestIdentitiesInner.type) &&
        Objects.equals(this.details, createUserRequestIdentitiesInner.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateUserRequestIdentitiesInner {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

