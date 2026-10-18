package com.kinde.accounts.manager.impl;

import com.kinde.KindeClientSession;
import com.kinde.accounts.dto.PermissionDto;
import com.kinde.accounts.util.ApiResponseHandler;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Permission;
import org.openapitools.client.model.PermissionResponse;
import org.openapitools.client.model.PermissionsResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermissionsManagerImplTest {

    @Test
    void listsAndRetrievesPermissionsUsingSessionToken() throws ApiException {
        DefaultApi api = mock(DefaultApi.class);
        ApiClient apiClient = mock(ApiClient.class);
        ApiResponseHandler responseHandler = mock(ApiResponseHandler.class);
        KindeClientSession session = mock(KindeClientSession.class);
        PermissionsResponse listResponse = mock(PermissionsResponse.class);
        PermissionResponse singleResponse = mock(PermissionResponse.class);
        Permission permission = new Permission().id("read-id").name("read").description("Read data");
        when(session.getAccessToken()).thenReturn("access-token");
        when(api.getApiClient()).thenReturn(apiClient);
        when(api.getPermissions(null, 100)).thenReturn(listResponse);
        when(listResponse.getData()).thenReturn(List.of(permission));
        when(listResponse.getMetadata()).thenReturn(null);
        when(api.getPermission("read")).thenReturn(singleResponse);
        when(singleResponse.getData()).thenReturn(permission);

        PermissionsManagerImpl manager = new PermissionsManagerImpl(api, responseHandler, session);

        List<PermissionDto> permissions = manager.getAllPermissionsAsync().join();
        assertEquals("read-id", permissions.getFirst().getId());
        PermissionDto result = manager.getPermissionAsync("read").join();
        assertEquals("read", result.getName());
        assertEquals("read-id", result.getKey());
        verify(apiClient, org.mockito.Mockito.times(2)).setBearerToken("access-token");
        verify(responseHandler).validateKey("read", "permission");
    }

    @Test
    void convertsApiFailureToEmptyList() throws ApiException {
        DefaultApi api = mock(DefaultApi.class);
        ApiClient apiClient = mock(ApiClient.class);
        ApiResponseHandler responseHandler = mock(ApiResponseHandler.class);
        KindeClientSession session = mock(KindeClientSession.class);
        when(session.getAccessToken()).thenReturn("");
        when(api.getApiClient()).thenReturn(apiClient);
        when(api.getPermissions(null, 100)).thenThrow(new ApiException("offline"));

        PermissionsManagerImpl manager = new PermissionsManagerImpl(api, responseHandler, session);

        assertEquals(List.of(), manager.getAllPermissionsAsync().join());
        verify(apiClient).setBearerToken((String) null);
        verify(responseHandler).handleApiException(any(ApiException.class), eq("get permissions"));
    }
}
