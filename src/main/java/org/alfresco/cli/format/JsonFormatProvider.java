/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

@Component
public class JsonFormatProvider implements FormatProvider {

    public static final String FORMAT = "json";

    @Override
    public void print(Object item) {
        try {
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            System.out.println(mapper.writeValueAsString(item));
        } catch(JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isApplicable(Class<?> itemClass, String format) {
        return FORMAT.equals(format);
    }
}