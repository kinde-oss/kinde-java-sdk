package com.kinde.spring;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers.withPkce;

/**
 * Custom OAuth2 authorization request resolver that adds invitation code support
 * on top of the default PKCE-enabled resolver. When the originating HTTP request
 * contains an {@code invitation_code} parameter, this resolver appends
 * {@code invitation_code} and {@code is_invitation=true} to the authorization
 * request's additional parameters.
 */
public class KindeOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String INVITATION_CODE_PARAM = "invitation_code";
    private static final String IS_INVITATION_PARAM = "is_invitation";

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
        return addInvitationParameters(request, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return addInvitationParameters(request, authorizationRequest);
    }

    private OAuth2AuthorizationRequest addInvitationParameters(
            HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }
        String invitationCode = request.getParameter(INVITATION_CODE_PARAM);
        if (invitationCode == null || invitationCode.isEmpty()) {
            return authorizationRequest;
        }
        Map<String, Object> additionalParams = new HashMap<>(authorizationRequest.getAdditionalParameters());
        additionalParams.put(INVITATION_CODE_PARAM, invitationCode);
        additionalParams.put(IS_INVITATION_PARAM, "true");
        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParams)
                .build();
    }
}
