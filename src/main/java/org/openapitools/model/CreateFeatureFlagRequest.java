package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreateFeatureFlagRequest
 */

@JsonTypeName("CreateFeatureFlag_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreateFeatureFlagRequest {

  private String name;

  private String description;

  private String key;

  /**
   * The variable type.
   */
  public enum TypeEnum {
    STR("str"),
    
    INT("int"),
    
    BOOL("bool");

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

  /**
   * Allow the flag to be overridden at a different level.
   */
  public enum AllowOverrideLevelEnum {
    ENV("env"),
    
    ORG("org");

    private String value;

    AllowOverrideLevelEnum(String value) {
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
    public static AllowOverrideLevelEnum fromValue(String value) {
      for (AllowOverrideLevelEnum b : AllowOverrideLevelEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private AllowOverrideLevelEnum allowOverrideLevel;

  private String defaultValue;

  /**
   * Default constructor
   * @deprecated Use {@link CreateFeatureFlagRequest#CreateFeatureFlagRequest(String, String, TypeEnum, String)}
   */
  @Deprecated
  public CreateFeatureFlagRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateFeatureFlagRequest(String name, String key, TypeEnum type, String defaultValue) {
    this.name = name;
    this.key = key;
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public CreateFeatureFlagRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the flag.
   * @return name
  */
  @NotNull 
  @Schema(name = "name", description = "The name of the flag.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CreateFeatureFlagRequest description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the flag purpose.
   * @return description
  */
  
  @Schema(name = "description", description = "Description of the flag purpose.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CreateFeatureFlagRequest key(String key) {
    this.key = key;
    return this;
  }

  /**
   * The flag identifier to use in code.
   * @return key
  */
  @NotNull 
  @Schema(name = "key", description = "The flag identifier to use in code.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("key")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public CreateFeatureFlagRequest type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * The variable type.
   * @return type
  */
  @NotNull 
  @Schema(name = "type", description = "The variable type.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public CreateFeatureFlagRequest allowOverrideLevel(AllowOverrideLevelEnum allowOverrideLevel) {
    this.allowOverrideLevel = allowOverrideLevel;
    return this;
  }

  /**
   * Allow the flag to be overridden at a different level.
   * @return allowOverrideLevel
  */
  
  @Schema(name = "allow_override_level", description = "Allow the flag to be overridden at a different level.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allow_override_level")
  public AllowOverrideLevelEnum getAllowOverrideLevel() {
    return allowOverrideLevel;
  }

  public void setAllowOverrideLevel(AllowOverrideLevelEnum allowOverrideLevel) {
    this.allowOverrideLevel = allowOverrideLevel;
  }

  public CreateFeatureFlagRequest defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  /**
   * Default value for the flag used by environments and organizations.
   * @return defaultValue
  */
  @NotNull 
  @Schema(name = "default_value", description = "Default value for the flag used by environments and organizations.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("default_value")
  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateFeatureFlagRequest createFeatureFlagRequest = (CreateFeatureFlagRequest) o;
    return Objects.equals(this.name, createFeatureFlagRequest.name) &&
        Objects.equals(this.description, createFeatureFlagRequest.description) &&
        Objects.equals(this.key, createFeatureFlagRequest.key) &&
        Objects.equals(this.type, createFeatureFlagRequest.type) &&
        Objects.equals(this.allowOverrideLevel, createFeatureFlagRequest.allowOverrideLevel) &&
        Objects.equals(this.defaultValue, createFeatureFlagRequest.defaultValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, key, type, allowOverrideLevel, defaultValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateFeatureFlagRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    allowOverrideLevel: ").append(toIndentedString(allowOverrideLevel)).append("\n");
    sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append("\n");
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

