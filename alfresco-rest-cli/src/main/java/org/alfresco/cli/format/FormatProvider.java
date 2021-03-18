/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.cli.format;

@FunctionalInterface
public interface FormatProvider {

    public static final String DEFAULT = "default";
    public static final String ID = "id";
    public static final String TO_STRING = "toString";

    void print(Object item);

    default boolean isApplicable(Class<?> itemClass, String format) {
        return false;
    }
}