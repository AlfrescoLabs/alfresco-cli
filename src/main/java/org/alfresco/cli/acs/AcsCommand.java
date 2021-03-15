/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "acs", mixinStandardHelpOptions = true, subcommands = {NodeCommand.class, PeopleCommand.class},
    exitCodeOnExecutionException = 34)
public class AcsCommand implements Callable<Integer> {

  @Override
  public Integer call() {
    System.out.printf("Use -h for available subcommands.%n");
    return 1;
  }

}