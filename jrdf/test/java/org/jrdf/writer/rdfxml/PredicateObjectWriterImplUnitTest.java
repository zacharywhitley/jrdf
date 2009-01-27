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

package org.jrdf.writer.rdfxml;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
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
import static org.jrdf.util.test.ReflectTestUtil.getFieldValue;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.WriteException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Modifier;
import java.net.URI;

public class PredicateObjectWriterImplUnitTest extends TestCase {
    private static final Class<?>[] PARAM_TYPES = new Class[]{RdfNamespaceMap.class, BlankNodeRegistry.class,
        XMLStreamWriter.class, XmlLiteralWriter.class};
    private static final String NODE_ID = "foo";
    private static final XMLStreamException EXPECTED_EXCEPTION = new XMLStreamException();
    private static final String LANGUAGE = "en-au";
    private static final String LANGUAGE_LITERAL = "foo@en-au";
    private static final String DATA_TYPE = "xsd:int";
    private static final String NEW_LINE = "\n";
    private MockFactory factory = new MockFactory();
    private RdfNamespaceMap map;
    private BlankNodeRegistry blankNodeRegistry;
    private XMLStreamWriter xmlStreamWriter;
    private PredicateObjectWriter writer;
    private XmlLiteralWriter xmlLiteralWriter;

    public void setUp() {
        map = factory.createMock(RdfNamespaceMap.class);
        blankNodeRegistry = factory.createMock(BlankNodeRegistry.class);
        xmlStreamWriter = factory.createMock(XMLStreamWriter.class);
        xmlLiteralWriter = factory.createMock(XmlLiteralWriter.class);
        writer = new PredicateObjectWriterImpl(map, blankNodeRegistry, xmlStreamWriter, xmlLiteralWriter);
    }

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(PredicateObjectWriter.class, PredicateObjectWriterImpl.class);
        checkConstructor(PredicateObjectWriterImpl.class, Modifier.PUBLIC, PARAM_TYPES);
        checkConstructNullAssertion(PredicateObjectWriterImpl.class, PARAM_TYPES);
        checkMethodNullAssertions(writer, "writePredicateObject", new ParameterDefinition(
            new String[]{"predicate", "object"}, new Class[]{PredicateNode.class, ObjectNode.class}));
        checkMethodNullAssertions(writer, "visitBlankNode", new ParameterDefinition(new String[]{"blankNode"},
            new Class[]{BlankNode.class}));
        checkMethodNullAssertions(writer, "visitURIReference", new ParameterDefinition(new String[]{"uriReference"},
            new Class[]{URIReference.class}));
        checkMethodNullAssertions(writer, "visitLiteral", new ParameterDefinition(new String[]{"literal"},
            new Class[]{Literal.class}));
        checkMethodNullAssertions(writer, "visitNode", new ParameterDefinition(new String[]{"node"},
            new Class[]{Node.class}));
    }

    public void testWritePredicateObjectTest() throws Exception {
        URIReference predicate = factory.createMock(URIReference.class);
        URIReference object = factory.createMock(URIReference.class);
        expect(map.replaceNamespace(predicate)).andReturn(NODE_ID);
        xmlStreamWriter.writeStartElement(NODE_ID);
        object.accept(writer);
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeCharacters(NEW_LINE);
        xmlStreamWriter.flush();
        factory.replay();
        writer.writePredicateObject(predicate, object);
        factory.verify();
    }

    public void testVisitBlankNode() throws Exception {
        final BlankNode node = createNodeExpectations();
        factory.replay();
        writer.visitBlankNode(node);
        factory.verify();
    }

    public void testVisitURIReference() throws Exception {
        URIReference node = factory.createMock(URIReference.class);
        URI uri = factory.createMock(URI.class);
        expect(node.getURI()).andReturn(uri);
        xmlStreamWriter.writeAttribute("rdf:resource", "http://www.slashdot.org");
        factory.replay();
        writer.visitURIReference(node);
        factory.verify();
    }

    public void testVisitLanguageLiteral() throws Exception {
        Literal node = factory.createMock(Literal.class);
        expect(node.isDatatypedLiteral()).andReturn(false).times(2);
        expect(node.isLanguageLiteral()).andReturn(true);
        expect(node.getLanguage()).andReturn(LANGUAGE);
        xmlStreamWriter.writeAttribute("xml:lang", LANGUAGE);
        expect(node.getLexicalForm()).andReturn(LANGUAGE_LITERAL);
        xmlStreamWriter.writeCharacters(LANGUAGE_LITERAL);
        factory.replay();
        writer.visitLiteral(node);
        factory.verify();
    }

    public void testVisitDatatypeLiteral() throws Exception {
        Literal node = factory.createMock(Literal.class);
        expect(node.isDatatypedLiteral()).andReturn(true).times(2);
        expect(node.getDatatypeURI()).andReturn(URI.create(DATA_TYPE));
        expect(node.getDatatypeURI()).andReturn(URI.create(DATA_TYPE));
        xmlStreamWriter.writeAttribute("rdf:datatype", DATA_TYPE);
        expect(node.getLexicalForm()).andReturn("foo^^xsd:int");
        xmlStreamWriter.writeCharacters("foo^^xsd:int");
        factory.replay();
        writer.visitLiteral(node);
        factory.verify();
    }

    public void testVisitNode() throws Exception {
        Node node = factory.createMock(Node.class);
        factory.replay();
        writer.visitNode(node);
        factory.verify();
        Object obj = getFieldValue(writer, "exception");
        assertEquals(WriteException.class, obj.getClass());
        assertTrue(((WriteException) obj).getMessage().startsWith("Unknown object node type:"));
    }

    public void testVisitBlankNodeWithException() throws Exception {
        final BlankNode node = createNodeExpectations();
        expectLastCall().andThrow(EXPECTED_EXCEPTION);
        factory.replay();
        writer.visitBlankNode(node);
        factory.verify();
        checkFieldValue(writer, "exception", EXPECTED_EXCEPTION);
    }

    private BlankNode createNodeExpectations() throws XMLStreamException {
        BlankNode node = factory.createMock(BlankNode.class);
        expect(blankNodeRegistry.getNodeId(node)).andReturn(NODE_ID);
        xmlStreamWriter.writeAttribute("rdf:nodeID", NODE_ID);
        return node;
    }
}
