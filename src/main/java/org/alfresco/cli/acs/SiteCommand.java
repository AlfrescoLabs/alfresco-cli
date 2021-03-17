package org.alfresco.cli.acs;

import java.util.List;
import org.alfresco.cli.acs.SiteCommand.SiteContainerCommand;
import org.alfresco.cli.acs.SiteCommand.SiteMemberCommand;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.Site;
import org.alfresco.core.model.SiteBodyCreate;
import org.alfresco.core.model.SiteBodyUpdate;
import org.alfresco.core.model.SiteContainer;
import org.alfresco.core.model.SiteContainerEntry;
import org.alfresco.core.model.SiteEntry;
import org.alfresco.core.model.SiteMember;
import org.alfresco.core.model.SiteMemberEntry;
import org.alfresco.core.model.SiteMembershipBodyCreate;
import org.alfresco.core.model.SiteMembershipBodyUpdate;
import org.alfresco.core.model.SiteRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "site", subcommands = {SiteContainerCommand.class, SiteMemberCommand.class},
        description = "Site commands.")
public class SiteCommand {

    @Autowired
    SitesApi sitesApi;

    @Command(description = "Get site list.")
    public Integer list(
            @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                    description = "Number of items to be skipped") Integer skipCount,
            @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                    description = "Number of items to be returned") Integer maxItems,
            @Option(names = {"-w", "--where"},
                    description = "Filter for returned sites") String where) {
        List<SiteEntry> sites = sitesApi.listSites(skipCount, maxItems, null, null, null, where)
                .getBody().getList().getEntries();
        System.out.println(sites);
        return 0;
    }

    @Command(description = "Create site.")
    public Integer create(@Parameters(description = "Id of the Site") String id,
            @Option(names = {"-d", "--description"},
                    description = "Description of the Site") String description,
            @Option(names = {"-t", "--title"}, required = true,
                    description = "Title of the Site") String title,
            @Option(names = {"-v", "--visibility"}, required = true,
                    description = "Visibility of the Site. Valid values: ${COMPLETION-CANDIDATES}") SiteBodyCreate.VisibilityEnum visibility) {
        Site site = sitesApi.createSite(new SiteBodyCreate().id(id).description(description)
                .title(title).visibility(visibility), null, null, null).getBody().getEntry();
        System.out.println(site);
        return 0;
    }

    @Command(description = "Get site details.")
    public Integer get(@Parameters(description = "Id of the Site") String id) {
        Site site = sitesApi.getSite(id, null, null).getBody().getEntry();
        System.out.println(site);
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
        System.out.println(site);
        return 0;
    }

    @Command(description = "Delete site.")
    public Integer delete(@Parameters(description = "Id of the Site") String id,
            @Option(names = {"-p", "--permanent"}, defaultValue = "false",
                    description = "Permanently deleted: true, false") Boolean permanent) {
        sitesApi.deleteSite(id, permanent);
        System.out.println(id);
        return 0;
    }

    @Component
    @Command(name = "container", description = "Site container commands.")
    static class SiteContainerCommand {

        @Autowired
        SitesApi sitesApi;

        @Command(description = "Get site container list.")
        public Integer list(@Parameters(description = "Id of the Site") String id,
                @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                        description = "Number of items to be skipped") Integer skipCount,
                @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                        description = "Number of items to be returned") Integer maxItems) {
            List<SiteContainerEntry> siteContainers =
                    sitesApi.listSiteContainers(id, skipCount, maxItems, null).getBody().getList()
                            .getEntries();
            System.out.println(siteContainers);
            return 0;
        }

        @Command(description = "Get site container details")
        public Integer get(@Parameters(description = "Id of the Site") String id, @Parameters(
                description = "Id of the Container: documentLibrary, dataLists, discussions, links, wiki") String containerId) {
            SiteContainer siteContainer =
                    sitesApi.getSiteContainer(id, containerId, null).getBody().getEntry();
            System.out.println(siteContainer);
            return 0;
        }
    }

    @Component
    @Command(name = "member", description = "Site members commands.")
    static class SiteMemberCommand {

        @Autowired
        SitesApi sitesApi;

        @Command(description = "Get site members list.")
        public Integer list(@Parameters(description = "Id of the Site") String id,
                @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                        description = "Number of items to be skipped") Integer skipCount,
                @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                        description = "Number of items to be returned") Integer maxItems) {
            List<SiteMemberEntry> members =
                    sitesApi.listSiteMemberships(id, skipCount, maxItems, null).getBody().getList()
                            .getEntries();
            System.out.println(members);
            return 0;
        }

        @Command(description = "Create site member.")
        public Integer create(@Parameters(description = "Id of the Site") String id,
                @Parameters(description = "User Id") String userId, @Parameters(
                        description = "Role in the Site. Valid values: ${COMPLETION-CANDIDATES}") SiteMembershipBodyCreate.RoleEnum role) {
            SiteMember siteMember = sitesApi.createSiteMembership(id,
                    new SiteMembershipBodyCreate().id(userId).role(role), null).getBody()
                    .getEntry();
            System.out.println(siteMember);
            return 0;
        }

        @Command(description = "Get site member details.")
        public Integer get(@Parameters(description = "Id of the Site") String id,
                @Parameters(description = "User Id") String personId) {
            SiteRole siteRole =
                    sitesApi.getSiteMembershipForPerson(personId, id).getBody().getEntry();
            System.out.println(siteRole);
            return 0;
        }

        @Command(description = "Update site member.")
        public Integer update(@Parameters(description = "Id of the Site") String id,
                @Parameters(description = "User Id") String personId, @Parameters(
                        description = "Role in the Site. Valid values: ${COMPLETION-CANDIDATES}") SiteMembershipBodyUpdate.RoleEnum role) {
            SiteMember siteMember = sitesApi.updateSiteMembership(id, personId,
                    new SiteMembershipBodyUpdate().role(role), null).getBody().getEntry();
            System.out.println(siteMember);
            return 0;
        }

        @Command(description = "Delete site member.")
        public Integer delete(@Parameters(description = "Id of the Site") String id,
                @Parameters(description = "User Id") String personId) {
            sitesApi.deleteSiteMembership(id, personId);
            System.out.println(personId);
            return 0;
        }
    }

    // TODO Add "/sites/{siteId}/group-members" when endpoints are available in SDK

}
