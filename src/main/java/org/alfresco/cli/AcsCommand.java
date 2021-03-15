/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli;

import java.util.List;
import java.util.concurrent.Callable;
import org.alfresco.cli.AcsCommand.CreateNodeCommand;
import org.alfresco.cli.AcsCommand.DeleteNodeCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "acs", mixinStandardHelpOptions = true, subcommands = {CreateNodeCommand.class, DeleteNodeCommand.class},
    exitCodeOnExecutionException = 34)
public class AcsCommand implements Callable<Integer> {

  @Override
  public Integer call() {
    System.out.printf("Use -h for available subcommands.");
    return 1;
  }

  @Component
  @Command(name = "create-node", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
  static class CreateNodeCommand implements Callable<Integer> {

    @Option(names = {"-t", "--type"}, description = "Content type (example cm:content)")
    private String contentType;

    // @Autowired
    // private SomeService service;

    @Override
    public Integer call() {
      String result = null /* service.service() */;
      System.out.printf("create-node was called with -t=%s. Service says: '%s'%n", contentType, result);
      return 43;
    }
  }

  @Component
  @Command(name = "delete-node", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
  static class DeleteNodeCommand implements Callable<Integer> {

    // @Autowired
    // private SomeService service;

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