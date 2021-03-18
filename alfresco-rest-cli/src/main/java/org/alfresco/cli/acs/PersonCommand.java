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
import org.alfresco.core.handler.PeopleApi;
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
@Command(name = "person", description = "Person commands", subcommands = {
        PersonCommand.ListPerson.class,
        PersonCommand.CreatePerson.class,
        PersonCommand.GetPerson.class,
        PersonCommand.UpdatePerson.class,
        PersonCommand.ListPersonGroup.class,
        PersonCommand.PersonSiteList.class,
        PersonCommand.PersonSiteGet.class,
        PersonCommand.PersonSiteDelete.class
})
public class PersonCommand {

    static abstract class AbstractPersonCommand implements Callable<Integer> {

        @Autowired
        PeopleApi peopleApi;

        @Mixin
        FormatProviderRegistry formatProvider;

    }

    @Command(name = "list", description = "Get people list", mixinStandardHelpOptions = true)
    class ListPerson extends AbstractPersonCommand {

        @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                description = "Number of items to be skipped")
        Integer skipCount;
        @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                description = "Number of items to be returned")
        Integer maxItems;

        @Override
        public Integer call() throws Exception {
            PersonPagingList sites = peopleApi.listPeople(skipCount, maxItems, null, null, null)
                    .getBody().getList();
            formatProvider.print(sites);
            return 0;
        }
    }

    @Command(name = "create", description = "Create person", mixinStandardHelpOptions = true)
    class CreatePerson extends AbstractPersonCommand {
        @Parameters(description = "Id of the Person")
        String id;
        @Option(names = {"-fn", "--first-name"}, required = true,
                description = "First Name of the Person")
        String firstName;
        @Option(names = {"-ln", "--last-name"}, required = true,
                description = "Last Name of the Person")
        String lastName;
        @Option(names = {"-e", "--email"}, required = true,
                description = "Email of the Person")
        String email;
        @Option(names = {"-p", "--password"}, required = true,
                description = "Password of the Person")
        String password;

        @Override
        public Integer call() throws Exception {
            Person person = peopleApi.createPerson(new PersonBodyCreate()
                    .id(id)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(password)
                    .enabled(true), null)
                    .getBody().getEntry();
            formatProvider.print(person);
            return 0;
        }
    }

    @Command(name = "get", description = "Get person details", mixinStandardHelpOptions = true)
    class GetPerson extends AbstractPersonCommand {
        @Parameters(description = "Id of the Person")
        String id;

        @Override
        public Integer call() throws Exception {
            Person person = peopleApi.getPerson(id, null).getBody().getEntry();
            formatProvider.print(person);
            return 0;
        }
    }

    @Command(name = "update", description = "Update person details", mixinStandardHelpOptions = true)
    class UpdatePerson extends AbstractPersonCommand {
        @Parameters(description = "Id of the Person")
        String id;
        @Option(names = {"-fn", "--first-name"},
                description = "First Name of the Person")
        String firstName;
        @Option(names = {"-ln", "--last-name"},
                description = "Last Name of the Person")
        String lastName;
        @Option(names = {"-e", "--email"},
                description = "Email of the Person")
        String email;
        @Option(names = {"-p", "--password"},
                description = "Password of the Person")
        String password;

        @Override
        public Integer call() throws Exception {
            Person person = peopleApi.updatePerson(id,
                    new PersonBodyUpdate()
                            .firstName(firstName)
                            .lastName(lastName)
                            .email(email)
                            .password(password)
                            .enabled(true)
                            .emailNotificationsEnabled(true)
                            .oldPassword(password), null).getBody().getEntry();
            formatProvider.print(person);
            return 0;
        }
    }

    @Command(name = "group-list", description = "Get person groups list", mixinStandardHelpOptions = true)
    class ListPersonGroup implements Callable<Integer> {
        @Autowired
        GroupsApi groupsApi;
        @Mixin
        FormatProviderRegistry formatProvider;
        @Parameters(description = "Id of the Person")
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
            GroupPagingList groups =
                    groupsApi.listGroupMembershipsForPerson(id, skipCount, maxItems, null, null, where, null)
                            .getBody().getList();
            formatProvider.print(groups);
            return 0;
        }
    }

    static abstract class AbstractPersonSiteCommand implements Callable<Integer> {

        @Autowired
        SitesApi sitesApi;

        @Mixin
        FormatProviderRegistry formatProvider;

    }

    @Command(name = "list-site", description = "Get person sites list", mixinStandardHelpOptions = true)
    class PersonSiteList extends AbstractPersonSiteCommand {
        @Parameters(description = "Id of the Person")
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
            SiteRolePagingList siteRoles =
                    sitesApi.listSiteMembershipsForPerson(id, skipCount, maxItems, null, null, null, where)
                            .getBody().getList();
            formatProvider.print(siteRoles);
            return 0;
        }
    }

    @Command(name = "get-site", description = "Get person sites details", mixinStandardHelpOptions = true)
    class PersonSiteGet extends AbstractPersonSiteCommand {
        @Parameters(description = "Id of the Person")
        String personId;
        @Parameters(description = "Id of the Site")
        String siteId;

        @Override
        public Integer call() throws Exception {
            SiteRole siteRole = sitesApi.getSiteMembershipForPerson(personId, siteId).getBody().getEntry();
            formatProvider.print(siteRole);
            return 0;
        }
    }

    @Command(name = "delete-site", description = "Get person sites details", mixinStandardHelpOptions = true)
    class PersonSiteDelete extends AbstractPersonSiteCommand {
        @Parameters(description = "Id of the Person")
        String personId;
        @Parameters(description = "Id of the Site")
        String siteId;

        @Override
        public Integer call() throws Exception {
            sitesApi.deleteSiteMembership(siteId, personId);
            formatProvider.print(personId);
            return 0;
        }
    }

    @Component
    static class PersonFormatProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final Person person = (Person) item;
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-20s %-50s %-30s", "ID", "NAME", "EMAIL");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-20s %-50s %-30s", person.getId(), (person.getLastName() == null ? "" : person.getLastName() + ", ") + person.getFirstName(), person.getEmail());
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && Person.class == itemClass;
        }
    }

    @Component
    static class PersonIdFormatProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final Person person = (Person) item;
            System.out.printf(person.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && Person.class == itemClass;
        }
    }

    @Component
    static class PersonPagingListFormatProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final PersonPagingList personList = (PersonPagingList) item;
            List<PersonEntry> entries = personList.getEntries();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-20s %-50s %-30s", "ID", "NAME", "EMAIL");
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-20s %-50s %-30s", entry.getId(), (entry.getLastName() == null ? "" : entry.getLastName() + ", ") + entry.getFirstName(), entry.getEmail());
                System.out.println();
            });
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && PersonPagingList.class == itemClass;
        }
    }

    @Component
    static class PersonPagingListIdsFormatProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final PersonPagingList personList = (PersonPagingList) item;
            List<PersonEntry> entries = personList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && PersonPagingList.class == itemClass;
        }
    }

}

