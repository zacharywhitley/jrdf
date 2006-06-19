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

import org.jrdf.JRDFFactoryImpl;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.TripleFactoryException;
import org.jrdf.graph.URIReference;
import org.jrdf.sparql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.sparql.parser.node.AResourceResourceTripleElement;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.AVariableObjectTripleElement;
import org.jrdf.sparql.parser.node.PLiteral;
import org.jrdf.sparql.parser.node.PObjectTripleElement;
import org.jrdf.util.param.ParameterUtil;

import java.net.URI;

/**
 * Constructs {@link org.jrdf.graph.Triple}s from {@link org.jrdf.sparql.parser.node.ATriple}s.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class TripleBuilder {
    // FIXME TJA: Test drive out code to do with graphs, creating triples & resources, etc. into a utility.

    private static final String SINGLE_QUOTE = "'";
    private static final ObjectNode ANY_OBJECT_VALUE = AnyObjectNode.ANY_OBJECT_NODE;

    /**
     * Builds the given <var>tripleNode</var> into a local Triple.
     *
     * @param tripleNode The tripleNode to build into a JRDF class instance.
     * @return The local version of the given <var>tripleNode</var>
     */
    public Triple build(ATriple tripleNode) {
        ParameterUtil.checkNotNull("tripleNode", tripleNode);
        // FIXME TJA: Check format () of triple here (is this done by the grammar now?).
        SubjectNode subject = buildSubject(tripleNode);
        PredicateNode predicate = buildPredicate(tripleNode);
        ObjectNode object = buildObject(tripleNode);
        return createTriple(subject, predicate, object);
    }

    // FIXME TJA: We should not get to here, having to check that the field is a resource. Should be handled earlier.
    private URIReference buildSubject(ATriple tripleNode) {
        AResourceResourceTripleElement subject = (AResourceResourceTripleElement) tripleNode.getSubject();
        return createResource(getStringForm(subject));
    }

    private URIReference buildPredicate(ATriple tripleNode) {
        AResourceResourceTripleElement predicate = (AResourceResourceTripleElement) tripleNode.getPredicate();
        return createResource(getStringForm(predicate));
    }

    private ObjectNode buildObject(ATriple tripleNode) {
        // FIXME TJA: Use the visitor pattern to do this.
        PObjectTripleElement object = tripleNode.getObject();
        if (object instanceof AVariableObjectTripleElement) {
            return ANY_OBJECT_VALUE;
        } else {
            PLiteral literal = ((ALiteralObjectTripleElement) object).getLiteral();
            String text = extractTextFromLiteralNode(literal);
            return createLiteral(text);
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

    private Triple createTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        try {
            return getTripleFactory().createTriple(subject, predicate, object);
        } catch (TripleFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    private TripleFactory getTripleFactory() {
        return createGraph().getTripleFactory();
    }

    private GraphElementFactory getElementFactory() {
        return createGraph().getElementFactory();
    }

    // TODO (AN) Come back and see if we can fix this up.
    private Graph createGraph() {
        return new JRDFFactoryImpl().getNewGraph();
    }
}
