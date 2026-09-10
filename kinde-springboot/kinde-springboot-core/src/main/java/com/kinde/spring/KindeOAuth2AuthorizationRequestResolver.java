package com.kinde.spring;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.kinde.session.KindeRequestParameters;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers.withPkce;

/**
 * Custom OAuth2 authorization request resolver that adds Kinde-specific
 * authorize URL parameters on top of the default PKCE-enabled resolver.
 * When the originating HTTP request contains an {@code invitation_code}
 * parameter, this resolver appends {@code invitation_code} and
 * {@code is_invitation=true}. When it contains a {@code connection_id}
 * parameter, that value is forwarded so Kinde can skip the identity picker.
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
        if (authorizationRequest == null) {
            return null;
        }
        String invitationCode = request.getParameter(KindeRequestParameters.INVITATION_CODE);
        String connectionId = request.getParameter(KindeRequestParameters.CONNECTION_ID);
        boolean hasInvitation = invitationCode != null && !invitationCode.isBlank();
        boolean hasConnectionId = connectionId != null && !connectionId.isBlank();
        if (!hasInvitation && !hasConnectionId) {
            return authorizationRequest;
        }
        Map<String, Object> additionalParams = new HashMap<>(authorizationRequest.getAdditionalParameters());
        if (hasInvitation) {
            additionalParams.put(KindeRequestParameters.INVITATION_CODE, invitationCode);
            additionalParams.put(KindeRequestParameters.IS_INVITATION, "true");
        }
        if (hasConnectionId && connectionId != null) {
            additionalParams.put(KindeRequestParameters.CONNECTION_ID, connectionId.trim());
        }
        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParams)
                .build();
    }
}
