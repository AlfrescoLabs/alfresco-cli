/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.events;

import org.alfresco.cli.filters.StreamFilter;
import org.alfresco.event.sdk.integration.EventChannels;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.NodeResource;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
public class EventHandler {

    @Autowired
    private StreamFilter filter;

    @Autowired
    private EventPrinter printer;

    @Bean
    public IntegrationFlow handleEvent() {
        printer.printHeader();
        return IntegrationFlows.from(EventChannels.MAIN)
                .filter(filter::test)
                .handle(t -> printer.printEvent((RepoEvent<DataAttributes<NodeResource>>)t.getPayload()))
                .get();
    }
}
