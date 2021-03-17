/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import org.alfresco.cli.acs.PersonCommand.PersonGroupCommand;
import org.alfresco.cli.acs.PersonCommand.PersonSiteCommand;
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

@Component
@Command(name = "person", subcommands = {PersonGroupCommand.class, PersonSiteCommand.class}, description = "Person commands")
public class PersonCommand {

    @Autowired
    PeopleApi peopleApi;

    @Mixin
    FormatProviderRegistry formatProvider;

    @Command(description = "Get people list")
    public Integer list(
            @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                    description = "Number of items to be skipped") Integer skipCount,
            @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                    description = "Number of items to be returned") Integer maxItems) {
        PersonPagingList sites = peopleApi.listPeople(skipCount, maxItems, null, null, null)
                .getBody().getList();
        formatProvider.print(sites);
        return 0;
    }

    @Command(description = "Create person")
    public Integer create(@Parameters(description = "Id of the Person") String id,
                          @Option(names = {"-fn", "--first-name"}, required = true,
                                  description = "First Name of the Person") String firstName,
                          @Option(names = {"-ln", "--last-name"}, required = true,
                                  description = "Last Name of the Person") String lastName,
                          @Option(names = {"-e", "--email"}, required = true,
                                  description = "Email of the Person") String email,
                          @Option(names = {"-p", "--password"}, required = true,
                                  description = "Password of the Person") String password) {
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

    @Command(description = "Get person details")
    public Integer get(@Parameters(description = "Id of the Person") String id) {
        Person person = peopleApi.getPerson(id, null).getBody().getEntry();
        formatProvider.print(person);
        return 0;
    }

    @Command(description = "Update person details")
    public Integer update(@Parameters(description = "Id of the Person") String id,
                          @Option(names = {"-fn", "--first-name"},
                                  description = "First Name of the Person") String firstName,
                          @Option(names = {"-ln", "--last-name"},
                                  description = "Last Name of the Person") String lastName,
                          @Option(names = {"-e", "--email"},
                                  description = "Email of the Person") String email,
                          @Option(names = {"-p", "--password"},
                                  description = "Password of the Person") String password) {
        Person person = peopleApi.updatePerson(id,
                new PersonBodyUpdate()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .password(password), null).getBody().getEntry();
        formatProvider.print(person);
        return 0;
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

    @Command(name = "group", description = "Person groups commands")
    static class PersonGroupCommand {

        @Autowired
        GroupsApi groupsApi;

        @Mixin
        FormatProviderRegistry formatProvider;

        @Command(description = "Get person groups list")
        public Integer list(@Parameters(description = "Id of the Person") String id,
                            @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                                    description = "Number of items to be skipped") Integer skipCount,
                            @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                                    description = "Number of items to be returned") Integer maxItems,
                            @Option(names = {"-w", "--where"},
                                    description = "Filter for returned sites") String where) {
            GroupPagingList groups =
                    groupsApi.listGroupMembershipsForPerson(id, skipCount, maxItems, null, null, where, null)
                            .getBody().getList();
            formatProvider.print(groups);
            return 0;
        }

    }

    @Command(name = "site", description = "Person sites commands")
    static class PersonSiteCommand {

        @Autowired
        SitesApi sitesApi;

        @Mixin
        FormatProviderRegistry formatProvider;

        @Command(description = "Get person sites list")
        public Integer list(@Parameters(description = "Id of the Person") String id,
                            @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                                    description = "Number of items to be skipped") Integer skipCount,
                            @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                                    description = "Number of items to be returned") Integer maxItems,
                            @Option(names = {"-w", "--where"},
                                    description = "Filter for returned sites") String where) {
            SiteRolePagingList siteRoles =
                    sitesApi.listSiteMembershipsForPerson(id, skipCount, maxItems, null, null, null, where)
                            .getBody().getList();
            formatProvider.print(siteRoles);
            return 0;
        }

        @Command(description = "Get person sites details")
        public Integer get(@Parameters(description = "Id of the Person") String personId,
                           @Parameters(description = "Id of the Site") String siteId) {
            SiteRole siteRole = sitesApi.getSiteMembershipForPerson(personId, siteId).getBody().getEntry();
            formatProvider.print(siteRole);
            return 0;
        }

        @Command(description = "Delete person site membership")
        public Integer delete(@Parameters(description = "Id of the Person") String personId,
                           @Parameters(description = "Id of the Site") String siteId) {
            sitesApi.deleteSiteMembership(siteId, personId);
            formatProvider.print(personId);
            return 0;
        }

    }

}

