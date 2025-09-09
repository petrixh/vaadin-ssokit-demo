#!/bin/sh

echo '\n********\n* Running postCreateScript.sh\n********\n'

echo 'Copying .vaadin folder to users home .vaadin folder if available'
/workspaces/vaadin-ssokit-demo/.devcontainer/scripts/copyDotVaadin.sh 

echo '\nIf proKey is required but was not copied above, run /workspace/scripts/setProKey.sh for easy way'
echo '********'
