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
 * UpdateOrganizationRequest
 */

@JsonTypeName("updateOrganization_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class UpdateOrganizationRequest {

  private String name;

  private String externalId;

  private String backgroundColor;

  private String buttonColor;

  private String buttonTextColor;

  private String linkColor;

  public UpdateOrganizationRequest name(String name) {
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

  public UpdateOrganizationRequest externalId(String externalId) {
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

  public UpdateOrganizationRequest backgroundColor(String backgroundColor) {
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

  public UpdateOrganizationRequest buttonColor(String buttonColor) {
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

  public UpdateOrganizationRequest buttonTextColor(String buttonTextColor) {
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

  public UpdateOrganizationRequest linkColor(String linkColor) {
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
    UpdateOrganizationRequest updateOrganizationRequest = (UpdateOrganizationRequest) o;
    return Objects.equals(this.name, updateOrganizationRequest.name) &&
        Objects.equals(this.externalId, updateOrganizationRequest.externalId) &&
        Objects.equals(this.backgroundColor, updateOrganizationRequest.backgroundColor) &&
        Objects.equals(this.buttonColor, updateOrganizationRequest.buttonColor) &&
        Objects.equals(this.buttonTextColor, updateOrganizationRequest.buttonTextColor) &&
        Objects.equals(this.linkColor, updateOrganizationRequest.linkColor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, externalId, backgroundColor, buttonColor, buttonTextColor, linkColor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateOrganizationRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

