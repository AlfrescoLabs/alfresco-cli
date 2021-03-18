/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.filters;

import org.alfresco.event.sdk.handling.filter.EventFilter;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.alfresco.event.sdk.model.v1.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class StreamFilter implements EventFilter {

    private EventFilter filter = (event -> false);

    @Override
    public boolean test(RepoEvent<DataAttributes<Resource>> repoEvent) {
        return filter.test(repoEvent);
    }

    public void setFilter(EventFilter filter) {
        this.filter = filter;
    }
}