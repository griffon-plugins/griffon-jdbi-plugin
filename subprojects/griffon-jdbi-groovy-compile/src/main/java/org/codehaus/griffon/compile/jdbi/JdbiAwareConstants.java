/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2021 The author and/or original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.compile.jdbi;

import org.codehaus.griffon.compile.core.BaseConstants;
import org.codehaus.griffon.compile.core.MethodDescriptor;

import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedMethod;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedType;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotations;
import static org.codehaus.griffon.compile.core.MethodDescriptor.args;
import static org.codehaus.griffon.compile.core.MethodDescriptor.method;
import static org.codehaus.griffon.compile.core.MethodDescriptor.throwing;
import static org.codehaus.griffon.compile.core.MethodDescriptor.type;
import static org.codehaus.griffon.compile.core.MethodDescriptor.typeParams;
import static org.codehaus.griffon.compile.core.MethodDescriptor.types;

/**
 * @author Andres Almiray
 */
public interface JdbiAwareConstants extends BaseConstants {
    String DBI_TYPE = "org.skife.jdbi.v2.DBI";
    String JDBI_HANDLER_TYPE = "griffon.plugins.jdbi.JdbiHandler";
    String JDBI_CALLBACK_TYPE = "griffon.plugins.jdbi.JdbiCallback";
    String RUNTIME_JDBI_EXCEPTION_TYPE = "griffon.plugins.jdbi.exceptions.RuntimeJdbiException";
    String JDBI_HANDLER_PROPERTY = "jdbiHandler";
    String JDBI_HANDLER_FIELD_NAME = "this$" + JDBI_HANDLER_PROPERTY;

    String METHOD_WITH_JDBI = "withJdbi";
    String METHOD_CLOSE_JDBI = "closeJdbi";
    String DATASOURCE_NAME = "datasourceName";
    String CALLBACK = "callback";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        method(
            type(VOID),
            METHOD_CLOSE_JDBI
        ),
        method(
            type(VOID),
            METHOD_CLOSE_JDBI,
            args(annotatedType(types(type(ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),

        annotatedMethod(
            annotations(ANNOTATION_NONNULL),
            type(R),
            typeParams(R),
            METHOD_WITH_JDBI,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JDBI_CALLBACK_TYPE, R)),
            throwing(type(RUNTIME_JDBI_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(ANNOTATION_NONNULL)),
            type(R),
            typeParams(R),
            METHOD_WITH_JDBI,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(ANNOTATION_NONNULL), JDBI_CALLBACK_TYPE, R)),
            throwing(type(RUNTIME_JDBI_EXCEPTION_TYPE))
        )
    };
}
