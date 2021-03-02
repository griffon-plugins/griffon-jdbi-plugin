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
package org.codehaus.griffon.compile.jdbi.ast.transform

import griffon.plugins.jdbi.JdbiHandler
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * @author Andres Almiray
 */
class JdbiAwareASTTransformationSpec extends Specification {
    def 'JdbiAwareASTTransformation is applied to a bean via @JdbiAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''
        @griffon.transform.jdbi.JdbiAware
        class Bean { }
        new Bean()
        ''')

        then:
        bean instanceof JdbiHandler
        JdbiHandler.methods.every { Method target ->
            bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                    candidate.returnType == target.returnType &&
                    candidate.parameterTypes == target.parameterTypes &&
                    candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }

    def 'JdbiAwareASTTransformation is not applied to a JdbiHandler subclass via @JdbiAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''
        import griffon.plugins.jdbi.JdbiCallback
        import griffon.plugins.jdbi.exceptions.RuntimeJdbiException
        import griffon.plugins.jdbi.JdbiHandler

        import griffon.annotations.core.Nonnull
        @griffon.transform.jdbi.JdbiAware
        class JdbiHandlerBean implements JdbiHandler {
            @Override
             <R> R withJdbi(@Nonnull JdbiCallback<R> callback) throws RuntimeJdbiException {
                return null
            }
            @Override
             <R> R withJdbi(@Nonnull String datasourceName, @Nonnull JdbiCallback<R> callback) throws RuntimeJdbiException {
                return null
            }
            @Override
            void closeJdbi(){}
            @Override
            void closeJdbi(@Nonnull String datasourceName){}
        }
        new JdbiHandlerBean()
        ''')

        then:
        bean instanceof JdbiHandler
        JdbiHandler.methods.every { Method target ->
            bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                    candidate.returnType == target.returnType &&
                    candidate.parameterTypes == target.parameterTypes &&
                    candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }
}
