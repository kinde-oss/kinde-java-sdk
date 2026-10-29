package com.kinde.accounts.manager.impl;

import com.kinde.accounts.dto.RoleDto;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.ApiResponseHandlerImpl;
import com.kinde.accounts.util.PaginationHelper;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Role;
import org.openapitools.client.model.RolesResponse;

import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RolesManagerImplTest {

    @Test
    void convertsAllRolesFromApiResponse() throws ApiException {
        DefaultApi api = mock(DefaultApi.class);
        ApiResponseHandler responseHandler = mock(ApiResponseHandler.class);
        RolesResponse response = mock(RolesResponse.class);
        Role role = new Role().id("role-id").name("admin").description("Administrator");
        when(api.getRoles(null, 100)).thenReturn(response);
        when(response.getData()).thenReturn(List.of(role));
        when(response.getMetadata()).thenReturn(null);

        RolesManagerImpl manager = new RolesManagerImpl(api, mock(PaginationHelper.class), responseHandler);
        List<RoleDto> roles = manager.getAllRolesAsync().join();

        assertEquals(1, roles.size());
        assertEquals("role-id", roles.get(0).getId());
        assertEquals("admin", roles.get(0).getName());
        assertEquals("role-id", roles.get(0).getKey());
    }

    @Test
    void completesExceptionallyOnApiFailure() throws ApiException {
        DefaultApi api = mock(DefaultApi.class);
        ApiResponseHandler responseHandler = spy(new ApiResponseHandlerImpl());
        ApiException apiException = new ApiException("offline");
        when(api.getRoles(null, 100)).thenThrow(apiException);

        RolesManagerImpl manager = new RolesManagerImpl(api, mock(PaginationHelper.class), responseHandler);

        CompletionException exception = assertThrows(CompletionException.class,
                () -> manager.getAllRolesAsync().join());
        assertEquals("Failed to get roles (status 0): " + apiException.getMessage(),
                exception.getCause().getMessage());
        assertSame(apiException, exception.getCause().getCause());
        verify(responseHandler).handleApiException(apiException, "get roles");
    }
}
