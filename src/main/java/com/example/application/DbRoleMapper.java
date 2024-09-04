package com.example.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

/** 
 * Example of a more hand written simple role mapper that could call a service or query a database to fetch the users roles.
*/
public class DbRoleMapper implements VaadinOidcUserService.VaadinSsoAuthoritiesMapper {

    public static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities,
            OidcUserInfo userInfo) {

                List<? extends GrantedAuthority> mappedGrantedAuthorities = new ArrayList<>(authorities);

                String email = userInfo.getEmail(); 
                String subject = userInfo.getSubject(); 
                String preferredUsername = userInfo.getPreferredUsername(); 

                //Simulate fetching roles for the user from a database based on the appropriate identifier, be it email, the pricipal id or username
                List<String> roles = getRolesFor(preferredUsername); 
   
                ((List<String>) roles).forEach(role -> mappedGrantedAuthorities.add(makeAuth(role)));

                return mappedGrantedAuthorities;

    }

    protected <T extends GrantedAuthority> T makeAuth(String role) {
        return (T) new SimpleGrantedAuthority(ROLE_PREFIX + role);
    }


    /* Simulated backend service call, hardcoded as no db */
    protected List<String> getRolesFor(String userId){

        if("admin".equals(userId)){
            return Arrays.asList(new String[]{"admin", "test-role", "db-role-mapper-only-role"});
        }

        if("test".equals(userId)){
            return Arrays.asList(new String[]{"test-role", "db-role-mapper-only-role"});
        }

        return Collections.emptyList(); 

    }
    
}
