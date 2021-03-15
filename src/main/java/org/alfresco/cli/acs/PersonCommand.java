/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.alfresco.cli.acs.PersonCommand.ListCommand;
import org.alfresco.core.handler.PeopleApi;
import org.alfresco.core.model.PersonPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "person", mixinStandardHelpOptions = true, subcommands = {ListCommand.class})
public class PersonCommand {

  @Autowired
  private PeopleApi peopleApi;

  @Component
  @Command(name = "list", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
  class ListCommand implements Callable<Integer> {

    @Override
    public Integer call() {
      ResponseEntity<PersonPaging> response =
          peopleApi.listPeople(0, Integer.MAX_VALUE, null, null, null);
      String result = response.getBody().getList().getEntries().stream()
          .map(pe -> pe.getEntry().getDisplayName()).collect(Collectors.joining(", "));
      System.out.printf("People list: %s%n", result);
      return 0;
    }
  }

}
