/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "acs", mixinStandardHelpOptions = true, subcommands = {NodeCommand.class, PersonCommand.class})
public class AcsCommand {

}