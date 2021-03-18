/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.format;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;

public class FormatProviderRegistry {

    @CommandLine.Option(names = {"-f", "--format"}, description = "Output format. E.g.: 'default' or 'json'.")
    String format = FormatProvider.DEFAULT;

    @Autowired
    List<FormatProvider> providers = Collections.emptyList();

    public void print(Object item) {
        if (item != null) {
            FormatProvider provider = providers.stream()
                    .filter(prv -> prv.isApplicable(item.getClass(), format))
                    .findFirst()
                    .orElse(elem -> System.out.println(elem));

            provider.print(item);
        }
    }
}