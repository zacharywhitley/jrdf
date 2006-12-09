/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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
 */

package org.jrdf.sparql.builder;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairImpl;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.sparql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.sparql.parser.node.AQnameObjectTripleElement;
import org.jrdf.sparql.parser.node.AQnameResourceTripleElement;
import org.jrdf.sparql.parser.node.AResourceObjectTripleElement;
import org.jrdf.sparql.parser.node.AResourceResourceTripleElement;
import org.jrdf.sparql.parser.node.AVariable;
import org.jrdf.sparql.parser.node.AVariableObjectTripleElement;
import org.jrdf.sparql.parser.node.AVariableResourceTripleElement;
import org.jrdf.sparql.parser.node.PLiteral;
import org.jrdf.sparql.parser.node.TIdentifier;
import org.jrdf.sparql.parser.parser.ParserException;

import java.net.URI;
import java.util.Map;

// TODO (AN) Too much coupling still!!

@SuppressWarnings({ "MethodParameterOfConcreteClass", "CastToConcreteClass", "LocalVariableOfConcreteClass" })
public final class ElementBuilderImpl extends DepthFirstAdapter implements ElementBuilder {
    private static final String SINGLE_QUOTE = "'";
    private AttributeValuePair avp;
    private final NodeType nodeType;
    private final Node graphNode;
    private final Attribute attribute;
    private final Graph currentGraph;
    private final Map<String, String> prefixMap;
    private ParserException exception;

    public ElementBuilderImpl(NodeType nodeType, Node graphNode, Attribute attribute, Graph currentGraph,
            Map<String, String> prefixMap) {
        this.nodeType = nodeType;
        this.graphNode = graphNode;
        this.attribute = attribute;
        this.currentGraph = currentGraph;
        this.prefixMap = prefixMap;
    }

    public AttributeValuePair getElement() throws ParserException {
        if (exception != null) {
            throw exception;
        } else {
            return avp;
        }
    }

    @Override
    public void caseAResourceResourceTripleElement(AResourceResourceTripleElement node) {
        String text = getStringForm(node);
        avp = new AttributeValuePairImpl(attribute, createResource(text));
    }

    @Override
    public void caseAQnameResourceTripleElement(AQnameResourceTripleElement node) {
        avp = createQNameResource(node.getNcnamePrefix().getText(), node.getNcName().getText());
    }

    @Override
    public void caseAVariableResourceTripleElement(AVariableResourceTripleElement node) {
        avp = createAttributeValuePair(nodeType, graphNode, getVariableName(node));
    }

    @Override
    public void caseAResourceObjectTripleElement(AResourceObjectTripleElement node) {
        String text = getStringForm(node);
        avp = new AttributeValuePairImpl(attribute, createResource(text));
    }

    @Override
    public void caseAQnameObjectTripleElement(AQnameObjectTripleElement node) {
        avp = createQNameResource(node.getNcnamePrefix().getText(), node.getNcName().getText());
    }

    @Override
    public void caseAVariableObjectTripleElement(AVariableObjectTripleElement node) {
        avp = createAttributeValuePair(nodeType, graphNode, getVariableName(node));
    }

    @Override
    public void caseALiteralObjectTripleElement(ALiteralObjectTripleElement node) {
        String text = extractTextFromLiteralNode(node.getLiteral());
        avp = new AttributeValuePairImpl(attribute, createLiteral(text));
    }

    private AttributeValuePair createAttributeValuePair(NodeType type, Node anyNode, String variableName) {
        AttributeName newAttributeName = new VariableName(variableName);
        Attribute att = new AttributeImpl(newAttributeName, type);
        return new AttributeValuePairImpl(att, anyNode);
    }

    private String getVariableName(AVariableResourceTripleElement element) {
        AVariable variable = (AVariable) element.getVariable();
        return variable.getVariableprefix().toString().trim() + variable.getIdentifier().toString().trim();
    }

    private String getVariableName(AVariableObjectTripleElement element) {
        AVariable variable = (AVariable) element.getVariable();
        return variable.getVariableprefix().toString().trim() + variable.getIdentifier().toString().trim();
    }

    private AttributeValuePair createQNameResource(String identifier, String ncName) {
        if (!prefixMap.keySet().contains(identifier)) {
            exception = new ParserException(new TIdentifier("identifier"), "Couldn't find prefix: " + identifier);
            return null;
        } else {
            String stringForm = prefixMap.get(identifier) + ncName;
            URIReference uriReference = createResource(stringForm);
            return new AttributeValuePairImpl(attribute, uriReference);
        }
    }

// FIXME TJA: For a better way to do this, see Kowari::ItqlIntepreter::toLiteralImpl() &
    // Kowari::ItqlIntepreter::getLiteralText()
    // FIXME TJA: Handle datatypes.
    // FIXME TJA: Handle language code.
    private String extractTextFromLiteralNode(PLiteral literal) {
        return trim(stripQuotes(literal));
    }

    private String stripQuotes(PLiteral literal) {
        String lexicalValue = literal.toString();
        int start = lexicalValue.indexOf(SINGLE_QUOTE) + 1;
        int end = lexicalValue.lastIndexOf(SINGLE_QUOTE);
        return lexicalValue.substring(start, end);
    }

    private String getStringForm(AResourceResourceTripleElement resourceNode) {
        return resourceNode.getResource().getText();
    }

    private String getStringForm(AResourceObjectTripleElement resourceNode) {
        return resourceNode.getResource().getText();
    }

    private String trim(String s) {
        return s.trim();
    }

    private URIReference createResource(String uri) {
        try {
            return getElementFactory().createResource(new URI(uri));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Literal createLiteral(String lexicalValue) {
        try {
            return getElementFactory().createLiteral(lexicalValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private GraphElementFactory getElementFactory() {
        return currentGraph.getElementFactory();
    }
}
