#!/bin/sh

echo "Tunneling Keycloak to localhost:8081"
socat TCP-LISTEN:8081,fork TCP:sso-kit-demo-keycloak:8080 