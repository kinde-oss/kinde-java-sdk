package com.kinde.accounts.manager.impl;

import com.kinde.KindeClientSession;
import com.kinde.accounts.dto.PermissionDto;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.ApiResponseHandlerImpl;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Permission;
import org.openapitools.client.model.PermissionResponse;
import org.openapitools.client.model.PermissionsResponse;

import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
        assertEquals("read-id", permissions.get(0).getId());
        PermissionDto result = manager.getPermissionAsync("read").join();
        assertEquals("read", result.getName());
        assertEquals("read-id", result.getKey());
        verify(apiClient, org.mockito.Mockito.times(2)).setBearerToken("access-token");
        verify(responseHandler).validateKey("read", "permission");
    }

    @Test
    void completesExceptionallyOnApiFailure() throws ApiException {
        DefaultApi api = mock(DefaultApi.class);
        ApiClient apiClient = mock(ApiClient.class);
        ApiResponseHandler responseHandler = spy(new ApiResponseHandlerImpl());
        KindeClientSession session = mock(KindeClientSession.class);
        when(session.getAccessToken()).thenReturn("");
        when(api.getApiClient()).thenReturn(apiClient);
        ApiException apiException = new ApiException("offline");
        when(api.getPermissions(null, 100)).thenThrow(apiException);

        PermissionsManagerImpl manager = new PermissionsManagerImpl(api, responseHandler, session);

        CompletionException exception = assertThrows(CompletionException.class,
                () -> manager.getAllPermissionsAsync().join());
        assertEquals("Failed to get permissions (status 0): " + apiException.getMessage(),
                exception.getCause().getMessage());
        assertSame(apiException, exception.getCause().getCause());
        verify(apiClient).setBearerToken((String) null);
        verify(responseHandler).handleApiException(apiException, "get permissions");
    }
}
