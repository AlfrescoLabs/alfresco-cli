package org.alfresco.cli.acs;

import org.alfresco.cli.format.FormatProvider;
import org.alfresco.cli.format.FormatProviderRegistry;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "site", subcommands = {
        SiteCommand.ListSite.class,
        SiteCommand.CreateSite.class,
        SiteCommand.GetSite.class,
        SiteCommand.UpdateSite.class,
        SiteCommand.DeleteSite.class,
        SiteCommand.ListSiteContainer.class,
        SiteCommand.GetSiteContainer.class,
        SiteCommand.ListSiteMember.class,
        SiteCommand.CreateSiteMember.class,
        SiteCommand.GetSiteMember.class,
        SiteCommand.UpdateSiteMember.class,
        SiteCommand.DeleteSiteMember.class,
        SiteCommand.ListGroupMember.class,
        SiteCommand.CreateGroupMember.class,
        SiteCommand.GetGroupMember.class,
        SiteCommand.UpdateGroupMember.class,
        SiteCommand.DeleteGroupMember.class},
        description = "Site commands")
public class SiteCommand {

    static abstract class AbstractSiteCommand implements Callable<Integer> {

        @Autowired
        SitesApi sitesApi;

        @Mixin
        FormatProviderRegistry formatProvider;

    }

    @Command(name = "list", description = "Get site list", mixinStandardHelpOptions = true)
    class ListSite extends AbstractSiteCommand {
        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                description = "Number of items to be skipped")
        Integer skipCount;
        @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                description = "Number of items to be returned")
        Integer maxItems;
        @Option(names = {"-w", "--where"},
                description = "Filter for returned sites")
        String where;

        @Override
        public Integer call() throws Exception {
            SitePagingList sites = sitesApi.listSites(skipCount, maxItems, null, null, null, where)
                    .getBody().getList();
            formatProvider.print(sites);
            return 0;
        }
    }

    @Command(name = "create", description = "Create site", mixinStandardHelpOptions = true)
    class CreateSite extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Option(names = {"-d", "--description"},
                description = "Description of the Site")
        String description;
        @Option(names = {"-t", "--title"}, required = true,
                description = "Title of the Site")
        String title;
        @Option(names = {"-v", "--visibility"}, required = true,
                description = "Visibility of the Site. Valid values: ${COMPLETION-CANDIDATES}")
        SiteBodyCreate.VisibilityEnum visibility;

        @Override
        public Integer call() throws Exception {
            Site site = sitesApi.createSite(new SiteBodyCreate().id(id).description(description)
                    .title(title).visibility(visibility), null, null, null).getBody().getEntry();
            formatProvider.print(site);
            return 0;
        }
    }

    @Command(name = "get", description = "Get site details", mixinStandardHelpOptions = true)
    class GetSite extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;

        @Override
        public Integer call() throws Exception {
            Site site = sitesApi.getSite(id, null, null).getBody().getEntry();
            formatProvider.print(site);
            return 0;
        }
    }

    @Command(name = "update", description = "Update site", mixinStandardHelpOptions = true)
    class UpdateSite extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Option(names = {"-d", "--description"}, required = true,
                description = "Description of the Site")
        String description;
        @Option(names = {"-t", "--title"}, required = true,
                description = "Title of the Site")
        String title;
        @Option(names = {"-v", "--visibility"}, required = true,
                description = "Visibility of the Site. Valid values: ${COMPLETION-CANDIDATES}")
        SiteBodyUpdate.VisibilityEnum visibility;

        @Override
        public Integer call() throws Exception {
            Site site = sitesApi.updateSite(id,
                    new SiteBodyUpdate().title(title).description(description).visibility(visibility),
                    null).getBody().getEntry();
            formatProvider.print(site);
            return 0;
        }
    }

    @Command(name = "delete", description = "Delete site", mixinStandardHelpOptions = true)
    class DeleteSite extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Option(names = {"-p", "--permanent"}, defaultValue = "false",
                description = "Permanently deleted: true, false")
        Boolean permanent;

        @Override
        public Integer call() throws Exception {
            sitesApi.deleteSite(id, permanent);
            formatProvider.print(id);
            return 0;
        }
    }

    @Command(name = "list-container", description = "Get site container list", mixinStandardHelpOptions = true)
    class ListSiteContainer extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                description = "Number of items to be skipped")
        Integer skipCount;
        @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                description = "Number of items to be returned")
        Integer maxItems;

        @Override
        public Integer call() throws Exception {
            SiteContainerPagingList siteContainers =
                    sitesApi.listSiteContainers(id, skipCount, maxItems, null).getBody().getList();
            formatProvider.print(siteContainers);
            return 0;
        }
    }

    @Command(name = "get-container", description = "Get site container details", mixinStandardHelpOptions = true)
    class GetSiteContainer extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(
                description = "Id of the Container: documentLibrary, dataLists, discussions, links, wiki")
        String containerId;

        @Override
        public Integer call() throws Exception {
            SiteContainer siteContainer =
                    sitesApi.getSiteContainer(id, containerId, null).getBody().getEntry();
            formatProvider.print(siteContainer);
            return 0;
        }
    }

    @Command(name = "list-member", description = "Get site members list", mixinStandardHelpOptions = true)
    class ListSiteMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                description = "Number of items to be skipped")
        Integer skipCount;
        @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                description = "Number of items to be returned")
        Integer maxItems;
        @Option(names = {"-w", "--where"},
                description = "Filter for returned sites")
        String where;

        @Override
        public Integer call() throws Exception {
            SiteMemberPagingList members =
                    sitesApi.listSiteMemberships(id, skipCount, maxItems, null, where).getBody().getList();
            formatProvider.print(members);
            return 0;
        }
    }

    @Command(name = "create-member", description = "Create site member", mixinStandardHelpOptions = true)
    class CreateSiteMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(description = "User Id")
        String userId;
        @Parameters(
                description = "Role in the Site. Valid values: ${COMPLETION-CANDIDATES}")
        SiteMembershipBodyCreate.RoleEnum role;

        @Override
        public Integer call() throws Exception {
            SiteMember siteMember = sitesApi.createSiteMembership(id,
                    new SiteMembershipBodyCreate().id(userId).role(role), null).getBody()
                    .getEntry();
            formatProvider.print(siteMember);
            return 0;
        }
    }

    @Command(name = "get-member", description = "Get site member details", mixinStandardHelpOptions = true)
    class GetSiteMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(description = "User Id")
        String personId;

        @Override
        public Integer call() throws Exception {
            SiteRole siteRole =
                    sitesApi.getSiteMembershipForPerson(personId, id).getBody().getEntry();
            formatProvider.print(siteRole);
            return 0;
        }
    }

    @Command(name = "update-member", description = "Update site member", mixinStandardHelpOptions = true)
    class UpdateSiteMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(description = "User Id")
        String personId;
        @Parameters(
                description = "Role in the Site. Valid values: ${COMPLETION-CANDIDATES}")
        SiteMembershipBodyUpdate.RoleEnum role;

        @Override
        public Integer call() throws Exception {
            SiteMember siteMember = sitesApi.updateSiteMembership(id, personId,
                    new SiteMembershipBodyUpdate().role(role), null).getBody().getEntry();
            formatProvider.print(siteMember);
            return 0;
        }
    }

    @Command(name = "delete-member", description = "Delete site member", mixinStandardHelpOptions = true)
    class DeleteSiteMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(description = "User Id")
        String personId;

        @Override
        public Integer call() throws Exception {
            sitesApi.deleteSiteMembership(id, personId);
            formatProvider.print(personId);
            return 0;
        }
    }

    @Command(name = "list-group-member", description = "Get group member list", mixinStandardHelpOptions = true)
    class ListGroupMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                description = "Number of items to be skipped")
        Integer skipCount;
        @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                description = "Number of items to be returned")
        Integer maxItems;

        @Override
        public Integer call() throws Exception {
            SiteGroupPagingList members =
                    sitesApi.listSiteGroups(id, skipCount, maxItems, null).getBody().getList();
            formatProvider.print(members);
            return 0;
        }
    }

    @Command(name = "create-group-member", description = "Create site group member", mixinStandardHelpOptions = true)
    class CreateGroupMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(description = "Group Id")
        String groupId;
        @Parameters(description = "Role in the Site. Valid values: ${COMPLETION-CANDIDATES}")
        SiteMembershipBodyCreate.RoleEnum role;

        @Override
        public Integer call() throws Exception {
            SiteGroup siteMember = sitesApi.createSiteGroupMembership(id,
                    new SiteMembershipBodyCreate().id(groupId).role(role), null).getBody()
                    .getEntry();
            formatProvider.print(siteMember);
            return 0;
        }
    }

    @Command(name = "get-group-member", description = "Get site group member details", mixinStandardHelpOptions = true)
    class GetGroupMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(description = "Group Id")
        String groupId;

        @Override
        public Integer call() throws Exception {
            SiteGroup siteGroup =
                    sitesApi.getSiteGroupMembership(id, groupId, null).getBody().getEntry();
            formatProvider.print(siteGroup);
            return 0;
        }
    }

    @Command(name = "update-group-member", description = "Update site group member", mixinStandardHelpOptions = true)
    class UpdateGroupMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(description = "Group Id")
        String groupId;
        @Parameters(
                description = "Role in the Site. Valid values: ${COMPLETION-CANDIDATES}")
        SiteMembershipBodyUpdate.RoleEnum role;

        @Override
        public Integer call() throws Exception {
            SiteGroup siteGroup = sitesApi.updateSiteGroupMembership(id, groupId,
                    new SiteMembershipBodyUpdate().role(role), null).getBody().getEntry();
            formatProvider.print(siteGroup);
            return 0;
        }
    }


    @Command(name = "delete-group-member", description = "Delete site group member", mixinStandardHelpOptions = true)
    class DeleteGroupMember extends AbstractSiteCommand {
        @Parameters(description = "Id of the Site")
        String id;
        @Parameters(description = "User Id")
        String groupId;

        @Override
        public Integer call() throws Exception {
            sitesApi.deleteSiteGroupMembership(id, groupId);
            formatProvider.print(groupId);
            return 0;
        }
    }

    @Component
    static class SiteProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final Site site = (Site) item;
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-20s %-40s %-20s", "ID", "TITLE", "VISIBILITY");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-20s %-40s %-20s", site.getId(), site.getTitle(), site.getVisibility());
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && Site.class == itemClass;
        }
    }

    @Component
    static class SiteIdProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final Site site = (Site) item;
            System.out.printf(site.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && Site.class == itemClass;
        }
    }

    @Component
    static class SitePagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SitePagingList siteList = (SitePagingList) item;
            List<SiteEntry> entries = siteList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-20s %-40s %-20s", "ID", "TITLE", "VISIBILITY");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-20s %-40s %-20s", entry.getId(), entry.getTitle(), entry.getVisibility());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SitePagingList.class == itemClass;
        }
    }


    @Component
    static class SitePagingListIdsProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SitePagingList siteList = (SitePagingList) item;
            List<SiteEntry> entries = siteList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SitePagingList.class == itemClass;
        }
    }

    @Component
    static class SiteContainerProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteContainer siteContainer = (SiteContainer) item;
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s", "ID", "FOLDER-ID");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s", siteContainer.getId(), siteContainer.getFolderId());
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SiteContainer.class == itemClass;
        }
    }

    @Component
    static class SiteContainerIdProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteContainer siteContainer = (SiteContainer) item;
            System.out.println(siteContainer.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SiteContainer.class == itemClass;
        }
    }

    @Component
    static class SiteContainerPagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteContainerPagingList siteContainerList = (SiteContainerPagingList) item;
            List<SiteContainerEntry> entries = siteContainerList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s", "ID", "FOLDER-ID");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-40s %-40s", entry.getId(), entry.getFolderId());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SiteContainerPagingList.class == itemClass;
        }
    }

    @Component
    static class SiteContainerPagingListIdsProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteContainerPagingList siteContainerList = (SiteContainerPagingList) item;
            List<SiteContainerEntry> entries = siteContainerList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SiteContainerPagingList.class == itemClass;
        }
    }

    @Component
    static class SiteRoleProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteRole siteRole = (SiteRole) item;
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s %-20s", "ID", "ROLE", "VISIBILITY");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s %-20s", siteRole.getId(), siteRole.getRole(), siteRole.getSite().getVisibility());
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SiteRole.class == itemClass;
        }
    }

    @Component
    static class SiteRoleIdProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteRole siteRole = (SiteRole) item;
            System.out.printf(siteRole.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SiteRole.class == itemClass;
        }
    }

    @Component
    static class SiteRolePagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteRolePagingList siteRoleList = (SiteRolePagingList) item;
            List<SiteRoleEntry> entries = siteRoleList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s %-20s", "ID", "ROLE", "VISIBILITY");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-40s %-20s %-20s", entry.getId(), entry.getRole(), entry.getSite().getVisibility());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SiteRolePagingList.class == itemClass;
        }
    }

    @Component
    static class SiteRolePagingListIdsProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteRolePagingList siteRoleList = (SiteRolePagingList) item;
            List<SiteRoleEntry> entries = siteRoleList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SiteRolePagingList.class == itemClass;
        }
    }

    @Component
    static class SiteGroupProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteGroup siteRole = (SiteGroup) item;
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s", "ID", "ROLE");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s", siteRole.getId(), siteRole.getRole());
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SiteGroup.class == itemClass;
        }
    }

    @Component
    static class SiteGroupIdProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteGroup siteGroup = (SiteGroup) item;
            System.out.printf(siteGroup.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SiteGroup.class == itemClass;
        }
    }

    @Component
    static class SiteGroupPagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteGroupPagingList siteGroupList = (SiteGroupPagingList) item;
            List<SiteGroupEntry> entries = siteGroupList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s", "ID", "ROLE");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-40s %-20s", entry.getId(), entry.getRole());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SiteGroupPagingList.class == itemClass;
        }
    }

    @Component
    static class SiteGroupPagingListIdsProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteGroupPagingList siteGroupList = (SiteGroupPagingList) item;
            List<SiteGroupEntry> entries = siteGroupList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SiteGroupPagingList.class == itemClass;
        }
    }

    @Component
    static class SiteMemberProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteMember siteMember = (SiteMember) item;
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s", "ID", "ROLE");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s", siteMember.getId(), siteMember.getRole());
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SiteMember.class == itemClass;
        }
    }

    @Component
    static class SiteMemberIdProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteMember siteMember = (SiteMember) item;
            System.out.printf(siteMember.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SiteMember.class == itemClass;
        }
    }

    @Component
    static class SiteMemberPagingListProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteMemberPagingList siteMemberList = (SiteMemberPagingList) item;
            List<SiteMemberEntry> entries = siteMemberList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-20s", "ID", "ROLE");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-40s %-20s", entry.getId(), entry.getRole());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && SiteMemberPagingList.class == itemClass;
        }
    }

    @Component
    static class SiteMemberPagingListIdsProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final SiteMemberPagingList siteMemberList = (SiteMemberPagingList) item;
            List<SiteMemberEntry> entries = siteMemberList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && SiteMemberPagingList.class == itemClass;
        }
    }

}
