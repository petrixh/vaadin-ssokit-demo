package com.example.application.views.helloworld;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.sso.starter.AuthenticationContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.annotation.security.RolesAllowed;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RolesAllowed({"test-role"})
@PageTitle("Hello World")
@Route(value = "profile", layout = MainLayout.class)
public class HelloProfile extends VerticalLayout {

    public HelloProfile(AuthenticationContext authenticationContext) throws InvocationTargetException, IllegalAccessException {

        add(new H1("This is for users with test-role only"));

        setWidthFull();
        Optional<OidcUser> authenticatedUser = authenticationContext.getAuthenticatedUser();

        if (authenticatedUser.isPresent()) {
            buildProfileUi(authenticatedUser.get());
        } else {
            add(new Span("No authenticated user present!"));
        }

        add(new Button("Logout", e -> authenticationContext.logout()));

    }

    private void buildProfileUi(OidcUser oidcUser) throws InvocationTargetException, IllegalAccessException {

        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        TextField preferredName = new TextField("Preferred Name");
        DateTimePicker authTime = new DateTimePicker("Auth time");
        DateTimePicker expTime = new DateTimePicker("Auth expiration");


        Binder<OidcUser> oidcUserBinder = new Binder<>(OidcUser.class);
        oidcUserBinder.bind(firstName, OidcUser::getName, null);
        oidcUserBinder.bind(lastName, OidcUser::getFamilyName, null);
        oidcUserBinder.bind(preferredName, OidcUser::getPreferredUsername, null);
        oidcUserBinder.forField(authTime).withConverter(
            localDateTime -> localDateTime.toInstant(ZoneOffset.of(ZoneOffset.systemDefault().getId())),
            instant -> LocalDateTime.ofInstant(instant,
                ZoneOffset.systemDefault())).bind(OidcUser::getAuthenticatedAt, null);
        oidcUserBinder.forField(expTime).withConverter(
            localDateTime -> localDateTime.toInstant(ZoneOffset.of(ZoneOffset.systemDefault().getId())),
            instant -> LocalDateTime.ofInstant(instant,
                ZoneOffset.systemDefault())).bind(OidcUser::getExpiresAt, null);

        oidcUserBinder.setBean(oidcUser);

        add(firstName, lastName, preferredName, authTime, expTime);

        List<String> grantedAuths = oidcUser.getAuthorities().stream().map(ga -> ga.getAuthority()).collect(Collectors.toList());
        ListBox<String> grantedAuthorities = new ListBox<>();
        grantedAuthorities.setItems(grantedAuths);
        add(new Span("Granted Authorities"), grantedAuthorities);

    }


}
