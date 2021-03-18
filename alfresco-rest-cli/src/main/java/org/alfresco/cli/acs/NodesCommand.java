/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.acs;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.alfresco.cli.format.FormatProvider;
import org.alfresco.cli.format.FormatProviderRegistry;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeBodyUpdate;
import org.alfresco.core.model.NodeChildAssociationEntry;
import org.alfresco.core.model.NodeChildAssociationPaging;
import org.alfresco.core.model.NodeChildAssociationPagingList;
import org.alfresco.core.model.NodeEntry;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Mixin;

@Component
@Command(name = "node", description = "Node commands",
        subcommands = {NodesCommand.ListNodeCommand.class, NodesCommand.UpdateNodeCommand.class,
                NodesCommand.CreateNodeCommand.class, NodesCommand.GetNodeCommand.class,
                NodesCommand.DeleteNodeCommand.class})
public class NodesCommand {

    private static final String ROOT_PATH = "/";
    private static final String MY_ID = "-my-";
    private static final String ROOT_ID = "-root-";

    @Component
    @Command(name = "delete", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
    static class DeleteNodeCommand extends AbstractNodesCommand {

        @Parameters(index = "0", description = "The id or relative path of the node to be deleted")
        private String node;

        @Option(names = {"-pe", "--permanent"},
                description = "Deletes the node permanently instead of moving it to the trashcan. Only the owner of the node can use this option")
        Boolean permanent = null;

        @Override
        public Integer call() {
            final String nodeId = getNodeId(node);
            nodesApi.deleteNode(nodeId, permanent);
            formatProvider.print(nodeId);
            return 0;
        }
    }

    @Component
    @Command(name = "get", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
    static class GetNodeCommand extends AbstractNodesCommand {

        @Parameters(index = "0",
                description = "The id or relative path of the node to be retrieved")
        private String node;

        @Override
        public Integer call() {
            final String nodeId = getNodeId(node);
            final ResponseEntity<NodeEntry> responseEntity =
                    nodesApi.getNode(nodeId, null, null, null);
            final Node node = responseEntity.getBody().getEntry();
            formatProvider.print(node);
            return 0;
        }
    }

    @Component
    @Command(name = "list", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
    static class ListNodeCommand extends AbstractNodesCommand {

        @Option(names = {"-w", "--where"},
                description = "\"Optionally filter the list. Here are some examples:  *   -w \"(isFolder=true)\"  *   -w \"(isFile=true)\"  *   -w \"(nodeType='my:specialNodeType')\"  *   -w \"(nodeType='my:specialNodeType INCLUDESUBTYPES')\"  *   -w \"(isPrimary=true)\"  *   -w \"(assocType='my:specialAssocType')\"  *   -w \"(isPrimary=false and assocType='my:specialAssocType')\" ")
        String where = null;

        @Option(names = {"-sc", "--skip-count"},
                description = "The number of entities that exist in the collection before those included in this list. (Default value is 0)")
        Integer skipCount = null;

        @Option(names = {"-max", "--max-items"},
                description = "The maximum number of items to return in the list. (Default valueis 100)")
        Integer maxItems = null;

        @Option(names = {"-ob", "--order-by"},
                description = "A string to control the order of the entities returned in a list. You can use the **order-by** parameter to sort the list by one or more fields.  Each field has a default sort order, which is normally ascending order. To sort the entities in a specific order, you can use the **ASC** and **DESC** keywords for any field.")
        List<String> orderBy = null;

        @Override
        public Integer call() {
            final String parentNodeId = getNodeId(parent);
            final ResponseEntity<NodeChildAssociationPaging> responseEntity =
                    nodesApi.listNodeChildren(parentNodeId, skipCount, maxItems, orderBy, where,
                            null, null, null, null);
            final NodeChildAssociationPagingList list = responseEntity.getBody().getList();
            formatProvider.print(list);
            return 0;
        }
    }

    @Component
    @Command(name = "create", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
    static class CreateNodeCommand extends UpdatableNodesCommand {

        @Override
        public Integer call() throws IOException {
            final Node node = createNode();
            updateNodeContent(node.getId(), null, null);
            formatProvider.print(node);
            return 0;
        }

        Node createNode() {
            final String effectiveNodeType =
                    contentType == null ? ContentModel.CM_CONTENT : contentType;
            final String effectiveName =
                    name != null ? name : source != null ? source.getName() : "unnamed";
            final NodeBodyCreate nodeBodyCreate = new NodeBodyCreate().nodeType(effectiveNodeType)
                    .name(effectiveName).properties(metadata).aspectNames(aspects);

            ResponseEntity<NodeEntry> responseEntity =
                    nodesApi.createNode(getNodeId(parent), nodeBodyCreate, true, null, null);
            return responseEntity.getBody().getEntry();
        }
    }

    @Component
    @Command(name = "update", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
    static class UpdateNodeCommand extends UpdatableNodesCommand {

        @Parameters(index = "0", description = "The id or relative path of the node to be updated")
        private String node;

        @Option(names = {"-v", "--major-version"},
                description = "If **true**, create a major version. Setting this parameter also enables versioning of this node, if it is not already versioned.")
        Boolean majorVersion = null;

        @Option(names = {"-c", "--comment"},
                description = "Add a version comment which will appear in version history. Setting this parameter also enables versioning of this node, if it is not already versioned.")
        String comment = null;

        @Override
        public Integer call() throws IOException {
            final String nodeId = getNodeId(node);
            Optional<Node> updatedContent = updateNodeContent(nodeId, majorVersion, comment);
            Optional<Node> updatedMetadata = updateNodeMetadata(nodeId);
            formatProvider.print(updatedMetadata.orElse(
                    updatedContent.orElseThrow(() -> new RuntimeException("Nothing to update"))));

            return 0;
        }

        Optional<Node> updateNodeMetadata(String nodeId) {
            if (!metadata.isEmpty() || name != null || !aspects.isEmpty()) {
                final NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate().nodeType(contentType)
                        .properties(metadata).aspectNames(aspects).name(name);

                ResponseEntity<NodeEntry> responseEntity =
                        nodesApi.updateNode(nodeId, nodeBodyUpdate, null, null);
                return Optional.of(responseEntity.getBody().getEntry());
            } else {
                return Optional.empty();
            }
        }
    }

    static abstract class AbstractNodesCommand implements Callable<Integer> {

        @Option(names = {"-p", "--parent"}, description = "Path of the parent folder of the node")
        String parent = MY_ID;

        @Mixin
        FormatProviderRegistry formatProvider;

        @Autowired
        NodesApi nodesApi;

        String getNodeId(String path) {
            if (path.startsWith(ROOT_PATH)) {
                ResponseEntity<NodeEntry> responseEntity =
                        nodesApi.getNode(ROOT_ID, null, path, Collections.singletonList("id"));
                return responseEntity.getBody().getEntry().getId();
            } else {
                return path;
            }
        }
    }

    static abstract class UpdatableNodesCommand extends AbstractNodesCommand {

        @Option(names = {"-t", "--type"}, description = "Content type (example cm:content)")
        String contentType = null;

        @Option(names = {"-n", "--name"}, description = "Name of the node")
        String name = null;

        @Option(names = {"-s", "--source"}, description = "File to be uploaded to ACS")
        File source = null;

        @Option(names = {"-a", "--aspects"}, description = "One or more aspect times")
        List<String> aspects = Collections.emptyList();

        @Option(names = {"-m", "--metadata"},
                description = "One or more metadata properties. E.g. -m cm:title=\"Proposal\"")
        Map<String, String> metadata = Collections.emptyMap();

        Optional<Node> updateNodeContent(String nodeId, Boolean majorVersion, String comment)
                throws IOException {
            if (source != null) {
                ResponseEntity<NodeEntry> responseEntity =
                        nodesApi.updateNodeContent(nodeId, FileUtils.readFileToByteArray(source),
                                majorVersion, comment, null, null, null);
                return Optional.of(responseEntity.getBody().getEntry());
            } else {
                return Optional.empty();
            }
        }
    }

    @Component
    static class NodeFormatProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final Node node = (Node) item;
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-30s %-25s %-10s", "ID", "NAME", "MODIFIED AT", "USER");
            System.out.println();
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-30s %-25s %-10s", node.getId(), node.getName(),
                    node.getModifiedAt(), node.getModifiedByUser().getDisplayName());
            System.out.println();
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && Node.class == itemClass;
        }
    }

    @Component
    static class NodeIdFormatProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final Node node = (Node) item;
            System.out.printf(node.getId());
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && Node.class == itemClass;
        }
    }

    @Component
    static class NodeChildAssociationPagingListFormatProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final NodeChildAssociationPagingList ncaList = (NodeChildAssociationPagingList) item;
            List<NodeChildAssociationEntry> entries = ncaList.getEntries();
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-40s %-30s %-25s %-10s", "ID", "NAME", "MODIFIED AT", "USER");
            System.out.println();
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            entries.stream().map(entry -> entry.getEntry()).forEach(entry -> {
                System.out.printf("%-40s %-30s %-25s %-10s", entry.getId(), entry.getName(),
                        entry.getModifiedAt(), entry.getModifiedByUser().getDisplayName());
                System.out.println();
            });
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return DEFAULT.equals(format) && NodeChildAssociationPagingList.class == itemClass;
        }
    }

    @Component
    static class NodeChildAssociationPagingListIdsFormatProvider implements FormatProvider {

        @Override
        public void print(Object item) {
            final NodeChildAssociationPagingList ncaList = (NodeChildAssociationPagingList) item;
            List<NodeChildAssociationEntry> entries = ncaList.getEntries();
            entries.stream()
                    .map(entry -> entry.getEntry().getId())
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .ifPresent(System.out::printf);
        }

        @Override
        public boolean isApplicable(Class<?> itemClass, String format) {
            return ID.equals(format) && NodeChildAssociationPagingList.class == itemClass;
        }
    }
}
