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

package org.jrdf.writer.rdfxml;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.URIReference;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.ReflectTestUtil.checkFieldValue;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Modifier;

public class PredicateObjectWriterImplUnitTest extends TestCase {
    private static final Class<?>[] PARAM_TYPES = new Class[]{RdfNamespaceMap.class, BlankNodeRegistry.class,
        XMLStreamWriter.class};
    private static final ParameterDefinition WRITE_PREDICATE_OBJECT = new ParameterDefinition(
        new String[]{"predicate", "object"}, new Class[]{PredicateNode.class, ObjectNode.class});
    private MockFactory factory = new MockFactory();
    private RdfNamespaceMap map;
    private BlankNodeRegistry blankNodeRegistry;
    private XMLStreamWriter xmlStreamWriter;
    private PredicateObjectWriter writer;
    private static final String NODE_ID = "foo";
    private static final XMLStreamException EXPECTED_EXCEPTION = new XMLStreamException();

    public void setUp() {
        map = factory.createMock(RdfNamespaceMap.class);
        blankNodeRegistry = factory.createMock(BlankNodeRegistry.class);
        xmlStreamWriter = factory.createMock(XMLStreamWriter.class);
        writer = new PredicateObjectWriterImpl(map, blankNodeRegistry, xmlStreamWriter);
    }

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(PredicateObjectWriter.class, PredicateObjectWriterImpl.class);
        checkConstructor(PredicateObjectWriterImpl.class, Modifier.PUBLIC, PARAM_TYPES);
        checkConstructNullAssertion(PredicateObjectWriterImpl.class, PARAM_TYPES);
        checkMethodNullAssertions(writer, "writePredicateObject", WRITE_PREDICATE_OBJECT);
    }

    public void testWritePredicateObjectTest() throws Exception {
        URIReference predicate = factory.createMock(URIReference.class);
        URIReference object = factory.createMock(URIReference.class);
        expect(map.replaceNamespace(predicate)).andReturn(NODE_ID);
        xmlStreamWriter.writeStartElement(NODE_ID);
        object.accept(writer);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.flush();
        factory.replay();
        writer.writePredicateObject(predicate, object);
        factory.verify();
    }

    public void testVisitBlankNode() throws Exception {
        BlankNode node = factory.createMock(BlankNode.class);
        expect(blankNodeRegistry.getNodeId(node)).andReturn(NODE_ID);
        xmlStreamWriter.writeAttribute("rdf:nodeID", NODE_ID);
        factory.replay();
        writer.visitBlankNode(node);
        factory.verify();
    }

    public void testVisitBlankNodeWithException() throws Exception {
        final BlankNode node = factory.createMock(BlankNode.class);
        expect(blankNodeRegistry.getNodeId(node)).andReturn(NODE_ID);
        xmlStreamWriter.writeAttribute("rdf:nodeID", NODE_ID);
        expectLastCall().andThrow(EXPECTED_EXCEPTION);
        factory.replay();
        writer.visitBlankNode(node);
        factory.verify();
        checkFieldValue(writer, "exception", EXPECTED_EXCEPTION);
    }
}
