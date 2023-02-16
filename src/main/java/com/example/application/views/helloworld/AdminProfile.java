package com.example.application.views.helloworld;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.AddressStandardClaim;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.annotation.security.RolesAllowed;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RolesAllowed({"admin"})
@PageTitle("Admin Profile")
@Route(value = "admin_profile", layout = MainLayout.class)
public class AdminProfile extends VerticalLayout {

    public AdminProfile(AuthenticationContext authenticationContext) throws InvocationTargetException, IllegalAccessException {
        add(new H1("This is for users with the admin role"));
        setWidthFull();
        Optional<OidcUser> authenticatedUser = authenticationContext.getAuthenticatedUser(OidcUser.class);

        if (authenticatedUser.isPresent()) {
            buildProfileUi(authenticatedUser.get());
        } else {
            add(new Span("No authenticated user present!"));
        }

    }

    private void buildProfileUi(OidcUser oidcUser) throws InvocationTargetException, IllegalAccessException {

        add(new ProfileUi(new OidcUserProxy(oidcUser)));

    }


    public static class ProfileUi extends FormLayout {

        private OidcUserProxy userProxy;

        public ProfileUi(OidcUserProxy userProxy) throws InvocationTargetException, IllegalAccessException {
            this.userProxy = userProxy;
            setWidthFull();

            setResponsiveSteps(new ResponsiveStep("0", 4));

            Binder<OidcUserProxy> binder = new Binder<>(OidcUserProxy.class);
            binder.setBean(userProxy);

            List<Method> declaredMethods = Arrays.stream(userProxy.getClass().getDeclaredMethods()).collect(Collectors.toList());

            List<Method> strings =
                declaredMethods.stream().filter(m -> m.getReturnType().isAssignableFrom(String.class)).collect(Collectors.toList());
            List<Method> booleans =
                declaredMethods.stream().filter(m -> m.getReturnType().isAssignableFrom(Boolean.class)).collect(Collectors.toList());

            for (Method s : strings) {
                if (s.getParameterCount() > 0) {
                    System.out.println("Skipping: " + s.getName() + " due to parameter required as input...");
                    continue;
                }
                makePre(s.invoke(userProxy), s.getName());
            }

            for (Method s : booleans) {
                if (s.getParameterCount() > 0) {
                    System.out.println("Skipping: " + s.getName() + " due to parameter required as input...");
                    continue;
                }
                makePre(s.invoke(userProxy), s.getName());
            }

            Set<String> claims = userProxy.getClaims().keySet();
            for (String claim : claims) {
                Object value = userProxy.getClaimAsString(claim);
                makePre(value, claim);
            }

        }

        private void makePre(Object value, String label) {
            label = label.startsWith("get") ? label.replace("get", " ") : label;
            label = splitCamelCase(label);

            String text = value != null ? value.toString() : " ";
            Pre claimDisp = new Pre(text);
            claimDisp.getStyle().set("white-space", "normal");
            claimDisp.getStyle().set("word-break", "break-all");
            claimDisp.getStyle().set("margin", "0");
            claimDisp.getStyle().set("padding", "0 8px");
            FormItem formItem = addFormItem(claimDisp, label.replaceAll("_", " "));
            formItem.getStyle().set("flex-direction", "column");
        }

        String splitCamelCase(String s) {
            return s.replaceAll(
                String.format("%s|%s|%s",
                    "(?<=[A-Z])(?=[A-Z][a-z])",
                    "(?<=[^A-Z])(?=[A-Z])",
                    "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
            );
        }


    }

    public static class OidcUserProxy {
        private OidcUser user;

        public OidcUserProxy(OidcUser user) {
            this.user = user;
        }

        public Map<String, Object> getClaims() {
            return user.getClaims();
        }

        public OidcUserInfo getUserInfo() {
            return user.getUserInfo();
        }

        public OidcIdToken getIdToken() {
            return user.getIdToken();
        }

        @Nullable
        public <A> A getAttribute(String name) {
            return user.getAttribute(name);
        }

        public Map<String, Object> getAttributes() {
            return user.getAttributes();
        }

        public Collection<? extends GrantedAuthority> getAuthorities() {
            return user.getAuthorities();
        }

        public String getName() {
            return user.getName();
        }

        public URL getIssuer() {
            return user.getIssuer();
        }

        public String getSubject() {
            return user.getSubject();
        }

        public List<String> getAudience() {
            return user.getAudience();
        }

        public Instant getExpiresAt() {
            return user.getExpiresAt();
        }

        public Instant getIssuedAt() {
            return user.getIssuedAt();
        }

        public Instant getAuthenticatedAt() {
            return user.getAuthenticatedAt();
        }

        public String getNonce() {
            return user.getNonce();
        }

        public String getAuthenticationContextClass() {
            return user.getAuthenticationContextClass();
        }

        public List<String> getAuthenticationMethods() {
            return user.getAuthenticationMethods();
        }

        public String getAuthorizedParty() {
            return user.getAuthorizedParty();
        }

        public String getAccessTokenHash() {
            return user.getAccessTokenHash();
        }

        public String getAuthorizationCodeHash() {
            return user.getAuthorizationCodeHash();
        }

        public String getFullName() {
            return user.getFullName();
        }

        public String getGivenName() {
            return user.getGivenName();
        }

        public String getFamilyName() {
            return user.getFamilyName();
        }

        public String getMiddleName() {
            return user.getMiddleName();
        }

        public String getNickName() {
            return user.getNickName();
        }

        public String getPreferredUsername() {
            return user.getPreferredUsername();
        }

        public String getProfile() {
            return user.getProfile();
        }

        public String getPicture() {
            return user.getPicture();
        }

        public String getWebsite() {
            return user.getWebsite();
        }

        public String getEmail() {
            return user.getEmail();
        }

        public Boolean getEmailVerified() {
            return user.getEmailVerified();
        }

        public String getGender() {
            return user.getGender();
        }

        public String getBirthdate() {
            return user.getBirthdate();
        }

        public String getZoneInfo() {
            return user.getZoneInfo();
        }

        public String getLocale() {
            return user.getLocale();
        }

        public String getPhoneNumber() {
            return user.getPhoneNumber();
        }

        public Boolean getPhoneNumberVerified() {
            return user.getPhoneNumberVerified();
        }

        public AddressStandardClaim getAddress() {
            return user.getAddress();
        }

        public Instant getUpdatedAt() {
            return user.getUpdatedAt();
        }

        public <T> T getClaim(String claim) {
            return user.getClaim(claim);
        }

        public boolean hasClaim(String claim) {
            return user.hasClaim(claim);
        }

        @Deprecated
        public Boolean containsClaim(String claim) {
            return user.containsClaim(claim);
        }

        public String getClaimAsString(String claim) {
            return user.getClaimAsString(claim);
        }

        public Boolean getClaimAsBoolean(String claim) {
            return user.getClaimAsBoolean(claim);
        }

        public Instant getClaimAsInstant(String claim) {
            return user.getClaimAsInstant(claim);
        }

        public URL getClaimAsURL(String claim) {
            return user.getClaimAsURL(claim);
        }

        public Map<String, Object> getClaimAsMap(String claim) {
            return user.getClaimAsMap(claim);
        }

        public List<String> getClaimAsStringList(String claim) {
            return user.getClaimAsStringList(claim);
        }
    }
}
