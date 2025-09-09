package com.example.application.views.error;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.ParentLayout;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
@ParentLayout(MainLayout.class)
@PermitAll
public class AccessDeniedExceptionHandler
     extends Component
     implements HasErrorParameter<CustomAccessDeniedException>
{

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<CustomAccessDeniedException>
                    parameter) {
        getElement().setText(
            "Tried to navigate to a view without "
            + "correct access rights");
        return HttpServletResponse.SC_FORBIDDEN;
    }
}