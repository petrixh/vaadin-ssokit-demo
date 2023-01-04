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
- Go to the `Users` section from the left navigation
- Add a user, for instance `test`
- Click `Create` (shouldn't need to fill in anything except username)
- Go to the `Creadentials` tab
- Set the password, uncheck `Temporary` unless you want to be forced to change it on the first login...
- Go to the `Role mapping` tab
- Click on `Assign role`
- Select the `test-role` role

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

//TODO add views that demonstrate user access and show info from the JWT... 
//TODO Update creation part to include values to set that are shown in the UI.. 

//TODO document the overrides and Keycloak parser that was needed... 

# SSO Kit Test 2

This project can be used as a starting point to create your own Vaadin application with Spring Boot.
It contains all the necessary configuration and some placeholder files to get you started.

## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different 
IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/ssokittest2-1.0-SNAPSHOT.jar`

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorials at [vaadin.com/tutorials](https://vaadin.com/tutorials).
- Watch training videos and get certified at [vaadin.com/learn/training](https://vaadin.com/learn/training).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/components](https://vaadin.com/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes). 
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).
