/*
 * Copyright 2021-2021 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.cli.filters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.alfresco.event.sdk.handling.filter.AbstractEventFilter;
import org.alfresco.event.sdk.handling.filter.EventFilter;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.NodeResource;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.alfresco.event.sdk.model.v1.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link EventFilter} that checks if an event makes reference to a descendant of a specific node
 */
public class ParentNodeFilter extends AbstractEventFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(org.alfresco.event.sdk.handling.filter.MimeTypeFilter.class);

    private final String parentNodeId;

    private ParentNodeFilter(final String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    /**
     * Obtain a {@link ParentNodeFilter} for a specific parent node id.
     */
    public static ParentNodeFilter of(final String parentNodeId) {
        Objects.requireNonNull(parentNodeId);
        return new ParentNodeFilter(parentNodeId);
    }

    @Override
    public boolean test(final RepoEvent<DataAttributes<Resource>> event) {
        LOGGER.debug("Checking filter for MimeTypes {} and event {}", parentNodeId, event);
        final List<String> primaryHierarchy = ((NodeResource)event.getData().getResource()).getPrimaryHierarchy();
        return primaryHierarchy != null && primaryHierarchy.contains(parentNodeId);
    }
}
