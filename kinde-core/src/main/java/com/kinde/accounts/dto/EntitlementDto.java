package com.kinde.accounts.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO representing an entitlement from the Kinde Accounts API.
 * This class provides a clean interface for entitlement data without exposing OpenAPI internals.
 */
public class EntitlementDto {
    private String key;
    private String name;
    private String description;
    private String type;
    private String value;
    private String orgCode;
    private List<String> plans;

    public EntitlementDto() {
    }

    public EntitlementDto(String key, String name, String description, String type, String value, String orgCode, List<String> plans) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.orgCode = orgCode;
        this.plans = plans;
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

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public List<String> getPlans() {
        return plans;
    }

    public void setPlans(List<String> plans) {
        this.plans = plans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitlementDto that = (EntitlementDto) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(type, that.type) &&
                Objects.equals(value, that.value) &&
                Objects.equals(orgCode, that.orgCode) &&
                Objects.equals(plans, that.plans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, description, type, value, orgCode, plans);
    }

    @Override
    public String toString() {
        return "EntitlementDto{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", plans=" + plans +
                '}';
    }
}
