package com.example.application;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VaadinOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        // A bit wasteful to call super and make the OidcUser...
        // However, the super of the super also dodes things and there's some OAuth2 stuff in
        // super as well...
        DefaultOidcUser oidcUser = (DefaultOidcUser) super.loadUser(userRequest);

        VaadinSsoAuthoritiesMapper mapper = new VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper();
        Collection<? extends GrantedAuthority> grantedAuthorities = mapper.mapAuthorities(oidcUser.getAuthorities(), oidcUser.getUserInfo());

        DefaultOidcUser modifiedOidcUser = new DefaultOidcUser(grantedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

        return modifiedOidcUser;
    }

    public interface VaadinSsoAuthoritiesMapper {

        Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities, OidcUserInfo userInfo);

    }

    public class VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper implements VaadinSsoAuthoritiesMapper {

        @Override
        public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities, OidcUserInfo userInfo) {
            List<? extends GrantedAuthority> mappedGrantedAuthorities = new ArrayList<>(authorities);
            if (userInfo.hasClaim("roles")) {
                List<String> roles = (List<String>) userInfo.getClaim("roles");
                roles.forEach(role -> mappedGrantedAuthorities.add(makeAuth(role)));
            }

            return mappedGrantedAuthorities;
        }

        protected <T extends GrantedAuthority> T makeAuth(String role) {
            return (T) new SimpleGrantedAuthority("ROLE_"+role);
        }

    }
}
