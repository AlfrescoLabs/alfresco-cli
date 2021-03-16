/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli;

import org.alfresco.cli.acs.AcsCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.ScopeType;

@Component
@Command(name = "alfresco", scope = ScopeType.INHERIT, mixinStandardHelpOptions = true, exitCodeOnExecutionException = 1,
        version = "1.0", subcommands = AcsCommand.class)
public class AlfrescoCommand {
}
