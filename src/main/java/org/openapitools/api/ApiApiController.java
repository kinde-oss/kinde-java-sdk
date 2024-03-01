package org.openapitools.api;

import org.openapitools.model.AddOrganizationUsersRequest;
import org.openapitools.model.AddOrganizationUsersResponse;
import org.openapitools.model.ConnectedAppsAccessToken;
import org.openapitools.model.ConnectedAppsAuthUrl;
import org.openapitools.model.CreateApplicationRequest;
import org.openapitools.model.CreateApplicationResponse;
import org.openapitools.model.CreateFeatureFlagRequest;
import org.openapitools.model.CreateOrganizationRequest;
import org.openapitools.model.CreateOrganizationResponse;
import org.openapitools.model.CreateOrganizationUserRoleRequest;
import org.openapitools.model.CreatePermissionRequest;
import org.openapitools.model.CreateRoleRequest;
import org.openapitools.model.CreateSubscriberSuccessResponse;
import org.openapitools.model.CreateUserRequest;
import org.openapitools.model.CreateUserResponse;
import org.openapitools.model.ErrorResponse;
import org.openapitools.model.GetApplicationResponse;
import org.openapitools.model.GetApplicationsResponse;
import org.openapitools.model.GetEnvironmentFeatureFlagsResponse;
import org.openapitools.model.GetOrganizationFeatureFlagsResponse;
import org.openapitools.model.GetOrganizationUsersResponse;
import org.openapitools.model.GetOrganizationsResponse;
import org.openapitools.model.GetOrganizationsUserRolesResponse;
import org.openapitools.model.GetRolesResponse;
import org.openapitools.model.GetSubscriberResponse;
import org.openapitools.model.GetSubscribersResponse;
import org.openapitools.model.Organization;
import org.openapitools.model.RedirectCallbackUrls;
import org.openapitools.model.ReplaceRedirectCallbackURLsRequest;
import org.openapitools.model.SuccessResponse;
import org.openapitools.model.UpdateApplicationRequest;
import org.openapitools.model.UpdateEnvironementFeatureFlagOverrideRequest;
import org.openapitools.model.UpdateOrganizationRequest;
import org.openapitools.model.UpdateOrganizationUsersRequest;
import org.openapitools.model.UpdateOrganizationUsersResponse;
import org.openapitools.model.UpdateRolePermissionsRequest;
import org.openapitools.model.UpdateRolePermissionsResponse;
import org.openapitools.model.UpdateRolesRequest;
import org.openapitools.model.UpdateUserRequest;
import org.openapitools.model.User;
import org.openapitools.model.UsersResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.NativeWebRequest;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
@Controller
@RequestMapping("${openapi.kindeManagement.base-path:}")
public class ApiApiController implements ApiApi {

    private final NativeWebRequest request;

    @Autowired
    public ApiApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
