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
import java.util.concurrent.Callable;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeBodyUpdate;
import org.alfresco.core.model.NodeEntry;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "nodes", mixinStandardHelpOptions = true, subcommands = {NodesCommand.CreateNodeCommand.class, NodesCommand.UpdateNodeCommand.class})
public class NodesCommand {

  private static final String ROOT_PATH = "/";
  private static final String MY_ID = "-my-";
  private static final String ROOT_ID = "-root-";

  @Component
  @Command(name = "create", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
  static class CreateNodeCommand extends NodeCommand {

    @Override
    public Integer call() throws IOException {
      final String nodeId = createNode();
      updateNodeContent(nodeId);
      return 0;
    }
  }

  @Component
  @Command(name = "update", mixinStandardHelpOptions = true, exitCodeOnExecutionException = 44)
  static class UpdateNodeCommand extends NodeCommand {

    @Parameters(index = "0", description = "The id or relative path of the node to be updated.")
    private String node;

    @Override
    public Integer call() throws IOException {
      final String nodeId = getNodeId(node);
      updateNodeContent(nodeId);
      updateNodeMetadata(nodeId);
      return 0;
    }
  }

  static abstract class NodeCommand implements Callable<Integer> {

    @Option(names = {"-t", "--type"}, description = "Content type (example cm:content)")
    String contentType = null;

    @Option(names = {"-n", "--name"}, description = "Name of the node")
    String name = null;

    @Option(names = {"-p", "--parent"}, description = "Path of the parent folder of the node")
    String parent = MY_ID;

    @Option(names = {"-s", "--source"}, description = "File to be uploaded to ACS")
    File source = null;

    @Option(names = {"-v", "--major-version"}, description = "If **true**, create a major version. Setting this parameter also enables versioning of this node, if it is not already versioned.")
    Boolean majorVersion = null;

    @Option(names = {"-c", "--comment"}, description = "Add a version comment which will appear in version history. Setting this parameter also enables versioning of this node, if it is not already versioned.")
    String comment = null;

    @Option(names = {"-a", "--aspects"}, description = "One or more aspect times")
    List<String> aspects = Collections.emptyList();

    @Option(names = {"-m", "--metadata"}, description = "One or more metadata properties. E.g. -m cm:title=\"Proposal\"")
    Map<String, String> metadata = Collections.emptyMap();

    @Autowired
    NodesApi nodesApi;

    String getNodeId(String path) {
      if (path.startsWith(ROOT_PATH)) {
        ResponseEntity<NodeEntry> responseEntity = nodesApi.getNode(ROOT_ID, null, path, Collections.singletonList("id"));
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
          return responseEntity.getBody().getEntry().getId();
        } else {
          throw new RuntimeException(String.format("Unable to retrieve node id from path: %s", path));
        }
      } else {
        return path;
      }
    }

    String createNode() {
      final String effectiveNodeType = contentType == null ? ContentModel.CM_CONTENT : contentType;
      final String effectiveName = name != null ? name : source != null ? source.getName() : "unnamed";
      final NodeBodyCreate nodeBodyCreate = new NodeBodyCreate()
              .nodeType(effectiveNodeType)
              .name(effectiveName)
              .properties(metadata)
              .aspectNames(aspects);

      ResponseEntity<NodeEntry> responseEntity = nodesApi.createNode(getNodeId(parent), nodeBodyCreate, true, null, null);
      if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
        final String nodeId = responseEntity.getBody().getEntry().getId();
        System.out.println(String.format("Created %s node with name: %s and id: %s", effectiveNodeType, effectiveName, nodeId));
        return nodeId;
      } else {
        throw new RuntimeException("Unable to create node. Service returned status: " + responseEntity.getStatusCode());
      }
    }

    void updateNodeContent(String nodeId) throws IOException {
      if(source != null) {
        nodesApi.updateNodeContent(nodeId, FileUtils.readFileToByteArray(source), majorVersion, comment, name, null, null);
        System.out.println(String.format("Node %s was updated with the content of %s", nodeId, source.getName()));
      }
    }

    void updateNodeMetadata(String nodeId) {
      if(!metadata.isEmpty() || name != null || !aspects.isEmpty()) {
        final NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate()
                .nodeType(contentType)
                .properties(metadata)
                .aspectNames(aspects)
                .name(name);

        nodesApi.updateNode(nodeId, nodeBodyUpdate, null, null);
      }
    }
  }
}