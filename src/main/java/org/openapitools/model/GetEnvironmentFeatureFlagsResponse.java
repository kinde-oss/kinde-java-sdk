package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.HashMap;
import java.util.Map;
import org.openapitools.model.GetOrganizationFeatureFlagsResponseFeatureFlagsValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetEnvironmentFeatureFlagsResponse
 */

@JsonTypeName("get_environment_feature_flags_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class GetEnvironmentFeatureFlagsResponse {

  private String code;

  private String message;

  @Valid
  private Map<String, GetOrganizationFeatureFlagsResponseFeatureFlagsValue> featureFlags = new HashMap<>();

  private String nextToken;

  public GetEnvironmentFeatureFlagsResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Response code.
   * @return code
  */
  
  @Schema(name = "code", description = "Response code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public GetEnvironmentFeatureFlagsResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Response message.
   * @return message
  */
  
  @Schema(name = "message", description = "Response message.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public GetEnvironmentFeatureFlagsResponse featureFlags(Map<String, GetOrganizationFeatureFlagsResponseFeatureFlagsValue> featureFlags) {
    this.featureFlags = featureFlags;
    return this;
  }

  public GetEnvironmentFeatureFlagsResponse putFeatureFlagsItem(String key, GetOrganizationFeatureFlagsResponseFeatureFlagsValue featureFlagsItem) {
    if (this.featureFlags == null) {
      this.featureFlags = new HashMap<>();
    }
    this.featureFlags.put(key, featureFlagsItem);
    return this;
  }

  /**
   * The environment's feature flag settings.
   * @return featureFlags
  */
  @Valid 
  @Schema(name = "feature_flags", description = "The environment's feature flag settings.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("feature_flags")
  public Map<String, GetOrganizationFeatureFlagsResponseFeatureFlagsValue> getFeatureFlags() {
    return featureFlags;
  }

  public void setFeatureFlags(Map<String, GetOrganizationFeatureFlagsResponseFeatureFlagsValue> featureFlags) {
    this.featureFlags = featureFlags;
  }

  public GetEnvironmentFeatureFlagsResponse nextToken(String nextToken) {
    this.nextToken = nextToken;
    return this;
  }

  /**
   * Pagination token.
   * @return nextToken
  */
  
  @Schema(name = "next_token", description = "Pagination token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("next_token")
  public String getNextToken() {
    return nextToken;
  }

  public void setNextToken(String nextToken) {
    this.nextToken = nextToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetEnvironmentFeatureFlagsResponse getEnvironmentFeatureFlagsResponse = (GetEnvironmentFeatureFlagsResponse) o;
    return Objects.equals(this.code, getEnvironmentFeatureFlagsResponse.code) &&
        Objects.equals(this.message, getEnvironmentFeatureFlagsResponse.message) &&
        Objects.equals(this.featureFlags, getEnvironmentFeatureFlagsResponse.featureFlags) &&
        Objects.equals(this.nextToken, getEnvironmentFeatureFlagsResponse.nextToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, featureFlags, nextToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetEnvironmentFeatureFlagsResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    featureFlags: ").append(toIndentedString(featureFlags)).append("\n");
    sb.append("    nextToken: ").append(toIndentedString(nextToken)).append("\n");
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

