package com.kinde.accounts.dto;

import java.util.Objects;

/**
 * DTO representing a feature flag from the Kinde Accounts API.
 * This class provides a clean interface for feature flag data without exposing OpenAPI internals.
 */
public class FeatureFlagDto {
    private String key;
    private String name;
    private String description;
    private String type;
    private String value;
    private boolean enabled;

    public FeatureFlagDto() {
    }

    public FeatureFlagDto(String key, String name, String description, String type, String value, boolean enabled) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureFlagDto that = (FeatureFlagDto) o;
        return enabled == that.enabled &&
                Objects.equals(key, that.key) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(type, that.type) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, description, type, value, enabled);
    }

    @Override
    public String toString() {
        return "FeatureFlagDto{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
