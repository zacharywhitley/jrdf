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

package org.jrdf.sparql.builder;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.type.NodeType;
import static org.jrdf.sparql.builder.TokenHelper.getResource;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.sparql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.sparql.parser.node.AQnameObjectTripleElement;
import org.jrdf.sparql.parser.node.AQnameQnameElement;
import org.jrdf.sparql.parser.node.AQnameResourceTripleElement;
import org.jrdf.sparql.parser.node.AResourceObjectTripleElement;
import org.jrdf.sparql.parser.node.AResourceResourceTripleElement;
import org.jrdf.sparql.parser.node.AVariable;
import org.jrdf.sparql.parser.node.AVariableObjectTripleElement;
import org.jrdf.sparql.parser.node.AVariableResourceTripleElement;
import org.jrdf.sparql.parser.node.TIdentifier;
import org.jrdf.sparql.parser.parser.ParserException;

import java.net.URI;
import static java.net.URI.create;
import java.util.HashMap;
import java.util.Map;

// TODO (AN) Too much coupling still!!
public final class ElementBuilderImpl extends DepthFirstAdapter implements ElementBuilder {
    private final NodeType nodeType;
    private final Node graphNode;
    private final Attribute attribute;
    private final Graph currentGraph;
    private final Map<String, String> prefixMap;
    private Map<Attribute, Node> avp = new HashMap<Attribute, Node>();
    private ParserException exception;
    private LiteralBuilder literalBuilder;

    public ElementBuilderImpl(NodeType newNodeType, Node newGraphNode, Attribute newAttribute, Graph newCurrentGraph,
            Map<String, String> newPrefixMap) {
        this.nodeType = newNodeType;
        this.graphNode = newGraphNode;
        this.attribute = newAttribute;
        this.currentGraph = newCurrentGraph;
        this.prefixMap = newPrefixMap;
        literalBuilder = new LiteralBuilderImpl(newCurrentGraph.getElementFactory(), newPrefixMap);
    }

    public Map<Attribute, Node> getElement() throws ParserException {
        if (exception != null) {
            throw exception;
        } else {
            return avp;
        }
    }

    @Override
    public void caseAResourceResourceTripleElement(AResourceResourceTripleElement node) {
        Node value = createResource(getResource(node.getResource()));
        avp.put(attribute, value);
    }

    @Override
    public void caseAQnameResourceTripleElement(AQnameResourceTripleElement node) {
        AQnameQnameElement element = (AQnameQnameElement) node.getQnameElement();
        Node value = createQNameResource(element.getNcnamePrefix().getText(), element.getNcName().getText());
        avp.put(attribute, value);
    }

    @Override
    public void caseAVariableResourceTripleElement(AVariableResourceTripleElement node) {
        createAttributeValuePair(nodeType, graphNode, getVariableName(node));
    }

    @Override
    public void caseAResourceObjectTripleElement(AResourceObjectTripleElement node) {
        Node value = createResource(getResource(node.getResource()));
        avp.put(attribute, value);
    }

    @Override
    public void caseAQnameObjectTripleElement(AQnameObjectTripleElement node) {
        AQnameQnameElement element = (AQnameQnameElement) node.getQnameElement();
        Node value = createQNameResource(element.getNcnamePrefix().getText(), element.getNcName().getText());
        avp.put(attribute, value);
    }

    @Override
    public void caseAVariableObjectTripleElement(AVariableObjectTripleElement node) {
        createAttributeValuePair(nodeType, graphNode, getVariableName(node));
    }

    @Override
    public void caseALiteralObjectTripleElement(ALiteralObjectTripleElement node) {
        Node value = createLiteral(node);
        avp.put(attribute, value);
    }

    private void createAttributeValuePair(NodeType type, Node anyNode, String variableName) {
        AttributeName attributeName = new VariableName(variableName);
        Attribute att = new AttributeImpl(attributeName, type);
        avp.put(att, anyNode);
    }

    private String getVariableName(AVariableResourceTripleElement element) {
        AVariable variable = (AVariable) element.getVariable();
        return variable.getVariablename().getText();
    }

    private String getVariableName(AVariableObjectTripleElement element) {
        AVariable variable = (AVariable) element.getVariable();
        return variable.getVariablename().getText();
    }

    private Node createQNameResource(String identifier, String ncName) {
        if (!prefixMap.keySet().contains(identifier)) {
            exception = new ParserException(new TIdentifier("identifier"), "Couldn't find prefix: " + identifier);
            return null;
        } else {
            String stringForm = prefixMap.get(identifier) + ncName;
            return createResource(create(stringForm));
        }
    }

    private URIReference createResource(URI uri) {
        try {
            return currentGraph.getElementFactory().createURIReference(uri);
        } catch (GraphElementFactoryException e) {
            exception = new ParserException(new TIdentifier("identifier"), "Couldn't create URI: " + uri);
            return null;
        }
    }

    private Literal createLiteral(ALiteralObjectTripleElement node) {
        try {
            return literalBuilder.createLiteral(node);
        } catch (ParserException e) {
            exception = e;
            return null;
        }
    }
}
