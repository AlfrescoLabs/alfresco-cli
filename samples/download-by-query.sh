#!/bin/bash
# file: download-by-query.sh

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

# cmis query
CMIS_QUERY="select * from cmis:document where cmis:name like '%Notes%'"

# login the server
echo "Validating credentials for admin in $ACS_SERVER_URL"
$ALF config acs "$ACS_SERVER_URL" $ACS_ADMIN_USER $ACS_ADMIN_PASS

# get document ids from query
echo "Executing CMIS Query: \"$CMIS_QUERY\""
QUERY_RESULTS=$($ALF acs search cmis "$CMIS_QUERY" -f id)

for nodeId in $(echo $QUERY_RESULTS | sed "s/,/ /g")
do
    $ALF acs node get-content $nodeId -d "$PWD" -f id
done

echo "Job done!"
