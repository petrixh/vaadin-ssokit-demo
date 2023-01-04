package com.example.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;

public class VaadinOidcUserService extends OidcUserService {


    @Autowired(required = false)
    private VaadinSsoAuthoritiesMapper mapper;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        // A bit wasteful to call super and make the OidcUser...
        // However, the super of the super also does things and there's some OAuth2 stuff in
        // super as well...
        DefaultOidcUser oidcUser = (DefaultOidcUser) super.loadUser(userRequest);
        
        if (mapper != null) {
            Collection<? extends GrantedAuthority> grantedAuthorities = mapper.mapAuthorities(oidcUser.getAuthorities(), oidcUser.getUserInfo());
            oidcUser = new DefaultOidcUser(grantedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        }

        return oidcUser;
    }

    public interface VaadinSsoAuthoritiesMapper {

        Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities, OidcUserInfo userInfo);

    }


}
