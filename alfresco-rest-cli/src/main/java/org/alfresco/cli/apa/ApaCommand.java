/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.apa;

import java.util.Map;
import com.alfresco.activiti.runtime.handler.ProcessInstanceControllerImplApi;
import com.alfresco.activiti.runtime.model.EntryResponseContentCloudProcessInstance;
import com.alfresco.activiti.runtime.model.StartProcessPayload;
import com.alfresco.activiti.runtime.model.StartProcessPayload.PayloadTypeEnum;
import org.alfresco.cli.format.FormatProviderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "apa", description = "Alfresco Process Automation commands")
public class ApaCommand {

    @Autowired
    ProcessInstanceControllerImplApi processInstanceControllerImplApi;

    @CommandLine.Mixin
    FormatProviderRegistry formatProvider;

    @Command(description = "Start process")
    public Integer startProcess(@Parameters(description = "Process definition key") String processDefinitionKey,
            @Parameters(description = "Variables map") Map<String, String> variables) {
        StartProcessPayload startProcessPayload =
                new StartProcessPayload().payloadType(PayloadTypeEnum.STARTPROCESSPAYLOAD)
                        .processDefinitionKey(processDefinitionKey).variables(variables);
        ResponseEntity<EntryResponseContentCloudProcessInstance> response =
                        processInstanceControllerImplApi.startProcessUsingPOST1(startProcessPayload);
        EntryResponseContentCloudProcessInstance result = response.getBody();
        formatProvider.print(result);
        return 0;
    }

}
