package com.kinde.accounts.manager.impl;

import com.kinde.accounts.dto.RoleDto;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Role;
import org.openapitools.client.model.RolesResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
        assertEquals("role-id", roles.getFirst().getId());
        assertEquals("admin", roles.getFirst().getName());
        assertEquals("role-id", roles.getFirst().getKey());
    }

    @Test
    void reportsApiErrorsAndReturnsEmptyResult() throws ApiException {
        DefaultApi api = mock(DefaultApi.class);
        ApiResponseHandler responseHandler = mock(ApiResponseHandler.class);
        when(api.getRoles(null, 100)).thenThrow(new ApiException("offline"));

        RolesManagerImpl manager = new RolesManagerImpl(api, mock(PaginationHelper.class), responseHandler);

        assertTrue(manager.getAllRolesAsync().join().isEmpty());
        verify(responseHandler).handleApiException(any(ApiException.class), eq("get roles"));
    }
}
