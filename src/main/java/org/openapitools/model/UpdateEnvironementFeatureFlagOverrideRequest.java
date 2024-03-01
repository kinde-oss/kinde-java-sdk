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
 * UpdateEnvironementFeatureFlagOverrideRequest
 */

@JsonTypeName("UpdateEnvironementFeatureFlagOverride_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateEnvironementFeatureFlagOverrideRequest {

  private String value;

  /**
   * Default constructor
   * @deprecated Use {@link UpdateEnvironementFeatureFlagOverrideRequest#UpdateEnvironementFeatureFlagOverrideRequest(String)}
   */
  @Deprecated
  public UpdateEnvironementFeatureFlagOverrideRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateEnvironementFeatureFlagOverrideRequest(String value) {
    this.value = value;
  }

  public UpdateEnvironementFeatureFlagOverrideRequest value(String value) {
    this.value = value;
    return this;
  }

  /**
   * The flag override value.
   * @return value
  */
  @NotNull 
  @Schema(name = "value", description = "The flag override value.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateEnvironementFeatureFlagOverrideRequest updateEnvironementFeatureFlagOverrideRequest = (UpdateEnvironementFeatureFlagOverrideRequest) o;
    return Objects.equals(this.value, updateEnvironementFeatureFlagOverrideRequest.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateEnvironementFeatureFlagOverrideRequest {\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

