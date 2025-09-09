package com.example.application.views;



import com.example.application.CustomAccessDeniedException;
import com.example.application.views.helloworld.AdminProfileView;
import com.example.application.views.helloworld.TestProfileView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.Optional;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final AuthenticationContext authenticationContext;
    private H1 viewTitle;

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("SSO Kit Test 2");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        if (authenticationContext.getAuthenticatedUser(OidcUser.class).isPresent()) {
            Button logout = new Button("Logout", e -> authenticationContext.logout());
            logout.setId("logout");
            layout.add(logout);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        // Since SSO Kit doesn't support role mapping to GrantedAuthrorities we cannot use @RolesAllowed annotation directly... 
        // this is one example (not the only example) for how to handle view access

        Class<?> navigationTarget = event.getNavigationTarget();
        Optional<OidcUser> authenticatedUser = this.authenticationContext.getAuthenticatedUser(OidcUser.class); 
        String currentPrincipalName = null;

        if(authenticatedUser.isPresent()){
            currentPrincipalName = authenticatedUser.get().getPreferredUsername(); 
        }

        System.out.println("Before enter running for: " + navigationTarget + " for user: " + currentPrincipalName);


        //AdminView
        if(AdminProfileView.class.equals(navigationTarget)){
            if("admin".equalsIgnoreCase(currentPrincipalName)){
                //Allow admin user to admin view... 
            }else{
                //Redirect to error
                event.rerouteToError(new CustomAccessDeniedException(), "Unauthorized"); 
            }
        }

        //Test user view only
        if(TestProfileView.class.equals(navigationTarget)){
            if("test".equalsIgnoreCase(currentPrincipalName)){
                //Allow admin user to admin view... 
            }else{
                //Redirect to error
                event.rerouteToError(new CustomAccessDeniedException(), "Unauthorized"); 
            }
        }

    
    }

    
}
