package com.example.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper implements VaadinOidcUserService.VaadinSsoAuthoritiesMapper {

    public static final String DELIMITER = "\\.";
    public static final String ROLE_PREFIX = "ROLE_";

    @Value("${vaadin.sso.keycloak.oidc.roles.path:realm_access.roles}")
    private String oidcRolePath;

    private static Logger logger = Logger.getLogger(VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper.class.getName());
    private Level LOG_LEVEL = Level.FINE;

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities, OidcUserInfo userInfo) {
        List<? extends GrantedAuthority> mappedGrantedAuthorities = new ArrayList<>(authorities);

        if (oidcRolePath != null) {

            logger.log(LOG_LEVEL, "Mapping Authorities from: " + oidcRolePath);
            String[] split = oidcRolePath.split(DELIMITER);

            Object entry = null;
            for (int i = 0; i < split.length; i++) {

                if (entry instanceof Map) {
                    logger.log(LOG_LEVEL, "Found entry of type Map");
                    entry = ((Map) entry).get(split[i]);
                }

                if (entry instanceof List) {
                    logger.log(LOG_LEVEL, "Found entry of type List, stopping...");
                    break;
                }

                if (i == 0) {
                    entry = userInfo.getClaim(split[0]);
                }
            }

            if (entry instanceof List) {
                logger.log(LOG_LEVEL, "Trying to parse entries of type List, mapped authorities size before: " + mappedGrantedAuthorities.size());
                ((List<String>) entry).forEach(role -> mappedGrantedAuthorities.add(makeAuth(role)));
                logger.log(LOG_LEVEL, "Done parsing entries of type List, mapped authorities size after: " + mappedGrantedAuthorities.size());
            }
        }

        return mappedGrantedAuthorities;
    }

    protected <T extends GrantedAuthority> T makeAuth(String role) {
        return (T) new SimpleGrantedAuthority(ROLE_PREFIX + role);
    }

    // For tests... Running with SpringBoot tries to bring up
    // entire context which wants to talk to Keycloak instance...
    protected void setOidcRolePath(String oidcRolePath) {
        this.oidcRolePath = oidcRolePath;
    }

}