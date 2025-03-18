package com.example.application.views.helloworld;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"USER"})
@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Menu(order = 0, icon = LineAwesomeIconUrl.GLOBE_SOLID)
public class HelloWorldView extends VerticalLayout {

    private TextField name;
    private Button sayHello;

    public HelloWorldView() {
        add(new H1("This is for any user with the default USER role"));

        name = new TextField("Your name");
        sayHello = new Button("Say hello");

        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setVerticalComponentAlignment(Alignment.END, name, sayHello);
        add(horizontalLayout);
        horizontalLayout.add(name, sayHello);
    }

}
