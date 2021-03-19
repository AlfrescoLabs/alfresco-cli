/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication

//Required for ApaCommand
@EnableFeignClients({"org.alfresco.core.handler", "org.alfresco.discovery.handler",
     "org.alfresco.governance.core.handler", "org.alfresco.governance.classification.handler",
     "org.alfresco.search.handler", "org.alfresco.search.sql.handler",
     "org.alfresco.cli.apa"})
public class Application implements CommandLineRunner, ExitCodeGenerator {

    private IFactory factory;
    private int exitCode;
    private AlfrescoCommand mailCommand;

    // constructor injection
    Application(IFactory factory, AlfrescoCommand mailCommand) {
        this.factory = factory;
        this.mailCommand = mailCommand;
    }

    @Override
    public void run(String... args) {
        // let picocli parse command line args and run the business logic
        exitCode = new CommandLine(mailCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    public static void main(String[] args) {
        // let Spring instantiate and inject dependencies
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }
}
