/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.ags;

import java.util.List;
import java.util.stream.Collectors;
import org.alfresco.cli.ags.AgsCommand.SecurityMarkCommand;
import org.alfresco.governance.classification.handler.SecuredNodesApi;
import org.alfresco.governance.classification.model.SecuringMarksEntry;
import org.alfresco.governance.classification.model.SecuringMarksPaging;
import org.alfresco.governance.classification.model.SecuringMarksUpdateBody;
import org.alfresco.governance.classification.model.SecuringMarksUpdateEntry;
import org.alfresco.governance.classification.model.SecuringMarksUpdateEntry.OpEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "ags", description = "Alfresco Governance Services commands",
        subcommands = SecurityMarkCommand.class)
public class AgsCommand {

    @Component
    @Command(name = "securityMark", description = "Security Marks commands")
    static class SecurityMarkCommand {

        @Autowired
        private SecuredNodesApi securedNodesApi;

        @Command(description = "List all security marks assigned to a node.")
        public Integer list(@Parameters(description = "Node identifier") String nodeId,
                @Option(names = {"-sc", "--skip-count"}, defaultValue = "0",
                        description = "Number of items to be skipped") Integer skipCount,
                @Option(names = {"-mi", "--max-items"}, defaultValue = "100",
                        description = "Number of items to be returned") Integer maxItems) {
            ResponseEntity<SecuringMarksPaging> response =
                    securedNodesApi.getSecuringMarks(nodeId, skipCount, maxItems);
            List<SecuringMarksEntry> result = response.getBody().getList().getEntries();
            System.out.println(result);
            return 0;
        }

        static class SecuringMarkUpdate {
            @Parameters(description = "Security mark identifier")
            String id;
            @Parameters(description = "Security group identifier")
            String groupId;
            @Parameters(description = "Operation. Valid values: ${COMPLETION-CANDIDATES}")
            OpEnum op;
        }

        @Command(description = "Manage the existing security marks for a node.")
        public Integer update(@Parameters(description = "Node identifier") String nodeId,
                @ArgGroup(exclusive = false,
                        multiplicity = "1..*") List<SecuringMarkUpdate> securingMarkUpdates) {
            List<SecuringMarksUpdateEntry> securingMarksUpdateEntries =
                    securingMarkUpdates
                            .stream().map(e -> new SecuringMarksUpdateEntry().id(e.id)
                                    .groupId(e.groupId).op(e.op))
                            .collect(Collectors.toList());
            SecuringMarksUpdateBody body = new SecuringMarksUpdateBody();
            body.addAll(securingMarksUpdateEntries);
            ResponseEntity<SecuringMarksPaging> response =
                    securedNodesApi.updateSecuringMarks(nodeId, body);
            List<SecuringMarksEntry> result = response.getBody().getList().getEntries();
            System.out.println(result);
            return 0;
        }
    }
}
