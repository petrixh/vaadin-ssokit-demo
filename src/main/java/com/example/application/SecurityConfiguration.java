package com.example.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Additional security configuration to permit access to static resources
 * that are not automatically allowed by the SSO Kit auto-configuration.
 * This filter chain runs before the SSO Kit's chain to allow specific paths.
 */
@Configuration
public class SecurityConfiguration {

    @Bean
    @Order(0)
    SecurityFilterChain staticResourcesFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/line-awesome/**", "/images/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
