This folder is mounted into the dev container and used as a cachek for maven dependencies so that if/when the container is recreated, not all dependencies need to be redownloaded. 

If the host has the dependencies (or many of them) then running:

```
cp -r ~/.m2/* ./m2-cache/
```

Can speed up the initial setup of the container as all maven dependencies don't have to be downloaded... 

Disable this behaviour in docker-compose-devcontainer.yml volumes section if desired... 