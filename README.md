# Setup
There are a fiew docker compose files under in this project. The project uses a preconfigured Keycloak server in docker which can be started with: 

`docker-compose up -d`

The `vaadin-sso` keycloak realm is imported on each keycloak container recreate, state is intentionally not stored between crates to ensure CI builds are consistent. 

The realm export with users etc. CI/CD passwords and secretes are exported under: `keycloak_import` do *NOT* use these for anything! 

To reset KC simply run: 

```
docker compose down && docker compose up
```

and it should return back to the default state. 

To run the project, you will need to have a license for Vaadin SSO Kit. 

To run the project after starting the keycloak server run: 

```
./mvnw spring-boot:run
```

## Example project structure
There are a few views under the `views` package: 
- about just a placeholder view with `@AnonymousAllowed` to demonstrate navigation wtiout logging in... 
- error has the unauthorized custom exception and handler, i.e. what to show when a unauthorized navigation occurs
- helloworld package has 3 views, for wich the access control is handled from `MainView.java`
  - AdminProfileView.java is for the admin user only, others will get a unauthorized response
  - HelloWorldView.java is for any authored user
  - TestProfileView.java is for the user 'test' only
- MainLayout.java works as a parent layout for all views, it implements `BeforeEnterObserver` which allows it to control whether a navigation is allowed before the view is entered. 

While the implementation is minimal in order to keep it simple, obviously the user check could be done against a database where a principalName or email to viewname or role mapping is done. 


For a more integrated approach with @RolesAllowed and Keycloak managed roles  see https://github.com/petrixh/vaadin-ssokit-demo/tree/custom-role-mappings-for-keycloak see README.md on that branch also for configuration details. 

## Keycloak setup (manual/recreate db)

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
- Go to the `Users` section from the left navigation
- Add a user, for instance `test`
- Click `Create` (shouldn't need to fill in anything except username)
- Go to the `Creadentials` tab
- Set the password, uncheck `Temporary` unless you want to be forced to change it on the first login...
- Repeat for another user called `admin`


### Time to run and test
Check `application.properties` again, make sure the keycloak server address is correct and the client secret has been 
copied over. 
