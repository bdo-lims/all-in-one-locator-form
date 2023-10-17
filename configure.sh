#!/bin/bash

# Author : Caleb Steele-Lane

read -p "server address: " SERVER
echo

read -sp "database admin password: " DB_ADMIN_PASSWORD
echo

read -sp "database password: " DB_PASSWORD
echo

read -sp "keystore password: " KEY_PASS
echo

read -sp "truststore password: " TRUST_PASS
echo

read -sp "keycloak admin password: " KEYCLOAK_PASS
echo

find ./prod/ \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s/host\.openelis\.org/$SERVER/g"
find ./locator-form-frontend/ \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s/host\.openelis\.org/$SERVER/g"
find ./prod/ \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s/databaseAdminPassword/$DB_ADMIN_PASSWORD/g"
find ./prod/ \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s/databasePassword/$DB_PASSWORD/g"
find ./prod/ \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s/passwordForKeystore/$KEY_PASS/g"
find ./prod/ \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s/passwordForTruststore/$TRUST_PASS/g"
find ./prod/ \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i "s/keyCloakAdminPassword/$KEYCLOAK_PASS/g"