package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;

final class KindeJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public KindeJwtAuthenticationConverter(String groupClaim) {
        JwtGrantedAuthoritiesConverter originalConverter = new JwtGrantedAuthoritiesConverter();
        this.setJwtGrantedAuthoritiesConverter(source -> {
            Collection<GrantedAuthority> result = originalConverter.convert(source);
            result.addAll(TokenUtil.tokenClaimsToAuthorities(source.getClaims(), groupClaim));
            return result;
        });
    }

    public KindeJwtAuthenticationConverter(KindeOAuth2Properties kindeOAuth2Properties) {
        JwtGrantedAuthoritiesConverter originalConverter = new JwtGrantedAuthoritiesConverter();

        this.setJwtGrantedAuthoritiesConverter(source -> {
            Collection<GrantedAuthority> result = originalConverter.convert(source);
            result.addAll(TokenUtil.tokenClaimsToAuthorities(source.getClaims(), kindeOAuth2Properties.getPermissionsClaim()));
            return result;
        });
    }
}