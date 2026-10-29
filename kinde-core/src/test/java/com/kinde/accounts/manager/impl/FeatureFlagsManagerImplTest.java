package com.kinde.accounts.manager.impl;

import com.kinde.accounts.dto.FeatureFlagDto;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.FeatureFlag;
import org.openapitools.client.model.FeatureFlagResponse;
import org.openapitools.client.model.FeatureFlagValue;
import org.openapitools.client.model.FeatureFlagsResponse;
import org.openapitools.client.model.FeatureFlagsResponseData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeatureFlagsManagerImplTest {

    @Test
    void listsAndRetrievesFeatureFlags() throws ApiException {
        DefaultApi api = mock(DefaultApi.class);
        ApiResponseHandler responseHandler = mock(ApiResponseHandler.class);
        FeatureFlagsResponse listResponse = mock(FeatureFlagsResponse.class);
        FeatureFlagsResponseData listData = mock(FeatureFlagsResponseData.class);
        FeatureFlagResponse singleResponse = mock(FeatureFlagResponse.class);
        FeatureFlag flag = new FeatureFlag()
                .key("new-dashboard")
                .name("New dashboard")
                .type("boolean")
                .value(new FeatureFlagValue(Boolean.TRUE));
        when(api.getFeatureFlags(null, 100)).thenReturn(listResponse);
        when(listResponse.getData()).thenReturn(listData);
        when(listData.getFeatureFlags()).thenReturn(List.of(flag));
        when(listResponse.getMetadata()).thenReturn(null);
        when(api.getFeatureFlag("new-dashboard")).thenReturn(singleResponse);
        when(singleResponse.getData()).thenReturn(flag);

        FeatureFlagsManagerImpl manager = new FeatureFlagsManagerImpl(
                api, mock(PaginationHelper.class), responseHandler);

        List<FeatureFlagDto> flags = manager.getAllFeatureFlagsAsync().join();
        assertEquals(1, flags.size());
        assertEquals("new-dashboard", flags.get(0).getKey());
        assertEquals("New dashboard", flags.get(0).getName());
        assertTrue(flags.get(0).isEnabled());
        assertEquals("true", flags.get(0).getValue());
        FeatureFlagDto result = manager.getFeatureFlagAsync("new-dashboard").join();
        assertEquals("boolean", result.getType());
        assertEquals("true", result.getValue());
        verify(responseHandler).validateKey("new-dashboard", "feature flag");
    }
}
