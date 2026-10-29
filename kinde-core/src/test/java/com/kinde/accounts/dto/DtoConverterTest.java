package com.kinde.accounts.dto;

import org.junit.jupiter.api.Test;
import org.openapitools.client.model.Entitlement;
import org.openapitools.client.model.FeatureFlag;
import org.openapitools.client.model.FeatureFlagValue;
import org.openapitools.client.model.Organization;
import org.openapitools.client.model.Permission;
import org.openapitools.client.model.Role;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoConverterTest {

    @Test
    void convertsEntitlementsAndHandlesNullValues() {
        Entitlement entitlement = new Entitlement();
        entitlement.setFeatureKey("seats");
        entitlement.setFeatureName("Seats");
        entitlement.setPriceName("Pro");
        entitlement.setEntitlementLimitMax(new BigDecimal("12"));

        EntitlementDto dto = DtoConverter.toEntitlementDto(entitlement);

        assertEquals("seats", dto.getKey());
        assertEquals("Seats", dto.getName());
        assertEquals("Pro", dto.getDescription());
        assertEquals("entitlement", dto.getType());
        assertEquals("12", dto.getValue());
        assertNull(dto.getOrgCode());
        assertNull(dto.getPlans());
        assertNull(DtoConverter.toEntitlementDto(null));
        assertNull(DtoConverter.toEntitlementDtos(null));
        assertEquals(2, DtoConverter.toEntitlementDtos(java.util.Arrays.asList(entitlement, null)).size());
        assertNull(DtoConverter.toEntitlementDtos(java.util.Arrays.asList(entitlement, null)).get(1));
    }

    @Test
    void convertsPermissionsAndRoles() {
        Permission permission = new Permission();
        permission.setId("perm-id");
        permission.setName("read");
        permission.setDescription("Read data");

        PermissionDto permissionDto = DtoConverter.toPermissionDto(permission);
        assertEquals("perm-id", permissionDto.getId());
        assertEquals("perm-id", permissionDto.getKey());
        assertEquals("read", permissionDto.getName());
        assertEquals("Read data", permissionDto.getDescription());
        assertNull(DtoConverter.toPermissionDto(null));
        assertNull(DtoConverter.toPermissionDtos(null));
        assertEquals("read", DtoConverter.toPermissionDtos(List.of(permission)).get(0).getName());

        Role role = new Role();
        role.setId("role-id");
        role.setName("admin");
        role.setDescription("Administrator");

        RoleDto roleDto = DtoConverter.toRoleDto(role);
        assertEquals("role-id", roleDto.getId());
        assertEquals("role-id", roleDto.getKey());
        assertEquals("admin", roleDto.getName());
        assertEquals("Administrator", roleDto.getDescription());
        assertNull(roleDto.getPermissions());
        assertNull(DtoConverter.toRoleDto(null));
        assertNull(DtoConverter.toRoleDtos(null));
        assertEquals("admin", DtoConverter.toRoleDtos(List.of(role)).get(0).getName());
    }

    @Test
    void convertsFeatureFlagsAndOrganizations() {
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setKey("dark-mode");
        featureFlag.setName("Dark mode");
        featureFlag.setType("boolean");
        featureFlag.setValue(new FeatureFlagValue(Boolean.TRUE));

        FeatureFlagDto featureFlagDto = DtoConverter.toFeatureFlagDto(featureFlag);
        assertEquals("dark-mode", featureFlagDto.getKey());
        assertEquals("Dark mode", featureFlagDto.getName());
        assertEquals("boolean", featureFlagDto.getType());
        assertEquals("true", featureFlagDto.getValue());
        assertTrue(featureFlagDto.isEnabled());
        assertNull(featureFlagDto.getDescription());
        assertNull(DtoConverter.toFeatureFlagDto(null));
        assertNull(DtoConverter.toFeatureFlagDtos(null));
        assertEquals("dark-mode", DtoConverter.toFeatureFlagDtos(List.of(featureFlag)).get(0).getKey());

        FeatureFlag withoutValue = new FeatureFlag();
        assertNull(DtoConverter.toFeatureFlagDto(withoutValue).getValue());

        Organization organization = new Organization();
        organization.setId("org-id");
        organization.setName("Example");
        organization.setCode("example");

        OrganizationDto organizationDto = DtoConverter.toOrganizationDto(organization);
        assertEquals("org-id", organizationDto.getId());
        assertEquals("Example", organizationDto.getName());
        assertEquals("example", organizationDto.getCode());
        assertNull(organizationDto.getType());
        assertFalse(organizationDto.isDefault());
        assertNull(DtoConverter.toOrganizationDto(null));
        assertNull(DtoConverter.toOrganizationDtos(null));
        assertEquals("org-id", DtoConverter.toOrganizationDtos(List.of(organization)).get(0).getId());
    }

    @Test
    void convertsUnderlyingFeatureFlagValuesWithoutWrapperMetadata() {
        FeatureFlag featureFlag = new FeatureFlag();

        featureFlag.setValue(new FeatureFlagValue(Boolean.FALSE));
        assertEquals("false", DtoConverter.toFeatureFlagDto(featureFlag).getValue());

        featureFlag.setValue(new FeatureFlagValue("enabled"));
        assertEquals("enabled", DtoConverter.toFeatureFlagDto(featureFlag).getValue());

        featureFlag.setValue(new FeatureFlagValue(new BigDecimal("42")));
        assertEquals("42", DtoConverter.toFeatureFlagDto(featureFlag).getValue());
    }

    @Test
    void preservesNullForFeatureFlagWrapperWithoutAnActualValue() {
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setValue(new FeatureFlagValue());

        assertNull(DtoConverter.toFeatureFlagDto(featureFlag).getValue());
    }
}
