package com.kinde.accounts.dto;

import java.util.Objects;

/**
 * DTO representing an organization from the Kinde Accounts API.
 * This class provides a clean interface for organization data without exposing OpenAPI internals.
 */
public class OrganizationDto {
    private String id;
    private String name;
    private String code;
    private String type;
    private boolean isDefault;

    public OrganizationDto() {
    }

    public OrganizationDto(String id, String name, String code, String type, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.type = type;
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationDto that = (OrganizationDto) o;
        return isDefault == that.isDefault &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(code, that.code) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, type, isDefault);
    }

    @Override
    public String toString() {
        return "OrganizationDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
