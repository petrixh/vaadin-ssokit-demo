# Cloning and running the project

The demo application requires a Keycloak instance to be running on localhost:8081.

The following is to run the app on the host (see below for other options) 

The easiest way with Docker is to run the included compose file which will also use pre-existing keycloak configurations: 

```
docker compose up -d
```

Then you can build and run (Vaadin license required) the project with: 

```
./mvnw clean packaga spring-boot:run -DskipTests
```

## Running in a dev container with VS Code

Dev Containers require docker and docker compose to be installed in order to run. 

The container *will* forward the following from the host to the container: 
- current working directory under /workspace
- curerent user home/.vaadin under /dot-vaadin
- will forward the hosts docker socket to the container i.e. container can start/stop containers and has the same capabilities as running docker on the host

### TLDR version

Cmd + Shift + P -> "Dev Con reopen"

Copy and paste...

Run the keycloak 8081 tunnel: 
```
/workspace/scripts/tunnelKeyCloak.sh 
```

In another terminal run: 
```
./mvnw spring-boot:run
```

### Descriptive version

The Project includes a full dev container setup as well. The dev container uses a docker container to run the actual enviornment while the workspace folder is mounted from the host to the container. It is not strictly necessary to develop against the host filesystem, the dev contianer includes git and if desired, the repository could be checked out inside the container as well. 

However, in order to run the entire example application, including the keycloak and development container there are two docker compose files: `docker-compose.yml` and `docker-compose-devcontainer.yml`. The former has the application specific services (only Keycloak in this example but could also include databases, reddit, rabbits, etc.) while the latter has the dev container specific configurations, including workspace mounts etc. These two compose files are used by the devcontainer.json specification under `.devcontainer`

The dev container itself is beased on: 
- TODO make a project and push to docker hub for easier reuse
- Dockerfile that describes the container configurations themselves
- the bind mounts to the host filesystem

In order to run the dev container first clone the project and open it up in VS Code. While VS Code might suggest plugins etc to be installed we don't actually need to build anything on the host. 

If not already picked up by VS Code, you can open up the VS Code Command Promt (Cmd + Shift + P on OS X or click at the top of vs code and add a > to enter commands mode) and type `"Dev Con rebuild"` an you should have an option to run "Dev Containers: Rebuild in Container". This is handy if you're working on changes to the devcontainer itself, otherwise you can look for `"Dev Con reopen"` and you should have an option to "Reopen in Container" which will build and/or reuse an existing dev container if present.  

VS Code should start building the container and refresh itself after it has connected to the container. 

If your host did not have a proKey setup under .vaadin already, you will be asked to open as browser window to validate your proKey. Note that the proKey is only copied when the container is created, i.e. if you add it later to the host you should `rebuild` the container. 

Alternatively you could use a `offlineKey` however as these are machine dependent it might become problematic. If you use a `serverKey` then you need to run the build with `-Pproduction` as server key only allows production builds. 

Once the container is running and VS Code has stabilised, open a terminal and run: 

```
./scripts/tunnelKeyCloak.sh
```

This tunnels "localhost:8081" from within the container to the keycloak so that when the Vaadin applicaiton in `application.properties` has the SSO provider as `localhost:8081` it will work. This also makes it possible for the rediection to the SSO Provider (which will be localhost:8081) and also gives you access to the keycloak admin UI on http://localhost:8081 (user: admin, pass: admin in the preconfigured dababase). 

In antother terminal you can run `docker ps` or `docker stats` and you should see two containers, you are actually inside the one ending `-app-1` and the docker socket is passed through to it fromt he host in `docker-compose-devcontainer.yml` volumes section and the dev container has the feature `docker-outside-of-docker` enabled in `.devcontainer/devcontainer.json` so that you can control docker from the container just as if you were on the host.

### Running the application inside the container

Running the application within the container is not different from running it on the host: 

```
./mvnw spring-boot:run
```

However, if you had developed the application already on the host, then there is a possibility that permissions are not setup correctly. The container uses a user with UID 1000, i.e. on linux this is the first user (not root) that is created on the system. Depending on your system this may or may not be an issue. As quick fixes you can try to: 

- remove the target, node_modules etc. generated folders

Alsternatively clone your code within the container: 

```
cd
```

```
git clone https://URL_TO_REPO_HERE
```

and continue working from there. However, if using this approach, remember to commit your changes as they exist only inside the container whereas the /workspace folder is mirrored to the host. 

### Stopping development inside the container

To jump from the devcontainer back to the host, in the bottom left you can click on the dev container connection and search for `reopen` and you should have options to reopen the folder on the host (or SSH if running on a remote host)

In theory, the `devcontainer.json` has a `shutdownAction` that should run a `compose down` once VS Code is closed or disconnected from the container... However, theory and practice don't always meet. So in case they didn't from the host you can run: 

```
docker compose -f docker-compose-devconatiner.yml down && docker compose down 
```

To bring down the dev container and the services (only keycloak in this case) conatiners. 

## Running in a codespace

As we have a dev container setup already, the application can also be run in a github codespace. 

(Note: you can also create/start/manage a codespace directly from the `Codespaces` extension from the VS Code `Remote Explorer` tab for any of youre repositories if you wish)

For this, clone the repository and from your clone from the "code" button at the top right, select the `Codespaces` tab and launch a codespace. 

This will launch a browser based VS Code instance and setup the project on a virtual host. While the applicaiton in theory could be developed through the browser based IDE, the port forwarding feature uses URLs for forwarding ports, meaning that authtentication redirect to `localhost:8081` won't work with the browser based IDE. 

However, while the codespace is warming up, you can install the `Codespaces` plugin to VS Code. 

After the codespace has intialized itself, you can use the command palette (Cmd + Shift + P on OS X) and search for `codespa con` and you should have options to `Connect to Codespace` and it should list your codespaces. If this is the first time you're using `Codespaces` with this VS Code instance, you'll have to authorize it by logging in with your github credentials. 

After this VS Code should open up just as if you were running locally or against a SSH host. 

Run the tunnel for `localhost:8081` for Keycloak redirects in a terminal from VS Code: 

```
./scripts/tunnelKeyCloak.sh 
```

VS Code should automatically forward port 8081 to your localhost so now you should be able to access the Keycloak admin UI on http://localhost:8081

If you want to setup the remote host with a Vaadin license manually you can do it now. 

Otherwise in the next step, VS Code will try to open a browser and ask you to authenticate with vaadin.com

In another VS Code terminal run: 

```
./mvnw spring-boot:run
``` 

Once you're done with the codespace, remember to stop it and delete it if you don't need it anymore. You can stop it from the github repository UI, or from VS Codes command prompt `Stop Current Codespace`. Remember to delete it from the github UI if you don't need it anymore. 

Github also has under https://github.com/codespaces where you can track your codespaces, and from the github settings you can configure how fast an idle codespace is killed and for how long its data is stored (auto delete). Both runnig a codespace and keeping its data does count againt the free usage and will eventually start to cost. 


# TODO Old suff below, review and clean up... 

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