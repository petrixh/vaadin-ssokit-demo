#!/bin/sh
# A script to tunnel Keycloak to localhost:8081, as the dev container runs on the hosts docker network and the Keycloak server is also running on the hosts docker network, 
# this script will allow the dev container to access the Keycloak server running on the host machine as if it were running on the dev container itself.
# Also allows for easy port forwarding to the Keycloak server running on the host machine through the dev containers port 8081.

echo "Tunneling Keycloak to localhost:8081"
socat TCP-LISTEN:8081,fork TCP:sso-kit-demo-keycloak:8080 