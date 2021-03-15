/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.*;
import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "alfresco", mixinStandardHelpOptions = true, subcommands = AcsCommand.class)
public class AlfrescoCommand implements Callable<Integer> {

  @Option(names = "-x", description = "optional option")
  private String x;

  @Parameters(description = "positional params")
  private List<String> positionals;

  @Override
  public Integer call() {
    System.out.printf("mycommand was called with -x=%s and positionals: %s%n", x, positionals);
    return 23;
  }

}