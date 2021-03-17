/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import java.util.List;
import org.alfresco.cli.acs.GroupCommand.GroupMemberCommand;
import org.alfresco.cli.format.FormatProvider;
import org.alfresco.cli.format.FormatProviderRegistry;
import org.alfresco.core.handler.GroupsApi;
import org.alfresco.core.model.*;
import org.alfresco.core.model.GroupMembershipBodyCreate.MemberTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "group", subcommands = GroupMemberCommand.class)
public class GroupCommand {

    @Autowired
    GroupsApi groupsApi;

    @CommandLine.Mixin
    FormatProviderRegistry formatProvider;

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
        GroupPagingList result = response.getBody().getList();
        formatProvider.print(result);
        return 0;
    }

    @Command(description = "Create group.")
    public Integer create(@Parameters(description = "Id of the Site") String id,
            @Option(names = {"-dn", "--displayName"}, required = true,
                    description = "Display name") String displayName,
            @Option(names = {"-pi", "--parentIds"},
                    description = "Parent group identifiers") List<String> parentIds) {
        Group result = groupsApi.createGroup(
                new GroupBodyCreate().id(id).displayName(displayName).parentIds(parentIds), null,
                null).getBody().getEntry();
        formatProvider.print(result);
        return 0;
    }

    @Command(description = "Get group details.")
    public Integer get(@Parameters(description = "Group identifier") String id) {
        Group result = groupsApi.getGroup(id, null, null).getBody().getEntry();
        formatProvider.print(result);
        return 0;
    }

    @Command(description = "Update group.")
    public Integer update(@Parameters(description = "Group identifier") String id,
            @Option(names = {"-dn", "--displayName"}, required = true,
                    description = "Display name") String displayName) {
        Group result = groupsApi
                .updateGroup(id, new GroupBodyUpdate().displayName(displayName), null, null)
                .getBody().getEntry();
        formatProvider.print(result);
        return 0;
    }

    @Command(description = "Delete group.")
    public Integer delete(@Parameters(description = "Group identifier") String id,
            @Option(names = {"-c", "--cascade"}, defaultValue = "false",
                    description = "Cascade deleted: true, false") Boolean cascade) {
        groupsApi.deleteGroup(id, cascade);
        formatProvider.print(id);
        return 0;
    }

    @Command(name = "member", description = "List, create and delete group members.")
    class GroupMemberCommand {
        
        @Command(description = "List group members.")
        public Integer list(@Parameters(description = "Group identifier") String id,
                @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                        description = "Number of items to be skipped") Integer skipCount,
                @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                        description = "Number of items to be returned") Integer maxItems,
                @Option(names = {"-w", "--where"},
                        description = "Filter for returned items") String where) {
            GroupMemberPagingList result =
                    groupsApi.listGroupMemberships(id, skipCount, maxItems, null, where, null)
                            .getBody().getList();
            formatProvider.print(result);
            return 0;
        }

        @Command(description = "Create group member.")
        public Integer create(@Parameters(description = "Group identifier") String id,
                @Parameters(description = "User Id") String memberId, @Parameters(
                        description = "Member type. Valid values: ${COMPLETION-CANDIDATES}") MemberTypeEnum memberType) {
            GroupMember result = groupsApi.createGroupMembership(id,
                    new GroupMembershipBodyCreate().id(memberId).memberType(memberType), null)
                    .getBody().getEntry();
            formatProvider.print(result);
            return 0;
        }

        @Command(description = "Delete group member.")
        public Integer delete(@Parameters(description = "Group identifier") String id,
                @Parameters(description = "User Id") String personId) {
            groupsApi.deleteGroupMembership(id, personId);
            formatProvider.print(personId);
            return 0;
        }
    }

    @Component
    static class GroupProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final Group group = (Group) item;
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-80s", "ID", "DISPLAY", "PARENTS");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-80s", group.getId(), group.getDisplayName(), (group.getParentIds() == null ? "" : group.getParentIds()));
            System.out.println();
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && Group.class == itemClass;
        }
    }

    @Component
    static class GroupPagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final GroupPagingList groupList = (GroupPagingList) item;
            List<GroupEntry> entries = groupList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-80s", "ID", "DISPLAY", "PARENTS");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-40s %-40s %-80s", entry.getId(), entry.getDisplayName(), (entry.getParentIds() == null ? "" : entry.getParentIds()));
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && GroupPagingList.class == itemClass;
        }
    }

    @Component
    static class GroupMemberProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final GroupMember groupMember = (GroupMember) item;
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-40s", "ID", "DISPLAY", "TYPE");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-40s", groupMember.getId(), groupMember.getDisplayName(), groupMember.getMemberType());
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && GroupMember.class == itemClass;
        }
    }

    @Component
    static class GroupMemberPagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final GroupMemberPagingList groupMemberList = (GroupMemberPagingList) item;
            List<GroupMemberEntry> entries = groupMemberList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-40s", "ID", "DISPLAY", "TYPE");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-40s %-40s %-40s", entry.getId(), entry.getDisplayName(), entry.getMemberType());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && GroupMemberPagingList.class == itemClass;
        }
    }

}
