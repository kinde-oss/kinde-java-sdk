package com.kinde.accounts.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountDtoTest {

    @Test
    void entitlementDtoSupportsConstructionEqualityAndAccessors() {
        EntitlementDto dto = new EntitlementDto();
        dto.setKey("seats");
        dto.setName("Seats");
        dto.setDescription("Plan limit");
        dto.setType("integer");
        dto.setValue("5");
        dto.setOrgCode("org");
        dto.setPlans(List.of("pro"));

        EntitlementDto same = new EntitlementDto("seats", "Seats", "Plan limit", "integer", "5", "org", List.of("pro"));
        assertEquals(same, dto);
        assertEquals(same.hashCode(), dto.hashCode());
        assertEquals("seats", dto.getKey());
        assertEquals("Seats", dto.getName());
        assertEquals("Plan limit", dto.getDescription());
        assertEquals("integer", dto.getType());
        assertEquals("5", dto.getValue());
        assertEquals("org", dto.getOrgCode());
        assertEquals(List.of("pro"), dto.getPlans());
        assertTrue(dto.toString().contains("EntitlementDto"));
        assertNotEquals(dto, null);
        assertNotEquals(dto, "seats");
        assertNotEquals(dto, new EntitlementDto("different", "Seats", "Plan limit", "integer", "5", "org", List.of("pro")));
    }

    @Test
    void permissionDtoSupportsConstructionEqualityAndAccessors() {
        PermissionDto dto = new PermissionDto();
        dto.setId("permission-id");
        dto.setName("read");
        dto.setDescription("Read");
        dto.setKey("read");

        PermissionDto same = new PermissionDto("permission-id", "read", "Read", "read");
        assertEquals(same, dto);
        assertEquals(same.hashCode(), dto.hashCode());
        assertEquals("permission-id", dto.getId());
        assertEquals("read", dto.getName());
        assertEquals("Read", dto.getDescription());
        assertEquals("read", dto.getKey());
        assertTrue(dto.toString().contains("PermissionDto"));
        assertNotEquals(dto, null);
        assertNotEquals(dto, "read");
        assertNotEquals(dto, new PermissionDto("different", "read", "Read", "read"));
    }

    @Test
    void roleDtoSupportsConstructionEqualityAndAccessors() {
        RoleDto dto = new RoleDto();
        dto.setId("role-id");
        dto.setName("admin");
        dto.setDescription("Administrator");
        dto.setKey("admin");
        dto.setPermissions(List.of("read", "write"));

        RoleDto same = new RoleDto("role-id", "admin", "Administrator", "admin", List.of("read", "write"));
        assertEquals(same, dto);
        assertEquals(same.hashCode(), dto.hashCode());
        assertEquals("role-id", dto.getId());
        assertEquals("admin", dto.getName());
        assertEquals("Administrator", dto.getDescription());
        assertEquals("admin", dto.getKey());
        assertEquals(List.of("read", "write"), dto.getPermissions());
        assertTrue(dto.toString().contains("RoleDto"));
        assertNotEquals(dto, null);
        assertNotEquals(dto, "admin");
        assertNotEquals(dto, new RoleDto("different", "admin", "Administrator", "admin", List.of("read", "write")));
    }

    @Test
    void featureFlagDtoSupportsConstructionEqualityAndAccessors() {
        FeatureFlagDto dto = new FeatureFlagDto();
        dto.setKey("dark-mode");
        dto.setName("Dark mode");
        dto.setDescription("Theme flag");
        dto.setType("boolean");
        dto.setValue("true");
        dto.setEnabled(true);

        FeatureFlagDto same = new FeatureFlagDto("dark-mode", "Dark mode", "Theme flag", "boolean", "true", true);
        assertEquals(same, dto);
        assertEquals(same.hashCode(), dto.hashCode());
        assertEquals("dark-mode", dto.getKey());
        assertEquals("Dark mode", dto.getName());
        assertEquals("Theme flag", dto.getDescription());
        assertEquals("boolean", dto.getType());
        assertEquals("true", dto.getValue());
        assertTrue(dto.isEnabled());
        assertTrue(dto.toString().contains("FeatureFlagDto"));
        assertNotEquals(dto, null);
        assertNotEquals(dto, "dark-mode");
        assertNotEquals(dto, new FeatureFlagDto("dark-mode", "Dark mode", "Theme flag", "boolean", "true", false));
    }

    @Test
    void organizationDtoSupportsConstructionEqualityAndAccessors() {
        OrganizationDto dto = new OrganizationDto();
        dto.setId("org-id");
        dto.setName("Example");
        dto.setCode("example");
        dto.setType("business");
        dto.setDefault(true);

        OrganizationDto same = new OrganizationDto("org-id", "Example", "example", "business", true);
        assertEquals(same, dto);
        assertEquals(same.hashCode(), dto.hashCode());
        assertEquals("org-id", dto.getId());
        assertEquals("Example", dto.getName());
        assertEquals("example", dto.getCode());
        assertEquals("business", dto.getType());
        assertTrue(dto.isDefault());
        assertTrue(dto.toString().contains("OrganizationDto"));
        assertNotEquals(dto, null);
        assertNotEquals(dto, "org-id");
        assertNotEquals(dto, new OrganizationDto("org-id", "Example", "example", "business", false));
    }
}
