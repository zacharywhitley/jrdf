/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

package org.jrdf.writer.mem;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.expect;
import org.jrdf.graph.BlankNode;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldIsOfTypePrivateAndFinal;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.ReflectTestUtil.getFieldValue;
import static org.jrdf.util.test.ReflectTestUtil.insertFieldValue;
import org.jrdf.writer.BlankNodeRegistry;

import java.lang.reflect.Modifier;
import java.util.List;

@SuppressWarnings({ "unchecked" })
public class MemBlankNodeRegistryImplUnitTest extends TestCase {
    private static final MockFactory FACTORY = new MockFactory();
    private static final String[] PARAM_NAMES = {"node"};
    private static final Class[] PARAM_TYPES = {BlankNode.class};
    private static final ParameterDefinition GET_NODEID_DEFINITION = new ParameterDefinition(PARAM_NAMES, PARAM_TYPES);
    private static final String FIELD_1_NAME = "blankNodeList";
    private List<BlankNode> bNodes;
    private BlankNode blankNode;
    private BlankNodeRegistry nodeRegistry;

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(BlankNodeRegistry.class, MemBlankNodeRegistryImpl.class);
        checkConstructor(MemBlankNodeRegistryImpl.class, Modifier.PUBLIC);
        checkFieldIsOfTypePrivateAndFinal(MemBlankNodeRegistryImpl.class, List.class, FIELD_1_NAME);
        assertNotNull(getFieldValue(new MemBlankNodeRegistryImpl(), FIELD_1_NAME));
    }

    public void testBadParams() throws Exception {
        checkMethodNullAssertions(new MemBlankNodeRegistryImpl(), "getNodeId", GET_NODEID_DEFINITION);
    }

    public void testGetSuccessfulyNodeId() {
        checkSuccessfulGetNodeId(0);
        checkSuccessfulGetNodeId(95);
    }

    public void testBadNodeId() {
        checkBadGetNodeId(98);
        checkBadGetNodeId(129);
    }

    private void checkSuccessfulGetNodeId(int nodeId) {
        setUpMocks();
        expect(bNodes.indexOf(blankNode)).andReturn(nodeId);
        insertFieldAndCall(nodeId);
    }

    private void checkBadGetNodeId(int nodeId) {
        setUpMocks();
        expect(bNodes.indexOf(blankNode)).andReturn(-1);
        expect(bNodes.add(blankNode)).andReturn(true);
        expect(bNodes.indexOf(blankNode)).andReturn(nodeId);
        insertFieldAndCall(nodeId);
    }

    private void setUpMocks() {
        FACTORY.reset();
        nodeRegistry = new MemBlankNodeRegistryImpl();
        bNodes = FACTORY.createMock(List.class);
        blankNode = FACTORY.createMock(BlankNode.class);
    }

    private void insertFieldAndCall(int nodeId) {
        insertFieldValue(nodeRegistry, FIELD_1_NAME, bNodes);
        FACTORY.replay();
        String s = nodeRegistry.getNodeId(blankNode);
        FACTORY.verify();
        assertEquals("bNode_" + nodeId, s);
    }
}
