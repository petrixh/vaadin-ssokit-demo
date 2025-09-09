#
#
# OUTDATED TODO UPDATE!!!
#

# Running in a dev container with VS Code

Dev Containers require docker and docker compose to be installed in order to run. 

The container *will* forward the following from the host to the container: 
- current working directory under /workspace
- curerent user home/.vaadin under /dot-vaadin
- will forward the hosts docker socket to the container i.e. container can start/stop containers and has the same capabilities as running docker on the host
- will mount the containers .m2 folder to the project checkout folder/m2-cache

## TLDR version

Cmd + Shift + P -> "Dev Con reopen"

In another terminal run: 
```
./mvnw spring-boot:run
```

## Descriptive version

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

Run the keycloak contianer (without -d if issues to see the logs): 

```
docker compose up -d 
```

Once the container is running and VS Code has stabilised VS Code should atuomatically forward port 8081 

This tunnels "localhost:8081" from within the container to the keycloak so that when the Vaadin applicaiton in `application.properties` has the SSO provider as `localhost:8081` it will work. This also makes it possible for the rediection to the SSO Provider (which will be localhost:8081) and also gives you access to the keycloak admin UI on http://localhost:8081 (user: admin, pass: admin in the preconfigured dababase). 

If you check the docker on the docker hosts terminal you can run `docker ps` or `docker stats` and you should see only one container, you are actually inside the one ending `-app-1`. Dokcer in Docker allows you to run containers within the container. For local development this is probably fine, however for prod use there are privilegie escalation concerns that seem to state to only use this "if you know what you're doing"... we don't, so don't do it as a production setup!

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
docker compose -f docker-compose-devcontainer.yml down && docker compose down  
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

Run keycloak with: 

````
docker compose up -d
````

VS Code should automatically forward port 8081 to your localhost so now you should be able to access the Keycloak admin UI on http://localhost:8081

If you want to setup the remote host with a Vaadin license manually you can do it now. 

Otherwise in the next step, VS Code will try to open a browser and ask you to authenticate with vaadin.com

In another VS Code terminal run: 

```
./mvnw spring-boot:run
``` 

Once you're done with the codespace, remember to stop it and delete it if you don't need it anymore. You can stop it from the github repository UI, or from VS Codes command prompt `Stop Current Codespace`. Remember to delete it from the github UI if you don't need it anymore. 

Github also has under https://github.com/codespaces where you can track your codespaces, and from the github settings you can configure how fast an idle codespace is killed and for how long its data is stored (auto delete). Both runnig a codespace and keeping its data does count againt the free usage and will eventually start to cost. 
