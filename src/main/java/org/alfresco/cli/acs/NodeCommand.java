/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import java.util.List;
import java.util.concurrent.Callable;
import org.alfresco.cli.acs.NodeCommand.CreateCommand;
import org.alfresco.cli.acs.NodeCommand.DeleteCommand;
import org.alfresco.cli.acs.NodeCommand.ListCommand;
import org.alfresco.core.handler.NodesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "node", mixinStandardHelpOptions = true, subcommands = {ListCommand.class, CreateCommand.class, DeleteCommand.class})
public class NodeCommand {

  @Autowired
  private NodesApi nodesApi;

  @Component
  @Command(name = "list", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
  class ListCommand implements Callable<Integer> {

    @Option(names = {"-t", "--type"}, description = "Content type (example cm:content)")
    private String contentType;

    @Override
    public Integer call() {
      String result = null /* service.service() */;
      System.out.printf("create-node was called with -t=%s. Service says: '%s'%n", contentType,
          result);
      return 43;
    }
  }

  @Component
  @Command(name = "create", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
  class CreateCommand implements Callable<Integer> {

    @Option(names = {"-t", "--type"}, description = "Content type (example cm:content)")
    private String contentType;

    @Override
    public Integer call() {
      String result = null /* service.service() */;
      System.out.printf("create-node was called with -t=%s. Service says: '%s'%n", contentType, result);
      return 43;
    }
  }

  @Component
  @Command(name = "delete", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1)
  class DeleteCommand implements Callable<Integer> {

    @Parameters(description = "node identifiers")
    private List<String> nodeIds;

    @Override
    public Integer call() {
      String result = null /* service.service() */;
      System.out.printf("delete-node was called with nodes %s. Service says: '%s'%n", nodeIds, result);
      return 2;
    }

  }
}