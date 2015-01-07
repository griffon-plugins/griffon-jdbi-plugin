/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.compile.jdbi.ast.transform;

import griffon.plugins.jdbi.JdbiHandler;
import griffon.transform.JdbiAware;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.ast.transform.AbstractASTTransformation;
import org.codehaus.griffon.compile.jdbi.JdbiAwareConstants;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * Handles generation of code for the {@code @JdbiAware} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(JdbiAware.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class JdbiAwareASTTransformation extends AbstractASTTransformation implements JdbiAwareConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(JdbiAwareASTTransformation.class);
    private static final ClassNode JDBI_HANDLER_CNODE = makeClassSafe(JdbiHandler.class);
    private static final ClassNode JDBI_AWARE_CNODE = makeClassSafe(JdbiAware.class);

    /**
     * Convenience method to see if an annotated node is {@code @JdbiAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasJdbiAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (JDBI_AWARE_CNODE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        addJdbiHandlerIfNeeded(source, (AnnotationNode) nodes[0], (ClassNode) nodes[1]);
    }

    public static void addJdbiHandlerIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, JdbiAware.class.getSimpleName(), JDBI_HANDLER_TYPE)) {
            LOG.debug("Injecting {} into {}", JDBI_HANDLER_TYPE, classNode.getName());
            apply(classNode);
        }
    }

    /**
     * Adds the necessary field and methods to support jdbi handling.
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(@Nonnull ClassNode declaringClass) {
        injectInterface(declaringClass, JDBI_HANDLER_CNODE);
        Expression jdbiHandler = injectedField(declaringClass, JDBI_HANDLER_CNODE, JDBI_HANDLER_FIELD_NAME);
        addDelegateMethods(declaringClass, JDBI_HANDLER_CNODE, jdbiHandler);
    }
}