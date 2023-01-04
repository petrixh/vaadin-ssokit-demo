package com.example.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapperTest {

    private VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper mapper = new VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void mapAuthorities_baseCaseWithMapToList() {
        //Setup
        mapper.setOidcRolePath("realm_access.roles");

        List<? extends GrantedAuthority> authoritiesIn = new ArrayList<>();

        OidcUserInfo userInfo = mock(OidcUserInfo.class);
        //Simulating the actual default Keycloak returned values...
        LinkedHashMap<String, List> realmAccessMap = new LinkedHashMap<>();
        List<String> roles = Arrays.asList("test1", "test2");
        realmAccessMap.put("roles", roles);

        when(userInfo.getClaim("realm_access")).thenReturn(realmAccessMap);

        //Test
        Collection<? extends GrantedAuthority> grantedAuthorities = mapper.mapAuthorities(authoritiesIn, userInfo);

        //Verify

        assertEquals(2, grantedAuthorities.size());

    }

    @Test
    void mapAuthorities_directRolesList() {

        //Setup
        mapper.setOidcRolePath("roles");

        List<? extends GrantedAuthority> authoritiesIn = new ArrayList<>();

        OidcUserInfo userInfo = mock(OidcUserInfo.class);
        List<String> roles = Arrays.asList("test1", "test2");

        when(userInfo.getClaim("roles")).thenReturn(roles);

        //Test
        Collection<? extends GrantedAuthority> grantedAuthorities = mapper.mapAuthorities(authoritiesIn, userInfo);

        //Verify

        assertEquals(2, grantedAuthorities.size());

    }

    @Test
    void mapAuthorities_noRoles() {

        //Setup
        mapper.setOidcRolePath("realm_access.roles");

        List<? extends GrantedAuthority> authoritiesIn = new ArrayList<>();

        OidcUserInfo userInfo = mock(OidcUserInfo.class);
        when(userInfo.getClaim(anyString())).thenReturn(null);

        //Test
        Collection<? extends GrantedAuthority> grantedAuthorities = mapper.mapAuthorities(authoritiesIn, userInfo);

        //Verify

        assertEquals(0, grantedAuthorities.size());

    }

}