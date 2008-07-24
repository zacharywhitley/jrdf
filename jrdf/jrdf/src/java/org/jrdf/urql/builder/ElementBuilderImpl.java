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

package org.jrdf.urql.builder;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactoryException;
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
import static org.jrdf.urql.builder.TokenHelper.getStringFromTokens;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.urql.parser.node.AResource;
import org.jrdf.urql.parser.node.AResourceObjectTripleElement;
import org.jrdf.urql.parser.node.AResourceResourceTripleElement;
import org.jrdf.urql.parser.node.AVariable;
import org.jrdf.urql.parser.node.AVariableObjectTripleElement;
import org.jrdf.urql.parser.node.AVariableResourceTripleElement;
import org.jrdf.urql.parser.node.TVariableprefix;
import org.jrdf.urql.parser.parser.ParserException;

import java.net.URI;
import java.util.Map;

// TODO (AN) Too much coupling still!!

public final class ElementBuilderImpl extends DepthFirstAdapter implements ElementBuilder {
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

//    @Override
//    public void caseAQnameResourceTripleElement(AQnameResourceTripleElement node) {
//        AQnameQnameElement element = (AQnameQnameElement) node.getQnameElement();
//        avp = createQNameResource(element.getNcnamePrefix().getText(), element.getNcName().getText());
//    }

    @Override
    public void caseAVariableResourceTripleElement(AVariableResourceTripleElement node) {
        avp = createAttributeValuePair(nodeType, graphNode, getVariableName(node));
    }

    @Override
    public void caseAResourceObjectTripleElement(AResourceObjectTripleElement node) {
        String text = getStringForm(node);
        avp = new AttributeValuePairImpl(attribute, createResource(text));
    }

//    @Override
//    public void caseAQnameObjectTripleElement(AQnameObjectTripleElement node) {
//        AQnameQnameElement element = (AQnameQnameElement) node.getQnameElement();
//        avp = createQNameResource(element.getNcnamePrefix().getText(), element.getNcName().getText());
//    }

    @Override
    public void caseAVariableObjectTripleElement(AVariableObjectTripleElement node) {
        avp = createAttributeValuePair(nodeType, graphNode, getVariableName(node));
    }

    @Override
    public void caseALiteralObjectTripleElement(ALiteralObjectTripleElement node) {
        avp = new AttributeValuePairImpl(attribute, createLiteral(node));
    }

    private AttributeValuePair createAttributeValuePair(NodeType type, Node anyNode, String variableName) {
        AttributeName newAttributeName = new VariableName(variableName);
        Attribute att = new AttributeImpl(newAttributeName, type);
        return new AttributeValuePairImpl(att, anyNode);
    }

    private String getVariableName(AVariableResourceTripleElement element) {
        AVariable variable = (AVariable) element.getVariable();
        return variable.getVariablename().toString().trim();
    }

    private String getVariableName(AVariableObjectTripleElement element) {
        AVariable variable = (AVariable) element.getVariable();
        return variable.getVariablename().toString().trim();
    }

//    private AttributeValuePair createQNameResource(String identifier, String ncName) {
//        if (!prefixMap.keySet().contains(identifier)) {
//            exception = new ParserException(new TIdentifier("identifier"), "Couldn't find prefix: " + identifier);
//            return null;
//        } else {
//            String stringForm = prefixMap.get(identifier) + ncName;
//            URIReference uriReference = createResource(stringForm);
//            return new AttributeValuePairImpl(attribute, uriReference);
//        }
//    }

    private String getStringForm(AResourceResourceTripleElement resourceNode) {
        AResource resource = (AResource) resourceNode.getResource();
        return getStringFromTokens(resource.getUrlchar());
    }

    private String getStringForm(AResourceObjectTripleElement resourceNode) {
        AResource resource = (AResource) resourceNode.getResource();
        return getStringFromTokens(resource.getUrlchar());
    }

    private URIReference createResource(String uri) {
        try {
            return currentGraph.getElementFactory().createURIReference(URI.create(uri));
        } catch (GraphElementFactoryException e) {
            exception = new ParserException(new TVariableprefix("identifier"), "Couldn't create URI: " + uri);
            return null;
        }
    }

    private Literal createLiteral(ALiteralObjectTripleElement node) {
        try {
            LiteralBuilder literalBuilder = new LiteralBuilderImpl(currentGraph.getElementFactory(), prefixMap);
            return literalBuilder.createLiteral(node);
        } catch (ParserException e) {
            exception = e;
            return null;
        }
    }
}
