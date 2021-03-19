#!/bin/bash
# file: create-site.sh

set -o errexit
set -o pipefail
set -o nounset

# alfresco cli program path
ALF="../alfresco-rest-cli/target/alfresco"
chmod +x $ALF

# server data
ACS_SERVER_URL=http://localhost:8080
ACS_ADMIN_USER=admin
ACS_ADMIN_PASS=admin

# site data
SITE_NAME=sample-site
SITE_VISIBILITY=PUBLIC

# user data
USER_NAME=johnsmith
USER_PASS=password
USER_EMAIL=johnsmith@domain.fake
USER_FIRST_NAME=John
USER_LAST_NAME=Smith

# login the server
echo "Validating credentials for admin in $ACS_SERVER_URL"
$ALF config acs "$ACS_SERVER_URL" $ACS_ADMIN_USER $ACS_ADMIN_PASS

# create site "sample"
echo "Creating site $SITE_NAME"
$ALF acs site create $SITE_NAME -t $SITE_NAME -d $SITE_NAME -v $SITE_VISIBILITY -f id
echo ""

# get documentLibrary folder id
DOCLIB_ID=$($ALF acs site get-container $SITE_NAME documentLibrary -f id)
echo "Document Library ID is $DOCLIB_ID"

# upload this file to documentLibrary (admin)
FILE_ID=$($ALF acs node create -n test-admin.sh -s "$PWD/create-site.sh" -p $DOCLIB_ID -f id)
echo "File ID uploaded by admin is $FILE_ID"

# create a new group
$ALF acs group create $SITE_NAME-Managers -dn $SITE_NAME-Managers
echo "Group $SITE_NAME-Managers has been created"

# create a new user
$ALF acs person create $USER_NAME -e $USER_EMAIL -fn $USER_FIRST_NAME -ln $USER_LAST_NAME -p $USER_PASS
echo "User $USER_NAME has been created"

# add user to group
$ALF acs group create-member GROUP_$SITE_NAME-Managers $USER_NAME PERSON
echo "User $USER_NAME has been added to group GROUP_$SITE_NAME-Managers"

# add new group to site as SiteManager
$ALF acs site create-group-member $SITE_NAME GROUP_$SITE_NAME-Managers SiteManager
echo "Group GROUP_$SITE_NAME-Managers has been added as member in the site $SITE_NAME"

# change user
echo "Validating credentials for $USER_NAME in $ACS_SERVER_URL"
$ALF config acs "$ACS_SERVER_URL" $USER_NAME $USER_PASS

# upload this file to documentLibrary (user)
FILE_ID=$($ALF acs node create -n test-user.sh -s "$PWD/create-site.sh" -p $DOCLIB_ID -f id)
echo "File ID uploaded by $USER_NAME is $FILE_ID"

echo "Job done!"
