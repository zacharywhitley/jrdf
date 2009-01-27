/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.parser.bnodefactory;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.collection.MapFactory;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.util.test.MockFactory;

import java.util.HashMap;

public class ParserBlankNodeFactoryImplUnitTest extends TestCase {
    private static final String NODE_ID = "foo" + System.currentTimeMillis();
    private final MockFactory mockFactory = new MockFactory();
    private GraphElementFactory graphElementFactory;
    private BlankNode blankNode;
    private ParserBlankNodeFactory nodeFactory;
    private MapFactory mapFactory;

    public void setUp() {
        mapFactory = mockFactory.createMock(MapFactory.class);
        expect(mapFactory.createMap(String.class, BlankNode.class)).andReturn(new HashMap<String, BlankNode>());
        graphElementFactory = mockFactory.createMock(GraphElementFactory.class);
        blankNode = mockFactory.createMock(BlankNode.class);
    }

    public void testCreateBlankNode() throws Exception {
        expect(graphElementFactory.createBlankNode()).andReturn(blankNode);
        mockFactory.replay();
        nodeFactory = new ParserBlankNodeFactoryImpl(mapFactory, graphElementFactory);
        BlankNode actualBlankNode = nodeFactory.createBlankNode();
        assertTrue("Expected the blank node to be the one created in the mock", actualBlankNode == blankNode);
        mockFactory.verify();
    }

    public void testCreateBlankNodeWithId() throws Exception {
        expect(graphElementFactory.createBlankNode()).andReturn(blankNode);
        expect(graphElementFactory.createBlankNode()).andReturn(mockFactory.createMock(BlankNode.class)).anyTimes();
        mockFactory.replay();
        nodeFactory = new ParserBlankNodeFactoryImpl(mapFactory, graphElementFactory);
        checkBlankNodeCreationById();
        checkBlankNodeCreationById();
        BlankNode actualBlankNode = nodeFactory.createBlankNode("bar");
        assertTrue("Expected the blank node to be different with a different id", actualBlankNode != blankNode);
        mockFactory.verify();
    }

    public void testClear() throws Exception {
        expect(graphElementFactory.createBlankNode()).andReturn(mockFactory.createMock(BlankNode.class));
        expect(graphElementFactory.createBlankNode()).andReturn(mockFactory.createMock(BlankNode.class));
        mockFactory.replay();
        nodeFactory = new ParserBlankNodeFactoryImpl(mapFactory, graphElementFactory);
        BlankNode firstBlankNode = nodeFactory.createBlankNode(NODE_ID);
        nodeFactory.clear();
        BlankNode secondBlankNode = nodeFactory.createBlankNode(NODE_ID);
        BlankNode thirdBlankNode = nodeFactory.createBlankNode(NODE_ID);
        assertTrue(firstBlankNode != secondBlankNode);
        assertTrue(secondBlankNode == thirdBlankNode);
        mockFactory.verify();
    }

    private void checkBlankNodeCreationById() throws GraphElementFactoryException {
        BlankNode actualBlankNode = nodeFactory.createBlankNode(NODE_ID);
        assertTrue("Expected the blank node to be the one created in the mock by id", actualBlankNode == blankNode);
    }
}
