package org.alfresco.cli.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "config", description = "Configuration commands")
public class ConfigCommand {

    static final File USER_CONFIGURATION_FILE = Paths
            .get(System.getProperty("user.home"), ".alfresco", "application.properties").toFile();

    @Command(description = "Set configuration for ACS.")
    public Integer acs(@Parameters(description = "Alfresco Content Services URL (example >> \"http://localhost:8080\"") String url,
                       @Parameters(description = "Alfresco Content Services Username") String username,
                       @Parameters(description = "Alfresco Content Services Password") String password) throws IOException {
        Properties appProps = getAppProps();
        appProps.setProperty("content.service.url", url);
        appProps.put("content.service.security.basicAuth.username", username);
        appProps.put("content.service.security.basicAuth.password", password);
        appProps.store(new FileWriter(USER_CONFIGURATION_FILE), "Alfresco CLI Local Configuration");
        return 0;
    }

    @Command(description = "Set configuration for APA.")
    public Integer apa(@Parameters(description = "Alfresco Process Application URL (example >> \"http://localhost:8080\"") String url)
            throws IOException {
        Properties appProps = getAppProps();
        appProps.setProperty("activiti.service.runtime.url", url);
        appProps.store(new FileWriter(USER_CONFIGURATION_FILE), "Alfresco CLI Local Configuration");
        return 0;
    }

    private Properties getAppProps() throws IOException {
        Properties appProps = new Properties();
        if (USER_CONFIGURATION_FILE.exists()) {
            appProps.load(new FileInputStream(USER_CONFIGURATION_FILE));
        } else {
            FileUtils.touch(USER_CONFIGURATION_FILE);
        }
        return appProps;
    }

}
