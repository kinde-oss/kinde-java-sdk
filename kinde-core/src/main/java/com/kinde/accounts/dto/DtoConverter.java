package com.kinde.accounts.dto;

import org.openapitools.client.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting OpenAPI response objects to clean DTOs.
 * This class encapsulates the conversion logic and keeps the OpenAPI internals hidden.
 */
public class DtoConverter {

    /**
     * Converts an OpenAPI Entitlement to EntitlementDto.
     */
    public static EntitlementDto toEntitlementDto(Entitlement entitlement) {
        if (entitlement == null) {
            return null;
        }
        
        EntitlementDto dto = new EntitlementDto();
        dto.setKey(entitlement.getFeatureKey());
        dto.setName(entitlement.getFeatureName());
        dto.setDescription(entitlement.getPriceName());
        dto.setType("entitlement");
        dto.setValue(entitlement.getEntitlementLimitMax() != null
                ? entitlement.getEntitlementLimitMax().toString()
                : null);
        dto.setOrgCode(null); // Not available in this structure
        dto.setPlans(null); // Not available in this structure
        
        return dto;
    }

    /**
     * Converts a list of OpenAPI Entitlements to EntitlementDtos.
     */
    public static List<EntitlementDto> toEntitlementDtos(List<Entitlement> entitlements) {
        if (entitlements == null) {
            return null;
        }
        
        return entitlements.stream()
                .map(DtoConverter::toEntitlementDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts an OpenAPI Permission to PermissionDto.
     */
    public static PermissionDto toPermissionDto(Permission permission) {
        if (permission == null) {
            return null;
        }
        
        PermissionDto dto = new PermissionDto();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        dto.setKey(permission.getId()); // Use ID as key since there's no separate key field
        
        return dto;
    }

    /**
     * Converts a list of OpenAPI Permissions to PermissionDtos.
     */
    public static List<PermissionDto> toPermissionDtos(List<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        
        return permissions.stream()
                .map(DtoConverter::toPermissionDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts an OpenAPI Role to RoleDto.
     */
    public static RoleDto toRoleDto(Role role) {
        if (role == null) {
            return null;
        }
        
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setKey(role.getId()); // Use ID as key since there's no separate key field
        dto.setPermissions(null); // Not available in this structure
        
        return dto;
    }

    /**
     * Converts a list of OpenAPI Roles to RoleDtos.
     */
    public static List<RoleDto> toRoleDtos(List<Role> roles) {
        if (roles == null) {
            return null;
        }
        
        return roles.stream()
                .map(DtoConverter::toRoleDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts an OpenAPI FeatureFlag to FeatureFlagDto.
     */
    public static FeatureFlagDto toFeatureFlagDto(FeatureFlag featureFlag) {
        if (featureFlag == null) {
            return null;
        }
        
        FeatureFlagDto dto = new FeatureFlagDto();
        dto.setKey(featureFlag.getKey());
        dto.setName(featureFlag.getName());
        dto.setDescription(null); // Not available in this structure
        dto.setType(featureFlag.getType());
        dto.setValue(featureFlag.getValue().toString());
        dto.setEnabled(true); // Assume enabled if present
        
        return dto;
    }

    /**
     * Converts a list of OpenAPI FeatureFlags to FeatureFlagDtos.
     */
    public static List<FeatureFlagDto> toFeatureFlagDtos(List<FeatureFlag> featureFlags) {
        if (featureFlags == null) {
            return null;
        }
        
        return featureFlags.stream()
                .map(DtoConverter::toFeatureFlagDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts an OpenAPI Organization to OrganizationDto.
     */
    public static OrganizationDto toOrganizationDto(Organization organization) {
        if (organization == null) {
            return null;
        }
        
        OrganizationDto dto = new OrganizationDto();
        dto.setId(organization.getId());
        dto.setName(organization.getName());
        dto.setCode(organization.getCode());
        dto.setType(null); // Not available in this structure
        dto.setDefault(false); // Not available in this structure
        
        return dto;
    }

    /**
     * Converts a list of OpenAPI Organizations to OrganizationDtos.
     */
    public static List<OrganizationDto> toOrganizationDtos(List<Organization> organizations) {
        if (organizations == null) {
            return null;
        }
        
        return organizations.stream()
                .map(DtoConverter::toOrganizationDto)
                .collect(Collectors.toList());
    }
}
