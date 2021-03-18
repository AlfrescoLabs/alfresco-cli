/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import org.alfresco.cli.format.FormatProvider;
import org.alfresco.cli.format.FormatProviderRegistry;
import org.alfresco.core.handler.GroupsApi;
import org.alfresco.core.model.*;
import org.alfresco.core.model.GroupMembershipBodyCreate.MemberTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "group", description = "Group commands", subcommands = {
        GroupCommand.ListGroup.class,
        GroupCommand.CreateGroup.class,
        GroupCommand.GetGroup.class,
        GroupCommand.UpdateGroup.class,
        GroupCommand.DeleteGroup.class,
        GroupCommand.ListGroupMember.class,
        GroupCommand.CreateGroupMember.class,
        GroupCommand.DeleteGroupMember.class})
public class GroupCommand {

    static abstract class AbstractGroupsCommand implements Callable<Integer> {

        @Mixin
        FormatProviderRegistry formatProvider;

        @Autowired
        GroupsApi groupsApi;

    }

    @Command(name = "list", description = "Get group list", mixinStandardHelpOptions = true)
    class ListGroup extends AbstractGroupsCommand {

        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                description = "Number of items to be skipped")
        Integer skipCount;
        @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                description = "Number of items to be returned")
        Integer maxItems;
        @Option(names = {"-w", "--where"},
                description = "Filter for returned items")
        String where;

        @Override
        public Integer call() throws Exception {
            ResponseEntity<GroupPaging> response =
                    groupsApi.listGroups(skipCount, maxItems, null, null, where, null);
            GroupPagingList result = response.getBody().getList();
            formatProvider.print(result);
            return 0;
        }
    }

    @Command(name = "create", description = "Create group", mixinStandardHelpOptions = true)
    class CreateGroup extends AbstractGroupsCommand {

        @Parameters(description = "Id of the Group")
        String id;
        @Option(names = {"-dn", "--displayName"}, required = true,
                description = "Display name")
        String displayName;
        @Option(names = {"-pi", "--parentIds"},
                description = "Parent group identifiers")
        List<String> parentIds;

        @Override
        public Integer call() throws Exception {
            Group result = groupsApi.createGroup(
                    new GroupBodyCreate().id(id).displayName(displayName).parentIds(parentIds), null,
                    null).getBody().getEntry();
            formatProvider.print(result);
            return 0;
        }
    }

    @Command(name = "get", description = "Get group details", mixinStandardHelpOptions = true)
    class GetGroup extends AbstractGroupsCommand {

        @Parameters(description = "Group identifier")
        String id;

        @Override
        public Integer call() throws Exception {
            Group result = groupsApi.getGroup(id, null, null).getBody().getEntry();
            formatProvider.print(result);
            return 0;
        }
    }

    @Command(name = "update", description = "Update group", mixinStandardHelpOptions = true)
    class UpdateGroup extends AbstractGroupsCommand {

        @Parameters(description = "Group identifier")
        String id;
        @Option(names = {"-dn", "--displayName"}, required = true,
                description = "Display name")
        String displayName;

        @Override
        public Integer call() throws Exception {
            Group result = groupsApi
                    .updateGroup(id, new GroupBodyUpdate().displayName(displayName), null, null)
                    .getBody().getEntry();
            formatProvider.print(result);
            return 0;
        }
    }

    @Command(name = "delete", description = "Delete group", mixinStandardHelpOptions = true)
    class DeleteGroup extends AbstractGroupsCommand {
        @Parameters(description = "Group identifier")
        String id;
        @Option(names = {"-c", "--cascade"}, defaultValue = "false",
                description = "Cascade deleted: true, false")
        Boolean cascade;

        @Override
        public Integer call() throws Exception {
            groupsApi.deleteGroup(id, cascade);
            formatProvider.print(id);
            return 0;
        }
    }

    @Command(name = "list-member", description = "List group members", mixinStandardHelpOptions = true)
    class ListGroupMember extends AbstractGroupsCommand {
        @Parameters(description = "Group identifier")
        String id;
        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                description = "Number of items to be skipped")
        Integer skipCount;
        @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                description = "Number of items to be returned")
        Integer maxItems;
        @Option(names = {"-w", "--where"},
                description = "Filter for returned items")
        String where;

        @Override
        public Integer call() throws Exception {
            GroupMemberPagingList result =
                    groupsApi.listGroupMemberships(id, skipCount, maxItems, null, where, null)
                            .getBody().getList();
            formatProvider.print(result);
            return 0;
        }
    }

    @Command(name = "create-member", description = "Create group member", mixinStandardHelpOptions = true)
    class CreateGroupMember extends AbstractGroupsCommand {
        @Parameters(description = "Group identifier")
        String id;
        @Parameters(description = "User Id")
        String memberId;
        @Parameters(
                description = "Member type. Valid values: ${COMPLETION-CANDIDATES}")
        MemberTypeEnum memberType;

        @Override
        public Integer call() throws Exception {
            GroupMember result = groupsApi.createGroupMembership(id,
                    new GroupMembershipBodyCreate().id(memberId).memberType(memberType), null)
                    .getBody().getEntry();
            formatProvider.print(result);
            return 0;
        }
    }

    @Command(name = "delete-member", description = "Delete group member", mixinStandardHelpOptions = true)
    class DeleteGroupMember extends AbstractGroupsCommand {
        @Parameters(description = "Group identifier")
        String id;
        @Parameters(description = "User Id")
        String personId;

        @Override
        public Integer call() throws Exception {
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
    static class GroupIdProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final Group group = (Group) item;
            System.out.printf(group.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && Group.class == itemClass;
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
    static class GroupPagingListIdsProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final GroupPagingList groupList = (GroupPagingList) item;
            List<GroupEntry> entries = groupList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && GroupPagingList.class == itemClass;
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
    static class GroupMemberIdProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final GroupMember groupMember = (GroupMember) item;
            System.out.printf(groupMember.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && GroupMember.class == itemClass;
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

    @Component
    static class GroupMemberPagingListIdProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final GroupMemberPagingList groupMemberList = (GroupMemberPagingList) item;
            List<GroupMemberEntry> entries = groupMemberList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && GroupMemberPagingList.class == itemClass;
        }
    }

}
