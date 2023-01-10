# Setup
There are docker compose files under the `/docker` folder. 

`docker-compose up -d`

After setting up the keycloak docker image use: 

`docker stop keycloak-keycloak-1`

`docker start keycloak-keycloak-1`

to start and stop the container. compose down/up will eventually wipe your data as the container is destroyed and the volumes are not named. 

(or whatever the container is called, check with `docker ps` or `docker container list -all` tab autocomplete should work at least if running docker as non-root)


## Keycloak setup

Login to the admin console, Keycloak typically runs on port 8080, however the example has it on 8081 to avoid clashes
when running both on localhost, (see docker-compose.yml for ports, passwords etc...)

## Configuring Keycloak
In the Keycloak admin console:
- Top left, create a new realm by clicking the selector that says "Master" and create a new realm
- Name it `vaadin-sso` and Save it
- Navigate to `Clients`
- Click on `Create cilent`
- Make sure the Client type is "OpenID Connect"
- Set the `Client ID` to `vaadin-sso`
  - On the next page enable `Client authentication` and `Authorization` (May or may not be necessary)
- After saving, you on the `Settings` page you might need to set (probably unsecure as heck): 
  - Root URL: `http://localhost:8080`
  - Home URL: `http://localhost:8080`
  - Valid redirect URI's: `*`
  - Valid post logout...: `*`
  - Web origins:          `*`
- Select the `Credentials` tab
  - copy the `Client secret` value to your projects `application.properties`-file

### Adding users and roles: 
- Go to `Realm roles` from the navigation to the left
- Add a new Role named `test-role` (this is what will be used in the Vaadin @RolesAllowed-annotation)
- Add a `admin` role
- 
- Go to the `Users` section from the left navigation
- Add a user, for instance `test`
- Click `Create` (shouldn't need to fill in anything except username)
- Go to the `Creadentials` tab
- Set the password, uncheck `Temporary` unless you want to be forced to change it on the first login...
- Go to the `Role mapping` tab
- Click on `Assign role`
- Select the `test-role` role
- Repeat for another user called `admin`
- Add both the `admin` and the `test-role` roles to the admin user

Next let's check if your version of Keycloak too has amnesia
- Navigate to `Clients` -> `vaadin-sso` -> `Client scopes` -> `Evaluate` (2nd level tab)
  - Enter your test user in the User field: `test`
  - After a couple of tries, you realise that you have to select it from the dropdown, Enter won't do... feature?
  - Click on `Generated User Info` (right)
  - Ensure that there is a section with `realm_access -> roles` included, for instance it could look like this: 
```
{
  "sub": "ae7fd237-d9d8-4922-b986-91c3326cc3b8",
  "email_verified": false,
  "realm_access": {
    "roles": [
      "test-role",
      "offline_access",
      "uma_authorization",
      "default-roles-vaadin-sso"
    ]
  },
  "preferred_username": "test",
  "given_name": "",
  "family_name": ""
}
```

If your Keycloak also suffers from amnesia, do the following:   
- Navigate to `Client scopes` -> `roles` (row) -> `Mappers` (tab) -> `realm roles` (row)
- Press `Save` (no need to change anything, just remind Keycloak that these should be included...)
  - You could take note of `Token Claim Name`, this is what will be parsed on the Vaadin side when determining roles

Verify through the `Client scopes` -> Evaluate as we did earlier that the roles are included.

### Time to run and test
Check `application.properties` again, make sure the keycloak server address is correct and the client secret has been 
copied over. 

Run the app... 

# Things to test

There are a couple of views in the app: 
- `Hello World` will allow any user with the default `USER` role (this is added by Spring Security?? I think)
- `Hello Profile Role` view will show the current users profile and granted authorities, accessible with the `test-role` role
- `Hello Admin Role Profile` view is only accessible if the user has the `admin` Granted Authority
- `About` the original location of the logout button.. also dumps the OidcUser object into the console for debugging


# Design/idea
The idea is to extend the Vaadin SSO kit by overriding the default `OidcUserService` with a `VaadinOidcUserService`. 
This new service hooks into the way the `OidcUser` object is created, however as the method of actually instantiating 
said object is private, we have to actually override the return value with our own. It is enabled by adding
`@Configuration` to the `Application` class and instantiating the bean: 

```
@Configuration
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public OidcUserService getOidcUserService() {
        return new VaadinOidcUserService();
    }

```

The service also has an interface, `VaadinSsoAuthoritiesMapper` that, if implemented and configured, will be called when 
a new instance of `OidcUser` is about to be created. In the case of this demo app, there is a Keycloak specific 
implementation included: `VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper` and it is enabled again from the `Application`
class: 

```
@Bean
public VaadinOidcUserService.VaadinSsoAuthoritiesMapper getVaadinSsoAuthoritiesMapper() {
    return new VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper();
}
```

The location (in the JWT) from where the mapper tries to parse roles, can be configured through 
`application.properties` but defaults to the Keycloak default `realm_access.roles`: 

`application.properties`
```
vaadin.sso.keycloak.oidc.roles.path=role-test
```

## BYOM (Bring Your Own Mapper)
You can make your own mapper by creating a class that implements `VaadinSsoAuthoritiesMapper` for instance: 

```
public class DbRoleMapper implements VaadinOidcUserService.VaadinSsoAuthoritiesMapper {
    
    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities, OidcUserInfo userInfo) {

        //TODO fetch your roles from somewhere, a DB for instance for the user
        // userInfo.getPreferredUsername() (or what ever you want to use to identify the user)
        // Return the full list of GrantedAuthorities for this user
        // (decide if you want to include the Spring Security/OpenID defaults coming from the authorities-variable)
        ArrayList<? extends GrantedAuthority> grantedAuthorities = new ArrayList<>(authorities);
        // TODO 
        return grantedAuthorities;
    }
}
```

NOTE: In order for Spring Security to work, the Granted Authorities that you want to use with the `@RolesAllowed` 
annotation need to start with `ROLE_` so for example, the `GrantedAuthority` value for our `test-role` 
should be: `ROLE_test-role`

And finally replace the implementing bean in the `Application` class: 

```
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
        //return new VaadinSsoKitKeycloakDefaultRealmAuthoritiesMapper();
        return new DbRoleMapper(); 
    }

}
```