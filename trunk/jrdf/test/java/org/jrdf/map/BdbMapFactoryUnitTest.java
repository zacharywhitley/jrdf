/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.map;

import junit.framework.TestCase;

public class BdbMapFactoryUnitTest extends TestCase {

    public void testKeepJUnitHappy() {
        assertTrue(true);
    }
    
//    private static final String NODE_ID = "foo" + System.currentTimeMillis();
//    private static final long UNIQUE_ID_1 = new Random().nextLong();
//    private static final long UNIQUE_ID_2 = new Random().nextLong();
//    private static final long UNIQUE_ID_3 = new Random().nextLong();
//    private final MockFactory mockFactory = new MockFactory();
//    private GraphElementFactory graphElementFactory;
//    private BlankNode blankNode;
//    private ParserBlankNodeFactory nodeFactory;
//    private BdbHandler handler;
//    private Environment env;
//    private StoredClassCatalog catalog;
//
//    public void setUp() throws DatabaseException {
//        handler = mockFactory.createMock(BdbHandler.class);
//        graphElementFactory = mockFactory.createMock(GraphElementFactory.class);
////        blankNode = new JeParserBlankNodeFactoryUnitTest.BlankNodeImpl(UNIQUE_ID_1);
//        env = mockFactory.createMock(Environment.class);
//        catalog = mockFactory.createMock(StoredClassCatalog.class);
//    }
//
//    public void testCreateMap() throws Exception {
//        assertTrue(true);
//        // Create mock and expectation for Environment:
//        expect(handler.setUpEnvironment()).andReturn(env);
//        DatabaseConfig databaseConfig = mockFactory.createMock(DatabaseConfig.class);
//        Database database = mockFactory.createNiceMock(Database.class);
//        expect(handler.setUpDatabase(false)).andReturn(databaseConfig);
//        expect(handler.setupCatalog(env, "java_class_catalog_blank_node", databaseConfig)).andReturn(catalog);
//        expect(env.openDatabase(null, "blank_node_factory_db", databaseConfig)).andReturn(database);
//        // * openDatabase
//        // Create mock for DatabaseConfig
//        // Create mock and expections for Handler:
//        // * setUpEnvironement - and returns mock environment.
//        // * setUpDatabase
//        // * setUpCatalog
//        mockFactory.replay();
//        nodeFactory = new JeParserBlankNodeFactory(handler, graphElementFactory);
//        // call the constructor with mock objects - handler and mock GraphElementFactory (no expectations).
//        mockFactory.verify();
//    }
//
//    public void testCreateBlankNode() throws Exception {
//        expect(graphElementFactory.createResource()).andReturn(blankNode);
//        mockFactory.replay();
//        BlankNode actualBlankNode = nodeFactory.createBlankNode();
//        assertTrue("Expected the blank node to be the one created in the mock", actualBlankNode == blankNode);
//        mockFactory.verify();
//    }
//
//    public void testCreateBlankNodeWithId() throws Exception {
//        expect(graphElementFactory.createResource()).andReturn(blankNode);
//        expect(graphElementFactory.createResource()).andReturn(new JeParserBlankNodeFactoryUnitTest.BlankNodeImpl(
//            UNIQUE_ID_2)).anyTimes();
//        mockFactory.replay();
//        checkBlankNodeCreationById();
//        checkBlankNodeCreationById();
//        BlankNode actualBlankNode = nodeFactory.createBlankNode("bar");
//        assertFalse("Expected the blank node to be different with a different id", actualBlankNode.equals(blankNode));
//        mockFactory.verify();
//    }
//
//    public void testClear() throws Exception {
//        expect(graphElementFactory.createResource()).andReturn(new JeParserBlankNodeFactoryUnitTest.BlankNodeImpl(
//            UNIQUE_ID_1));
//        expect(graphElementFactory.createResource()).andReturn(new JeParserBlankNodeFactoryUnitTest.BlankNodeImpl(
//            UNIQUE_ID_2));
//        expect(graphElementFactory.createResource()).andReturn(new JeParserBlankNodeFactoryUnitTest.BlankNodeImpl(
//            UNIQUE_ID_3));
//        mockFactory.replay();
//        BlankNode firstBlankNode = nodeFactory.createBlankNode();
//        nodeFactory.clear();
//        BlankNode secondBlankNode = nodeFactory.createBlankNode(NODE_ID);
//        BlankNode thirdBlankNode = nodeFactory.createBlankNode(NODE_ID);
//        BlankNode fourthBlankNode = nodeFactory.createBlankNode();
//        nodeFactory.clear();
//        assertTrue(!firstBlankNode.equals(secondBlankNode));
//        assertTrue(secondBlankNode.equals(thirdBlankNode));
//        assertTrue(!fourthBlankNode.equals(firstBlankNode));
//        mockFactory.verify();
//    }
//
//    private void checkBlankNodeCreationById() throws GraphElementFactoryException {
//        BlankNode actualBlankNode = nodeFactory.createBlankNode(NODE_ID);
//        assertTrue("Expected the blank node to be the one created in the mock by id expected: " + blankNode + " Got: " +
//            actualBlankNode, actualBlankNode.equals(blankNode));
//    }
//
//    private static class BlankNodeImpl implements BlankNode, Serializable {
//        private final Long uniqueId;
//
//        public BlankNodeImpl(Long uniqueId) {
//            this.uniqueId = uniqueId;
//        }
//
//        public boolean isURIReference() {
//            return false;
//        }
//
//        public int hashCode() {
//            return uniqueId.hashCode();
//        }
//
//        public boolean equals(Object obj) {
//            JeParserBlankNodeFactoryUnitTest.BlankNodeImpl impl = (JeParserBlankNodeFactoryUnitTest.BlankNodeImpl) obj;
//            return impl.uniqueId.equals(uniqueId);
//        }
//
//        public String toString() {
//            return "Internal Blank node: " + uniqueId;
//        }
//
//        public void accept(TypedNodeVisitor visitor) {
//        }
//    }
}
