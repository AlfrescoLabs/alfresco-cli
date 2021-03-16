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
import java.util.concurrent.Callable;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeEntry;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Component
@Command(name = "nodes", mixinStandardHelpOptions = true, subcommands = {NodesCommand.CreateNodeCommand.class, NodesCommand.UpdateNodeCommand.class},
    exitCodeOnExecutionException = 34)
public class NodesCommand implements Callable<Integer> {

  private static final String ROOT_PATH = "/";
  private static final String MY_ID = "-my-";
  private static final String ROOT_ID = "-root-";

  @Override
  public Integer call() {
    System.out.printf("Use -h for available subcommands.");
    return 1;
  }

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

    @Parameters  (index = "0", description = "The id or relative path of the node to be updated.")
    private String node;

    @Override
    public Integer call() throws IOException {
      final String nodeId = getNodeId(node);
      updateNodeContent(nodeId);
      return 0;
    }
  }



  static abstract class NodeCommand implements Callable<Integer> {

    @Option(names = {"-t", "--type"}, description = "Content type (example cm:content)")
    String contentType = "cm:content";

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
      final NodeBodyCreate nodeBodyCreate = new NodeBodyCreate()
              .nodeType(contentType)
              .name(source != null ? source.getName() : name);

      ResponseEntity<NodeEntry> responseEntity = nodesApi.createNode(getNodeId(parent), nodeBodyCreate, true, null, null);
      if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
        final String nodeId = responseEntity.getBody().getEntry().getId();
        System.out.println(String.format("created node %s of type %s with id: %s", name, contentType, nodeId));
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


  }
}