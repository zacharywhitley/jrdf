/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.graph.local;

import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.AbstractGraphElementFactoryUnitTest;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.util.test.AssertThrows;
import org.junit.Test;

import java.net.URI;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.graph.NullURI.NULL_URI;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.ReflectTestUtil.insertFieldValue;


/**
 * Implementation of {@link AbstractGraphElementFactoryUnitTest}
 * test case.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @version $Revision$
 */
public class GraphElementFactoryImplUnitTest extends AbstractGraphElementFactoryUnitTest {
    /**
     * Create a new graph of the appropriate type.
     *
     * @return A new graph implementation object.
     */
    public Graph newGraph() throws GraphException {
        return TestJRDFFactory.getFactory().getGraph();
    }

    /**
     * Return the default literal type from the implementation.
     *
     * @return The default Literal type.
     */
    public URI defaultLiteralType() {
        return NULL_URI;
    }

    /**
     * Get the default literal language from the implementation.
     *
     * @return The default Literal language.
     */
    public String getDefaultLiteralLanguage() {
        return "";
    }

    @Test(expected = GraphElementFactoryException.class)
    public void failToCreateBlankNodeCreatesGraphElementFactoryException() {
        // Setup
        final GraphElementFactory elementFactory = newGraph().getElementFactory();
        Localizer mockLocalizer = createMock(Localizer.class);
        insertFieldValue(elementFactory, "localizer", mockLocalizer);
        // Expectations
        mockLocalizer.createLocalBlankNode();
        expectLastCall().andThrow(new IllegalArgumentException("Doesn't matter"));
        replay(mockLocalizer);
        // Do
        elementFactory.createBlankNode();
        verify(mockLocalizer);
    }

    @Test(expected = GraphElementFactoryException.class)
    public void failToCreateResourceBlankNodeGraphElementFactoryException() {
        // Setup
        BlankNode node = createMock(BlankNode.class);
        final GraphElementFactory elementFactory1 = newGraph().getElementFactory();
        ResourceFactory mockResourceFactory = createMock(ResourceFactory.class);
        insertFieldValue(elementFactory1, "resourceFactory", mockResourceFactory);
        // Expectations
        mockResourceFactory.createResource(node);
        expectLastCall().andThrow(new IllegalArgumentException("Doesn't matter"));
        replay(mockResourceFactory);
        // Do
        elementFactory1.createResource(node);
        verify(mockResourceFactory);
    }

    @Test(expected = GraphElementFactoryException.class)
    public void failToCreateURIReferenceResourceGraphElementFactoryException() {
        // Setup
        URIReference node = createMock(URIReference.class);
        final GraphElementFactory elementFactory1 = newGraph().getElementFactory();
        ResourceFactory mockResourceFactory = createMock(ResourceFactory.class);
        insertFieldValue(elementFactory1, "resourceFactory", mockResourceFactory);
        // Expectations
        mockResourceFactory.createResource(node);
        expectLastCall().andThrow(new IllegalArgumentException("Doesn't matter"));
        replay(mockResourceFactory);
        // Do
        elementFactory1.createResource(node);
        verify(mockResourceFactory);
    }

    @Test
    public void createResource() {
        final GraphElementFactory factory = newGraph().getElementFactory();
        String literal = "abc";
        String expectedMessage = "Resource cannot be created from: \"" + literal + "\"";
        final Literal lit = factory.createLiteral(literal);
        assertThrows(GraphElementFactoryException.class, expectedMessage, new AssertThrows.Block() {
            public void execute() throws Throwable {
                factory.createResource(lit);
            }
        });
    }

    @Test
    public void resourcesWrapRawNodes() {
        final GraphElementFactory factory = newGraph().getElementFactory();
        BlankNode blankNode = factory.createBlankNode();
        Resource resource1 = factory.createResource(blankNode);
        Resource resource2 = factory.createResource(blankNode);
        assertThat(blankNode, equalTo(resource1.getUnderlyingNode()));
        assertThat(blankNode, equalTo(resource2.getUnderlyingNode()));
        assertThat(resource1, equalTo(resource1));
    }
}
