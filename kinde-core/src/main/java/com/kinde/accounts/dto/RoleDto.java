package com.kinde.accounts.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO representing a role from the Kinde Accounts API.
 * This class provides a clean interface for role data without exposing OpenAPI internals.
 */
public class RoleDto {
    private String id;
    private String name;
    private String description;
    private String key;
    private List<String> permissions;

    public RoleDto() {
    }

    public RoleDto(String id, String name, String description, String key, List<String> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.key = key;
        this.permissions = permissions;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDto that = (RoleDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(key, that.key) &&
                Objects.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, key, permissions);
    }

    @Override
    public String toString() {
        return "RoleDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", key='" + key + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
