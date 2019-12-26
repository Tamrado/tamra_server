package com.webapp.timeline.membership.security;

import com.webapp.timeline.membership.domain.Users;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    Users principal;
    String token;

    public JwtAuthenticationToken( String token, Users principal, Collection<? extends GrantedAuthority> authorities ) {
        super( authorities );
        this.token = token;
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public void setToken( String token ) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
