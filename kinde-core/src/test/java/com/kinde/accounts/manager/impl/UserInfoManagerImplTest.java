package com.kinde.accounts.manager.impl;

import com.kinde.accounts.dto.OrganizationDto;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.CurrentOrganizationResponse;
import org.openapitools.client.model.Organization;
import org.openapitools.client.model.UserOrganizationsResponse;
import org.openapitools.client.model.UserProfile;
import org.openapitools.client.model.UserProfileResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserInfoManagerImplTest {

    @Test
    void retrievesOrganizationsProfileAndCurrentOrganization() throws ApiException {
        DefaultApi api = mock(DefaultApi.class);
        UserOrganizationsResponse organizationsResponse = mock(UserOrganizationsResponse.class);
        UserProfileResponse profileResponse = mock(UserProfileResponse.class);
        CurrentOrganizationResponse currentOrganization = mock(CurrentOrganizationResponse.class);
        UserProfile profile = mock(UserProfile.class);
        Organization organization = new Organization()
                .id("org-id")
                .name("Example")
                .code("example");
        when(api.getUserOrganizations(null, 100)).thenReturn(organizationsResponse);
        when(organizationsResponse.getData()).thenReturn(List.of(organization));
        when(organizationsResponse.getMetadata()).thenReturn(null);
        when(api.getUserProfile()).thenReturn(profileResponse);
        when(profileResponse.getData()).thenReturn(profile);
        when(api.getCurrentOrganization()).thenReturn(currentOrganization);

        UserInfoManagerImpl manager = new UserInfoManagerImpl(
                api, mock(PaginationHelper.class), mock(ApiResponseHandler.class));

        List<OrganizationDto> organizations = manager.getUserOrganizationsAsync().join();
        assertEquals(1, organizations.size());
        assertEquals("org-id", organizations.getFirst().getId());
        assertEquals("Example", organizations.getFirst().getName());
        assertEquals("example", organizations.getFirst().getCode());
        assertSame(profile, manager.getUserProfileAsync().join());
        assertSame(currentOrganization, manager.getCurrentOrganizationAsync().join());
    }
}
