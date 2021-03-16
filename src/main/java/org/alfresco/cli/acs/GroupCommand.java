/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.alfresco.cli.acs.GroupCommand.ListCommand;
import org.alfresco.core.handler.GroupsApi;
import org.alfresco.core.model.GroupPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "group", mixinStandardHelpOptions = true, subcommands = {ListCommand.class})
public class GroupCommand {

  @Autowired
  private GroupsApi groupsApi;

  @Component
  @Command(name = "list", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
  class ListCommand implements Callable<Integer> {

    @Option(names = {"-sc", "--skip-count"}, defaultValue = "0", description = "Number of sites to be skipped")
    Integer skipCount;

    @Option(names = {"-mi", "--max-items"}, defaultValue = "100", description = "Number of sites to be recovered")
    Integer maxItems;

    @Option(names = {"-w", "--where"}, description = "Filter for returned groups")
    String where;

    @Override
    public Integer call() {
      ResponseEntity<GroupPaging> response =
          groupsApi.listGroups(skipCount, maxItems, null, null, where, null);
      String result = response.getBody().getList().getEntries().stream()
          .map(pe -> pe.getEntry().getDisplayName()).collect(Collectors.joining(", "));
      System.out.printf("People list: %s%n", result);
      return 0;
    }
  }

}
