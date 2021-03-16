package org.alfresco.cli.acs;

import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "sites", mixinStandardHelpOptions = true,
        subcommands = {SitesCommand.ListSiteCommand.class,
                SitesCommand.CreateSiteCommand.class,
                SitesCommand.GetSiteCommand.class,
                SitesCommand.UpdateSiteCommand.class,
                SitesCommand.DeleteSiteCommand.class,
                SitesCommand.ListContainerSiteCommand.class,
                SitesCommand.GetContainerSiteCommand.class,
                SitesCommand.ListMemberSiteCommand.class,
                SitesCommand.CreateMemberSiteCommand.class,
                SitesCommand.GetMemberSiteCommand.class,
                SitesCommand.UpdateMemberSiteCommand.class,
                SitesCommand.DeleteMemberSiteCommand.class},
        exitCodeOnExecutionException = 1)
public class SitesCommand {

    @Autowired
    SitesApi sitesApi;

    @Component
    @Command(name = "list", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class ListSiteCommand implements Callable<Integer> {

        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0", description = "Number of sites to be skipped")
        Integer skipCount;

        @Option(names = {"-mi", "--max-items"}, defaultValue = "100", description = "Number of sites to be recovered")
        Integer maxItems;

        @Override
        public Integer call() throws Exception {
            List<SiteEntry> sites = sitesApi.listSites(skipCount, maxItems, null, null, null, null).getBody().getList().getEntries();
            System.out.println(sites);
            return 0;
        }
    }

    @Component
    @Command(name = "create", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class CreateSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-d", "--description"}, description = "Description of the Site")
        String description;

        @Option(names = {"-t", "--title"}, required = true, description = "Title of the Site")
        String title;

        @Option(names = {"-v", "--visibility"}, required = true, description = "Visibility of the Site: PUBLIC, PRIVATE, MODERATED")
        String visibility;

        @Override
        public Integer call() throws Exception {
            Site site = sitesApi.createSite(
                    new SiteBodyCreate()
                            .id(id)
                            .description(description)
                            .title(title)
                            .visibility(SiteBodyCreate.VisibilityEnum.fromValue(visibility)),
                    null, null, null).getBody().getEntry();
            System.out.println(site);
            return 0;
        }
    }

    @Component
    @Command(name = "get", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class GetSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Override
        public Integer call() throws Exception {
            Site site = sitesApi.getSite(id, null, null).getBody().getEntry();
            System.out.println(site);
            return 0;
        }
    }

    @Component
    @Command(name = "update", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class UpdateSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-d", "--description"}, required = true, description = "Description of the Site")
        String description;

        @Option(names = {"-t", "--title"}, required = true, description = "Title of the Site")
        String title;

        @Option(names = {"-v", "--visibility"}, required = true, description = "Visibility of the Site: PUBLIC, PRIVATE, MODERATED")
        String visibility;

        @Override
        public Integer call() throws Exception {
            Site site = sitesApi.updateSite(id,
                    new SiteBodyUpdate().title(title).description(description).visibility(SiteBodyUpdate.VisibilityEnum.fromValue(visibility)),
                    null)
                    .getBody().getEntry();
            System.out.println(site);
            return 0;
        }
    }

    @Component
    @Command(name = "delete", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class DeleteSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-p", "--permanent"}, defaultValue = "false", description = "Permanently deleted: true, false")
        Boolean permanent;

        @Override
        public Integer call() throws Exception {
            sitesApi.deleteSite(id, permanent);
            System.out.println(id);
            return 0;
        }
    }

    @Component
    @Command(name = "list-containers", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class ListContainerSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0", description = "Number of sites to be skipped")
        Integer skipCount;

        @Option(names = {"-mi", "--max-items"}, defaultValue = "100", description = "Number of sites to be recovered")
        Integer maxItems;

        @Override
        public Integer call() throws Exception {
            List<SiteContainerEntry> siteContainers = sitesApi.listSiteContainers(id, skipCount, maxItems, null).getBody().getList().getEntries();
            System.out.println(siteContainers);
            return 0;
        }
    }

    @Component
    @Command(name = "get-container", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class GetContainerSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-ci", "--container-id"}, required = true, description = "Id of the Container: documentLibrary, dataLists, discussions, links, wiki")
        String containerId;

        @Override
        public Integer call() throws Exception {
            SiteContainer siteContainer = sitesApi.getSiteContainer(id, containerId, null).getBody().getEntry();
            System.out.println(siteContainer);
            return 0;
        }
    }

    @Component
    @Command(name = "list-members", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class ListMemberSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0", description = "Number of sites to be skipped")
        Integer skipCount;

        @Option(names = {"-mi", "--max-items"}, defaultValue = "100", description = "Number of sites to be recovered")
        Integer maxItems;

        @Override
        public Integer call() throws Exception {
            List<SiteMemberEntry> members = sitesApi.listSiteMemberships(id, skipCount, maxItems, null).getBody().getList().getEntries();
            System.out.println(members);
            return 0;
        }
    }

    @Component
    @Command(name = "create-member", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class CreateMemberSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @CommandLine.ArgGroup(exclusive = false, multiplicity = "1", heading = "Member Values")
        MemberValues memberValues;

        class MemberValues {
            @Option(names = {"-mi", "--member-id"}, required = true, description = "User Id")
            String id;
            @Option(names = {"-mr", "--member-role"}, required = true, description = "Role in the Site: SiteConsumer, SiteCollaborator, SiteContributor, SiteManager")
            String role;
        }

        @Override
        public Integer call() throws Exception {
            SiteMember siteMember = sitesApi.createSiteMembership(id,
                    new SiteMembershipBodyCreate()
                            .id(memberValues.id)
                            .role(SiteMembershipBodyCreate.RoleEnum.fromValue(memberValues.role)), null)
                    .getBody().getEntry();
            System.out.println(siteMember);
            return 0;
        }
    }

    @Component
    @Command(name = "get-member", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class GetMemberSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-mi", "--member-id"}, required = true, description = "User Id")
        String personId;

        @Override
        public Integer call() throws Exception {
            SiteRole siteRole = sitesApi.getSiteMembershipForPerson(personId, id).getBody().getEntry();
            System.out.println(siteRole);
            return 0;
        }
    }

    @Component
    @Command(name = "update-member", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class UpdateMemberSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-mi", "--member-id"}, required = true, description = "User Id")
        String personId;

        @Option(names = {"-mr", "--member-role"}, required = true, description = "Role in the Site: SiteConsumer, SiteCollaborator, SiteContributor, SiteManager")
        String role;

        @Override
        public Integer call() throws Exception {
            SiteMember siteMember = sitesApi.updateSiteMembership(id, personId,
                    new SiteMembershipBodyUpdate()
                            .role(SiteMembershipBodyUpdate.RoleEnum.fromValue(role)), null).getBody().getEntry();
            System.out.println(siteMember);
            return 0;
        }
    }

    @Component
    @Command(name = "delete-member", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
    class DeleteMemberSiteCommand implements Callable<Integer> {

        @Option(names = {"-id", "--id"}, required = true, description = "Id of the Site")
        String id;

        @Option(names = {"-mi", "--member-id"}, required = true, description = "User Id")
        String personId;

        @Override
        public Integer call() throws Exception {
            sitesApi.deleteSiteMembership(id, personId);
            System.out.println(personId);
            return 0;
        }
    }

    // TODO Add "/sites/{siteId}/group-members" when endpoints are available in SDK

}
