package com.example.application;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@Theme(value = "ssokittest2")
@PWA(name = "SSO Kit Test 2", shortName = "SSO Kit Test 2", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
@NpmPackage(value = "@vaadin-component-factory/vcf-nav", version = "1.0.6")
@Configuration
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public OidcUserService getOidcUserService() {
        return new VaadinOidcUserService();
    }

    @Bean
    public VaadinOidcUserService.VaadinSsoAuthoritiesMapper getVaadinSsoAuthoritiesMapper() {
        return new VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper();
        //return new DbRoleMapper(); 
    }

}
