/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.events;

import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.NodeResource;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.springframework.stereotype.Component;

@Component
public class EventPrinter {

    public void printHeader() {
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-40s %-30s %-32s %-10s", "EVENT TYPE", "ID", "NAME", "MODIFIED AT", "USER");
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
    }


    public void printEvent(RepoEvent<DataAttributes<NodeResource>> event) {
        final NodeResource resource = event.getData().getResource();
        System.out.printf("%-20s %-40s %-30s %-32s %-10s", convertEventType(event.getType()), resource.getId(), resource.getName(), resource.getModifiedAt(), resource.getModifiedByUser().getDisplayName());
        System.out.println();
    }

    private String convertEventType(String eventType) {
        return eventType
                .replace("org.alfresco.event.", "")
                .replace(".", "_")
                .toUpperCase();
    }
}
