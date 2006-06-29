/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Literal;
import org.jrdf.graph.URIReference;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairImpl;
import org.jrdf.query.relation.mem.SortedAttributeValuePairHelper;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.sparql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.sparql.parser.node.AResourceResourceTripleElement;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.AVariable;
import org.jrdf.sparql.parser.node.AVariableObjectTripleElement;
import org.jrdf.sparql.parser.node.AVariableResourceTripleElement;
import org.jrdf.sparql.parser.node.PLiteral;
import org.jrdf.sparql.parser.node.PObjectTripleElement;
import org.jrdf.sparql.parser.node.PResourceTripleElement;
import org.jrdf.util.param.ParameterUtil;

import java.net.URI;
import java.util.SortedSet;

/**
 * Constructs {@link org.jrdf.graph.Triple}s from {@link org.jrdf.sparql.parser.node.ATriple}s.
 *
 * @author Tom Adams
 * @version $Revision$
 */

// TODO (AN) Too much coupling here!

public final class TripleBuilderImpl implements TripleBuilder {
    // FIXME TJA: Test drive out code to do with graphs, creating triples & resources, etc. into a utility.

    private static final String SINGLE_QUOTE = "'";
    private final SortedAttributeValuePairHelper avpHelper;
    private Graph currentGraph;
    private long tripleCounter;
    private PositionName subjectAttName;
    private PositionName predicateAttName;
    private PositionName objectAttName;

    public TripleBuilderImpl(SortedAttributeValuePairHelper avpHelper, long counterStart) {
        this.avpHelper = avpHelper;
        this.tripleCounter = counterStart;
    }

    /**
     * Builds the given <var>tripleNode</var> into a local Triple.
     *
     * @param tripleNode The tripleNode to build into a JRDF class instance.
     * @return The local version of the given <var>tripleNode</var>
     */
    public SortedSet<AttributeValuePair> build(ATriple tripleNode, Graph graph) {
        ParameterUtil.checkNotNull("tripleNode", tripleNode);
        ParameterUtil.checkNotNull("graph", graph);

        currentGraph = graph;

        subjectAttName = new PositionName("SUBJECT" + tripleCounter);
        predicateAttName = new PositionName("PREDICATE" + tripleCounter);
        objectAttName = new PositionName("OBJECT" + tripleCounter);
        tripleCounter++;

        // FIXME TJA: Check format () of triple here (is this done by the grammar now?).
        AttributeValuePair subject = buildSubject(tripleNode);
        AttributeValuePair predicate = buildPredicate(tripleNode);
        AttributeValuePair object = buildObject(tripleNode);
        return avpHelper.createAvp(new AttributeValuePair[] {subject, predicate, object});
    }

    // FIXME TJA: We should not get to here, having to check that the field is a resource. Should be handled earlier.
    private AttributeValuePair buildSubject(ATriple tripleNode) {
        PResourceTripleElement subject = tripleNode.getSubject();
        if (subject instanceof AVariableResourceTripleElement) {
            AVariableResourceTripleElement element = (AVariableResourceTripleElement) subject;
            String variableName = getVariableName(element);
            Attribute att = new AttributeImpl(new VariableName(variableName), new SubjectNodeType());
            return new AttributeValuePairImpl(att, ANY_SUBJECT_NODE);
        } else {
            AResourceResourceTripleElement resource = (AResourceResourceTripleElement) tripleNode.getSubject();
            Attribute att = new AttributeImpl(subjectAttName, new SubjectNodeType());
            return new AttributeValuePairImpl(att, createResource(getStringForm(resource)));
        }
    }

    private AttributeValuePair buildPredicate(ATriple tripleNode) {
        PResourceTripleElement predicate = tripleNode.getPredicate();
        if (predicate instanceof AVariableResourceTripleElement) {
            AVariableResourceTripleElement element = (AVariableResourceTripleElement) predicate;
            String variableName = getVariableName(element);
            Attribute att = new AttributeImpl(new VariableName(variableName), new PredicateNodeType());
            return new AttributeValuePairImpl(att, ANY_PREDICATE_NODE);
        } else {
            AResourceResourceTripleElement resource = (AResourceResourceTripleElement) tripleNode.getPredicate();
            Attribute att = new AttributeImpl(predicateAttName, new PredicateNodeType());
            return new AttributeValuePairImpl(att, createResource(getStringForm(resource)));
        }

    }

    private AttributeValuePair buildObject(ATriple tripleNode) {
        // FIXME TJA: Use the visitor pattern to do this.
        PObjectTripleElement object = tripleNode.getObject();
        if (object instanceof AVariableObjectTripleElement) {
            AVariableObjectTripleElement element = (AVariableObjectTripleElement) object;
            String variableName = getVariableName(element);
            Attribute att = new AttributeImpl(new VariableName(variableName), new ObjectNodeType());
            return new AttributeValuePairImpl(att, ANY_OBJECT_NODE);
        } else {
            PLiteral literal = ((ALiteralObjectTripleElement) object).getLiteral();
            String text = extractTextFromLiteralNode(literal);
            Attribute att = new AttributeImpl(objectAttName, new ObjectNodeType());
            Literal literalNode = createLiteral(text);
            return new AttributeValuePairImpl(att, literalNode);
        }
    }

    private String getVariableName(AVariableResourceTripleElement element) {
        AVariable variable = (AVariable) element.getVariable();
        return variable.getVariableprefix().toString().trim() + variable.getIdentifier().toString().trim();
    }

    private String getVariableName(AVariableObjectTripleElement element) {
        AVariable variable = (AVariable) element.getVariable();
        return variable.getVariableprefix().toString().trim() + variable.getIdentifier().toString().trim();
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
