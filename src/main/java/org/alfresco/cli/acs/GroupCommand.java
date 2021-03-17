/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import java.util.List;
import org.alfresco.cli.acs.GroupCommand.GroupMemberCommand;
import org.alfresco.core.handler.GroupsApi;
import org.alfresco.core.model.Group;
import org.alfresco.core.model.GroupBodyCreate;
import org.alfresco.core.model.GroupBodyUpdate;
import org.alfresco.core.model.GroupEntry;
import org.alfresco.core.model.GroupMember;
import org.alfresco.core.model.GroupMemberEntry;
import org.alfresco.core.model.GroupMembershipBodyCreate;
import org.alfresco.core.model.GroupMembershipBodyCreate.MemberTypeEnum;
import org.alfresco.core.model.GroupPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "group", subcommands = GroupMemberCommand.class)
public class GroupCommand {

    @Autowired
    private GroupsApi groupsApi;

    @Command(description = "Get group list.")
    public Integer list(
            @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                    description = "Number of items to be skipped") Integer skipCount,
            @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                    description = "Number of items to be returned") Integer maxItems,
            @Option(names = {"-w", "--where"},
                    description = "Filter for returned items") String where) {
        ResponseEntity<GroupPaging> response =
                groupsApi.listGroups(skipCount, maxItems, null, null, where, null);
        List<GroupEntry> result = response.getBody().getList().getEntries();
        System.out.println(result);
        return 0;
    }

    @Command(description = "Create group.")
    public Integer create(
            @Option(names = {"-id", "--id"}, required = true,
                    description = "Group identifier") String id,
            @Option(names = {"-dn", "--displayName"}, required = true,
                    description = "Display name") String displayName,
            @Option(names = {"-pi", "--parentIds"},
                    description = "Parent group identifiers") List<String> parentIds) {
        Group result = groupsApi.createGroup(
                new GroupBodyCreate().id(id).displayName(displayName).parentIds(parentIds), null,
                null).getBody().getEntry();
        System.out.println(result);
        return 0;
    }

    @Command(description = "Get group details.")
    public Integer get(@Parameters(description = "Group identifier") String id) {
        Group result = groupsApi.getGroup(id, null, null).getBody().getEntry();
        System.out.println(result);
        return 0;
    }

    @Command(description = "Update group.")
    public Integer update(@Parameters(description = "Group identifier") String id,
            @Option(names = {"-dn", "--displayName"}, required = true,
                    description = "Display name") String displayName) {
        Group result = groupsApi
                .updateGroup(id, new GroupBodyUpdate().displayName(displayName), null, null)
                .getBody().getEntry();
        System.out.println(result);
        return 0;
    }

    @Command(description = "Delete group.")
    public Integer delete(@Parameters(description = "Group identifier") String id,
            @Option(names = {"-c", "--cascade"}, defaultValue = "false",
                    description = "Cascade deleted: true, false") Boolean cascade) {
        groupsApi.deleteGroup(id, cascade);
        System.out.println(id);
        return 0;
    }

    @Component
    @Command(name = "member", description = "List, create and delete group members.")
    static class GroupMemberCommand {

        @Autowired
        private GroupsApi groupsApi;

        @Command(description = "List group members.")
        public Integer list(@Parameters(description = "Group identifier") String id,
                @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                        description = "Number of items to be skipped") Integer skipCount,
                @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                        description = "Number of items to be returned") Integer maxItems,
                @Option(names = {"-w", "--where"},
                        description = "Filter for returned items") String where) {
            List<GroupMemberEntry> result =
                    groupsApi.listGroupMemberships(id, skipCount, maxItems, null, where, null)
                            .getBody().getList().getEntries();
            System.out.println(result);
            return 0;
        }

        @Command(description = "Create group member.")
        public Integer create(@Parameters(description = "Group identifier") String id,
                @Parameters(description = "User Id") String memberId, @Parameters(
                        description = "Member type. Valid values: ${COMPLETION-CANDIDATES}") MemberTypeEnum memberType) {
            GroupMember result = groupsApi.createGroupMembership(id,
                    new GroupMembershipBodyCreate().id(memberId).memberType(memberType), null)
                    .getBody().getEntry();
            System.out.println(result);
            return 0;
        }

        @Command(description = "Delete group member.")
        public Integer delete(@Parameters(description = "Group identifier") String id,
                @Parameters(description = "User Id") String personId) {
            groupsApi.deleteGroupMembership(id, personId);
            System.out.println(personId);
            return 0;
        }
    }
}
