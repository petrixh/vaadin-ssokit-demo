package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.annotation.security.PermitAll;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@PermitAll
@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {

    public AboutView(AuthenticationContext authenticationContext) {
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        add(new H2("This place intentionally left empty"));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");




        if(authenticationContext.getAuthenticatedUser(OidcUser.class).isPresent()) {
            OidcUser oidcUser = authenticationContext.getAuthenticatedUser(OidcUser.class).get();
            System.out.println("Dumping user info: ");
            System.out.println(oidcUser);

            boolean formatted = true;

            if(formatted) {
                System.out.println("Dumping user info formatted: ");
                add(new Span("Current auth User: " + authenticationContext.getAuthenticatedUser(OidcUser.class).get().getName()));
                Optional<OidcUser> authenticatedUser = authenticationContext.getAuthenticatedUser(OidcUser.class);
                Map<String, Object> claims = authenticatedUser.get().getClaims();
                StringBuilder sb = new StringBuilder();
                sb.append("------ Claims: \n ");
                for (String key : claims.keySet()) {
                    sb.append(key + ": ").append(claims.get(key)).append("\n");
                }
                sb.append("----- Claims done!");

                System.out.println(sb.toString());
                sb = new StringBuilder();
                Map<String, Object> attributes = authenticatedUser.get().getAttributes();
                sb.append("----- Attributes: \n");

                for (String key : attributes.keySet()) {
                    sb.append(key + ": ").append(attributes.get(key)).append("\n");
                }
                sb.append("----- Attributes done!");
                System.out.println(sb.toString());

                sb = new StringBuilder();
                Collection<? extends GrantedAuthority> authorities = authenticatedUser.get().getAuthorities();

                System.out.println("Granted Authorities: ");
                authorities.forEach(grantedAuthority -> System.out.println(grantedAuthority.toString()));


            }
            System.out.println("Done dumping user info");
        }

        add(new Button("Logout", e -> {
            authenticationContext.logout();
        }));
    }

}
