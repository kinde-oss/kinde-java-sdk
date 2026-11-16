package com.kinde.spring;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.kinde.session.KindeRequestParameters;
import com.kinde.stepup.StepUp;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers.withPkce;

/**
 * Custom OAuth2 authorization request resolver that adds Kinde-specific
 * authorize URL parameters on top of the default PKCE-enabled resolver.
 * When the originating HTTP request contains an {@code invitation_code}
 * parameter, this resolver appends {@code invitation_code} and
 * {@code is_invitation=true}. When it contains a {@code connection_id}
 * parameter, that value is forwarded so Kinde can skip the identity picker.
 *
 * <p>For step-up authentication (RFC 9470), {@code acr_values} ({@code mfa} or {@code phr})
 * and {@code max_age} (seconds) are validated and forwarded, so
 * {@code /oauth2/authorization/kinde?acr_values=mfa&max_age=300} asks an already signed-in
 * user for a stronger or more recent authentication. Invalid values are rejected with an
 * {@link IllegalArgumentException}: Kinde would silently ignore an unknown {@code acr_values}
 * and read a malformed {@code max_age} as "re-authenticate".
 */
public class KindeOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public KindeOAuth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository,
            String authorizationRequestBaseUri) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, authorizationRequestBaseUri);
        this.defaultResolver.setAuthorizationRequestCustomizer(withPkce());
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        return addKindeParameters(request, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return addKindeParameters(request, authorizationRequest);
    }

    private OAuth2AuthorizationRequest addKindeParameters(
            HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest) {
        return withKindeParameters(authorizationRequest, request::getParameter);
    }

    /**
     * Adds the Kinde-specific parameters found on the originating request to an authorization
     * request. Shared with the reactive resolver.
     *
     * @param requestParameter looks up a query parameter of the originating request
     * @throws IllegalArgumentException if {@code acr_values} or {@code max_age} is invalid
     */
    static OAuth2AuthorizationRequest withKindeParameters(
            OAuth2AuthorizationRequest authorizationRequest, Function<String, String> requestParameter) {
        if (authorizationRequest == null) {
            return null;
        }
        String invitationCode = requestParameter.apply(KindeRequestParameters.INVITATION_CODE);
        String connectionId = requestParameter.apply(KindeRequestParameters.CONNECTION_ID);
        String acrValuesParam = requestParameter.apply(KindeRequestParameters.ACR_VALUES);
        String acrValues = acrValuesParam == null || acrValuesParam.isBlank()
                ? null : StepUp.normalizeAcrValues(acrValuesParam);
        String maxAge = StepUp.normalizeMaxAgeParameter(requestParameter.apply(KindeRequestParameters.MAX_AGE));
        boolean hasInvitation = invitationCode != null && !invitationCode.isBlank();
        boolean hasConnectionId = connectionId != null && !connectionId.isBlank();
        if (!hasInvitation && !hasConnectionId && acrValues == null && maxAge == null) {
            return authorizationRequest;
        }
        Map<String, Object> additionalParams = new HashMap<>(authorizationRequest.getAdditionalParameters());
        if (hasInvitation) {
            additionalParams.put(KindeRequestParameters.INVITATION_CODE, invitationCode);
            additionalParams.put(KindeRequestParameters.IS_INVITATION, "true");
        }
        if (hasConnectionId) {
            additionalParams.put(KindeRequestParameters.CONNECTION_ID, connectionId.trim());
        }
        if (acrValues != null) {
            additionalParams.put(KindeRequestParameters.ACR_VALUES, acrValues);
        }
        if (maxAge != null) {
            additionalParams.put(KindeRequestParameters.MAX_AGE, maxAge);
        }
        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParams)
                .build();
    }
}
