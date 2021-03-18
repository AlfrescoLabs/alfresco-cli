/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.watch;

import java.util.Collections;
import java.util.concurrent.Callable;
import org.alfresco.cli.filters.ParentNodeFilter;
import org.alfresco.cli.filters.StreamFilter;
import org.alfresco.cli.events.EventPrinter;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.NodeEntry;
import org.alfresco.event.sdk.handling.filter.EventFilter;
import org.alfresco.event.sdk.handling.filter.EventTypeFilter;
import org.alfresco.event.sdk.model.v1.model.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "watch", description = "Alfresco Content Services commands",
subcommands = {WatchCommand.FolderCommand.class})
public class WatchCommand implements Callable<Integer> {

    private static final String ROOT_PATH = "/";
    private static final String ROOT_ID = "-root-";

    @Override
    public Integer call() {
        System.out.printf("Use -h for available subcommands.");
        return 1;
    }

    @Component
    @Command(name = "folder", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
    static class FolderCommand extends AbstractWatchCommand {

        @Autowired
        StreamFilter streamFilter;

        @Autowired
        EventPrinter eventPrinter;

        @Parameters(index = "0", description = "The id or relative path of the node to be watched.")
        String folder = ROOT_PATH;

        @Option(names = {"-t", "--event-type"}, description = "Type of event. E.g: NODE_CREATED, NODE_UPDATED, NODE_DELETED")
        String eventType = null;

        @Override
        public Integer call() {
            final String nodeId = getNodeId(folder);
            EventFilter filter = ParentNodeFilter.of(nodeId);

            if(eventType != null) {
                filter = filter.and(EventTypeFilter.of(EventType.valueOf(eventType.toUpperCase())));
            }

            streamFilter.setFilter(filter);
            return 0;
        }
    }

    static abstract class AbstractWatchCommand implements Callable<Integer> {

        @Autowired
        NodesApi nodesApi;

        String getNodeId(String path) {
            if (path.startsWith(ROOT_PATH)) {
                ResponseEntity<NodeEntry> responseEntity = nodesApi.getNode(ROOT_ID, null, path, Collections.singletonList("id"));
                return responseEntity.getBody().getEntry().getId();
            } else {
                return path;
            }
        }
    }

}