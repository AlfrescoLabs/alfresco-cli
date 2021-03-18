/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli;

import org.alfresco.cli.acs.AcsCommand;
import org.alfresco.cli.ags.AgsCommand;
import org.alfresco.cli.apa.ApaCommand;
import org.alfresco.cli.config.ConfigCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.ScopeType;

@Component
@Command(name = "alfresco", scope = ScopeType.INHERIT, mixinStandardHelpOptions = true,
        exitCodeOnExecutionException = 1, showDefaultValues = true, usageHelpAutoWidth = true,
        version = "1.0", subcommands = {AcsCommand.class, AgsCommand.class, ApaCommand.class, ConfigCommand.class},
        synopsisHeading = "    _   _  __                         _ _ \n"
                + "   /_\\ | |/ _|_ _ ___ _____ ___    __| (_)\n"
                + "  / _ \\| |  _| '_/ -_(_-/ _/ _ \\  / _| | |\n"
                + " /_/ \\_|_|_| |_| \\___/__\\__\\___/  \\__|_|_|     powered by picoTeam\n\n"
                + "Usage: ")
public class AlfrescoCommand {

    @Command(hidden = true)
    public Integer win() throws InterruptedException {
        System.out.println();
        String s = "............................ Alfresco CLI for the win! ";
        while (true) {
            System.out.printf("\r%s", s.substring(0, 30));
            Thread.sleep(100);
            s = s.substring(1) + s.substring(0, 1);
        }
    }
}
