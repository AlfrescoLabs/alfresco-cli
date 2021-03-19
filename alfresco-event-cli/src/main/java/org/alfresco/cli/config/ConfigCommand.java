package org.alfresco.cli.config;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

@Component
@Command(name = "config", description = "Configuration commands")
public class ConfigCommand {

    static final String USER_CONFIGURATION_FILE = System.getProperty("user.home") + "/.alfresco-stream/application.properties";

    @Command(description = "Set configuration for ACS.")
    public Integer acs(@Parameters(description = "Alfresco Content Services URL (example >> \"http://localhost:8080\")") String url,
                       @Parameters(description = "Alfresco Content Services Username") String username,
                       @Parameters(description = "Alfresco Content Services Password") String password) throws IOException {
        Properties appProps = getAppProps();
        appProps.setProperty("content.service.url", url);
        appProps.put("content.service.security.basicAuth.username", username);
        appProps.put("content.service.security.basicAuth.password", password);
        appProps.store(new FileWriter(USER_CONFIGURATION_FILE), "Alfresco CLI Local Configuration");
        return 0;
    }

    @Command(description = "Set configuration for ActiveMQ.")
    public Integer activemq(@Parameters(description = "ActiveMQ URL (example >> \"tcp://localhost:61616\"") String url)
            throws IOException {
        Properties appProps = getAppProps();
        appProps.setProperty("spring.activemq.brokerUrl", url);
        appProps.store(new FileWriter(USER_CONFIGURATION_FILE), "Alfresco CLI Local Configuration");
        return 0;
    }

    private Properties getAppProps() throws IOException {
        Properties appProps = new Properties();
        if (new File(USER_CONFIGURATION_FILE).exists()) {
            appProps.load(new FileInputStream(USER_CONFIGURATION_FILE));
        } else {
            FileUtils.touch(new File(USER_CONFIGURATION_FILE));
        }
        return appProps;
    }

}
