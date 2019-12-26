package com.webapp.timeline.membership.security;

import com.webapp.timeline.membership.domain.Users;
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
        Users user = new Users();

        // create a JWT token
        String jwtToken = "some-token-123";

        return new JwtAuthenticationToken( jwtToken, user, new ArrayList<>() );

    }
    @Override
    public boolean supports( Class<?> authentication ) {
        return authentication.equals( UsernamePasswordAuthenticationToken.class );
    }
}
