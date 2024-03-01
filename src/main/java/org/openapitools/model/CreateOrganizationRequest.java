package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreateOrganizationRequest
 */

@JsonTypeName("createOrganization_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreateOrganizationRequest {

  private String name;

  /**
   * Value of the feature flag.
   */
  public enum InnerEnum {
    STR("str"),
    
    INT("int"),
    
    BOOL("bool");

    private String value;

    InnerEnum(String value) {
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
    public static InnerEnum fromValue(String value) {
      for (InnerEnum b : InnerEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Valid
  private Map<String, InnerEnum> featureFlags = new HashMap<>();

  private String externalId;

  private String backgroundColor;

  private String buttonColor;

  private String buttonTextColor;

  private String linkColor;

  public CreateOrganizationRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The organization's name.
   * @return name
  */
  
  @Schema(name = "name", description = "The organization's name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CreateOrganizationRequest featureFlags(Map<String, InnerEnum> featureFlags) {
    this.featureFlags = featureFlags;
    return this;
  }

  public CreateOrganizationRequest putFeatureFlagsItem(String key, InnerEnum featureFlagsItem) {
    if (this.featureFlags == null) {
      this.featureFlags = new HashMap<>();
    }
    this.featureFlags.put(key, featureFlagsItem);
    return this;
  }

  /**
   * The organization's feature flag settings.
   * @return featureFlags
  */
  
  @Schema(name = "feature_flags", description = "The organization's feature flag settings.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("feature_flags")
  public Map<String, InnerEnum> getFeatureFlags() {
    return featureFlags;
  }

  public void setFeatureFlags(Map<String, InnerEnum> featureFlags) {
    this.featureFlags = featureFlags;
  }

  public CreateOrganizationRequest externalId(String externalId) {
    this.externalId = externalId;
    return this;
  }

  /**
   * The organization's ID.
   * @return externalId
  */
  
  @Schema(name = "external_id", description = "The organization's ID.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("external_id")
  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public CreateOrganizationRequest backgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
    return this;
  }

  /**
   * The organization's brand settings - background color.
   * @return backgroundColor
  */
  
  @Schema(name = "background_color", description = "The organization's brand settings - background color.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("background_color")
  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public CreateOrganizationRequest buttonColor(String buttonColor) {
    this.buttonColor = buttonColor;
    return this;
  }

  /**
   * The organization's brand settings - button color.
   * @return buttonColor
  */
  
  @Schema(name = "button_color", description = "The organization's brand settings - button color.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("button_color")
  public String getButtonColor() {
    return buttonColor;
  }

  public void setButtonColor(String buttonColor) {
    this.buttonColor = buttonColor;
  }

  public CreateOrganizationRequest buttonTextColor(String buttonTextColor) {
    this.buttonTextColor = buttonTextColor;
    return this;
  }

  /**
   * The organization's brand settings - button text color.
   * @return buttonTextColor
  */
  
  @Schema(name = "button_text_color", description = "The organization's brand settings - button text color.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("button_text_color")
  public String getButtonTextColor() {
    return buttonTextColor;
  }

  public void setButtonTextColor(String buttonTextColor) {
    this.buttonTextColor = buttonTextColor;
  }

  public CreateOrganizationRequest linkColor(String linkColor) {
    this.linkColor = linkColor;
    return this;
  }

  /**
   * The organization's brand settings - link color.
   * @return linkColor
  */
  
  @Schema(name = "link_color", description = "The organization's brand settings - link color.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("link_color")
  public String getLinkColor() {
    return linkColor;
  }

  public void setLinkColor(String linkColor) {
    this.linkColor = linkColor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateOrganizationRequest createOrganizationRequest = (CreateOrganizationRequest) o;
    return Objects.equals(this.name, createOrganizationRequest.name) &&
        Objects.equals(this.featureFlags, createOrganizationRequest.featureFlags) &&
        Objects.equals(this.externalId, createOrganizationRequest.externalId) &&
        Objects.equals(this.backgroundColor, createOrganizationRequest.backgroundColor) &&
        Objects.equals(this.buttonColor, createOrganizationRequest.buttonColor) &&
        Objects.equals(this.buttonTextColor, createOrganizationRequest.buttonTextColor) &&
        Objects.equals(this.linkColor, createOrganizationRequest.linkColor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, featureFlags, externalId, backgroundColor, buttonColor, buttonTextColor, linkColor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateOrganizationRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    featureFlags: ").append(toIndentedString(featureFlags)).append("\n");
    sb.append("    externalId: ").append(toIndentedString(externalId)).append("\n");
    sb.append("    backgroundColor: ").append(toIndentedString(backgroundColor)).append("\n");
    sb.append("    buttonColor: ").append(toIndentedString(buttonColor)).append("\n");
    sb.append("    buttonTextColor: ").append(toIndentedString(buttonTextColor)).append("\n");
    sb.append("    linkColor: ").append(toIndentedString(linkColor)).append("\n");
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

