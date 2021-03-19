#!/bin/bash
# file: watch-folder.sh

set -o errexit
set -o pipefail
set -o nounset

# alfresco cli program path
ALF="../alfresco-rest-cli/target/alfresco"
chmod +x $ALF

# alfresco stream cli program path
ALF_STREAM="../alfresco-event-cli/target/alfresco-stream"
chmod +x $ALF_STREAM

# server data
ACS_SERVER_URL=http://localhost:8080
ACS_ADMIN_USER=admin
ACS_ADMIN_PASS=admin

# folder data
FOLDER_NAME=watched

# events to be watched: NODE_CREATED NODE_UPDATED NODE_DELETED
# use "-t" to precede the event type
# unset value for this variable means that all events will be watched
EVENT_WATCHED="-t NODE_CREATED"

# login the server
echo "Validating credentials for admin in $ACS_SERVER_URL"
$ALF config acs "$ACS_SERVER_URL" $ACS_ADMIN_USER $ACS_ADMIN_PASS

# create folder
echo "Creating folder $FOLDER_NAME"
$ALF acs node create -n $FOLDER_NAME -t "cm:folder" -p "/Shared" -f id
echo ""

# watch folder
echo "Watching folder $FOLDER_NAME"
$ALF_STREAM watch folder "/Shared/$FOLDER_NAME" $EVENT_WATCHED
