package org.alfresco.cli.acs;

import org.alfresco.cli.acs.SiteCommand.SiteContainerCommand;
import org.alfresco.cli.acs.SiteCommand.SiteMemberCommand;
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

@Component
@Command(name = "site", subcommands = {SiteContainerCommand.class, SiteMemberCommand.class},
        description = "Site commands")
public class SiteCommand {

    @Autowired
    SitesApi sitesApi;

    @Mixin
    FormatProviderRegistry formatProvider;

    @Command(description = "Get site list")
    public Integer list(
            @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                    description = "Number of items to be skipped") Integer skipCount,
            @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                    description = "Number of items to be returned") Integer maxItems,
            @Option(names = {"-w", "--where"},
                    description = "Filter for returned sites") String where) {
        SitePagingList sites = sitesApi.listSites(skipCount, maxItems, null, null, null, where)
                .getBody().getList();
        formatProvider.print(sites);
        return 0;
    }

    @Command(description = "Create site")
    public Integer create(@Parameters(description = "Id of the Site") String id,
            @Option(names = {"-d", "--description"},
                    description = "Description of the Site") String description,
            @Option(names = {"-t", "--title"}, required = true,
                    description = "Title of the Site") String title,
            @Option(names = {"-v", "--visibility"}, required = true,
                    description = "Visibility of the Site. Valid values: ${COMPLETION-CANDIDATES}") SiteBodyCreate.VisibilityEnum visibility) {
        Site site = sitesApi.createSite(new SiteBodyCreate().id(id).description(description)
                .title(title).visibility(visibility), null, null, null).getBody().getEntry();
        formatProvider.print(site);
        return 0;
    }

    @Command(description = "Get site details")
    public Integer get(@Parameters(description = "Id of the Site") String id) {
        Site site = sitesApi.getSite(id, null, null).getBody().getEntry();
        formatProvider.print(site);
        return 0;
    }

    @Command(name = "update")
    public Integer update(@Parameters(description = "Id of the Site") String id,
            @Option(names = {"-d", "--description"}, required = true,
                    description = "Description of the Site") String description,
            @Option(names = {"-t", "--title"}, required = true,
                    description = "Title of the Site") String title,
            @Option(names = {"-v", "--visibility"}, required = true,
                    description = "Visibility of the Site. Valid values: ${COMPLETION-CANDIDATES}") SiteBodyUpdate.VisibilityEnum visibility) {
        Site site = sitesApi.updateSite(id,
                new SiteBodyUpdate().title(title).description(description).visibility(visibility),
                null).getBody().getEntry();
        formatProvider.print(site);
        return 0;
    }

    @Command(description = "Delete site")
    public Integer delete(@Parameters(description = "Id of the Site") String id,
            @Option(names = {"-p", "--permanent"}, defaultValue = "false",
                    description = "Permanently deleted: true, false") Boolean permanent) {
        sitesApi.deleteSite(id, permanent);
        formatProvider.print(id);
        return 0;
    }

    @Component
    @Command(name = "container", description = "Site container commands")
    class SiteContainerCommand {

        @Autowired
        SitesApi sitesApi;

        @Mixin
        FormatProviderRegistry formatProvider;

        @Command(description = "Get site container list")
        public Integer list(@Parameters(description = "Id of the Site") String id,
                @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                        description = "Number of items to be skipped") Integer skipCount,
                @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                        description = "Number of items to be returned") Integer maxItems) {
            SiteContainerPagingList siteContainers =
                    sitesApi.listSiteContainers(id, skipCount, maxItems, null).getBody().getList();
            formatProvider.print(siteContainers);
            return 0;
        }

        @Command(description = "Get site container details")
        public Integer get(@Parameters(description = "Id of the Site") String id, @Parameters(
                description = "Id of the Container: documentLibrary, dataLists, discussions, links, wiki") String containerId) {
            SiteContainer siteContainer =
                    sitesApi.getSiteContainer(id, containerId, null).getBody().getEntry();
            formatProvider.print(siteContainer);
            return 0;
        }
    }

    @Component
    @Command(name = "member", description = "Site members commands")
    class SiteMemberCommand {

        @Autowired
        SitesApi sitesApi;

        @Mixin
        FormatProviderRegistry formatProvider;

        @Command(description = "Get site members list")
        public Integer list(@Parameters(description = "Id of the Site") String id,
                            @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                                    description = "Number of items to be skipped") Integer skipCount,
                            @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                                    description = "Number of items to be returned") Integer maxItems,
                            @Option(names = {"-w", "--where"},
                                    description = "Filter for returned sites") String where) {
            SiteMemberPagingList members =
                    sitesApi.listSiteMemberships(id, skipCount, maxItems, null, where).getBody().getList();
            formatProvider.print(members);
            return 0;
        }

        @Command(description = "Create site member")
        public Integer create(@Parameters(description = "Id of the Site") String id,
                @Parameters(description = "User Id") String userId, @Parameters(
                        description = "Role in the Site. Valid values: ${COMPLETION-CANDIDATES}") SiteMembershipBodyCreate.RoleEnum role) {
            SiteMember siteMember = sitesApi.createSiteMembership(id,
                    new SiteMembershipBodyCreate().id(userId).role(role), null).getBody()
                    .getEntry();
            formatProvider.print(siteMember);
            return 0;
        }

        @Command(description = "Get site member details")
        public Integer get(@Parameters(description = "Id of the Site") String id,
                @Parameters(description = "User Id") String personId) {
            SiteRole siteRole =
                    sitesApi.getSiteMembershipForPerson(personId, id).getBody().getEntry();
            formatProvider.print(siteRole);
            return 0;
        }

        @Command(description = "Update site member")
        public Integer update(@Parameters(description = "Id of the Site") String id,
                @Parameters(description = "User Id") String personId, @Parameters(
                        description = "Role in the Site. Valid values: ${COMPLETION-CANDIDATES}") SiteMembershipBodyUpdate.RoleEnum role) {
            SiteMember siteMember = sitesApi.updateSiteMembership(id, personId,
                    new SiteMembershipBodyUpdate().role(role), null).getBody().getEntry();
            formatProvider.print(siteMember);
            return 0;
        }

        @Command(description = "Delete site member")
        public Integer delete(@Parameters(description = "Id of the Site") String id,
                @Parameters(description = "User Id") String personId) {
            sitesApi.deleteSiteMembership(id, personId);
            formatProvider.print(personId);
            return 0;
        }
    }

    // TODO Add "/sites/{siteId}/group-members" when endpoints are available in SDK

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
            System.out.printf("%-20s %-20s %-20s", "ID", "ROLE", "VISIBILITY");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-20s %-20s %-20s", siteRole.getId(), siteRole.getRole(), siteRole.getSite().getVisibility());
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
            System.out.printf("%-20s %-20s %-20s", "ID", "ROLE", "VISIBILITY");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-20s %-20s %-20s", entry.getId(), entry.getRole(), entry.getSite().getVisibility());
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
}
