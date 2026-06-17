package com.epam.learn.ss.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class JwtRealmRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Optional<Object> rolesClaim = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
            .map(stringObjectMap -> stringObjectMap.get("roles"));

        if (rolesClaim.isEmpty()) {
            return List.of();
        }

        if (!(rolesClaim.get() instanceof Collection<?> roles)) {
            return List.of();
        }

        return roles.stream()
            .map(String.class::cast)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .map(GrantedAuthority.class::cast)
            .toList();
    }

}
