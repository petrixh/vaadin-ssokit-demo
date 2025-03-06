#!/bin/sh

echo '\n********\n* Running postCreateScript.sh\n********\n'
echo 'Marking keycloak_data as unchanged in git to avoid accidental commits'
find /workspace/docker/keycloak_data -type f ! -name '*lock.db' -exec git update-index --assume-unchanged {} \; 

echo 'Copying .vaadin folder to users home .vaadin folder if available'
/workspace/scripts/copyDotVaadin.sh 

echo '\nIf proKey is required but was not copied above, run /workspace/scripts/setProKey.sh for easy way'
echo 'run /workspace/scripts/tunnelKeyCloak.sh to start tunneling localhost8081 to the KC container'
echo '********'
