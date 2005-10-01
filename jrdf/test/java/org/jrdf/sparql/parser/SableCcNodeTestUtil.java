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

package org.jrdf.sparql.parser;

import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.sparql.builder.VariableTripleSpec;
import org.jrdf.sparql.builder.LiteralTripleSpec;
import org.jrdf.sparql.parser.node.ALiteral;
import org.jrdf.sparql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.sparql.parser.node.AResourceResourceTripleElement;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.AUnescapedStrand;
import org.jrdf.sparql.parser.node.AVariable;
import org.jrdf.sparql.parser.node.AVariableObjectTripleElement;
import org.jrdf.sparql.parser.node.PObjectTripleElement;
import org.jrdf.sparql.parser.node.PResourceTripleElement;
import org.jrdf.sparql.parser.node.PStrand;
import org.jrdf.sparql.parser.node.TIdentifier;
import org.jrdf.sparql.parser.node.TQuote;
import org.jrdf.sparql.parser.node.TResource;
import org.jrdf.sparql.parser.node.TText;
import org.jrdf.sparql.parser.node.TVariableprefix;
import org.jrdf.sparql.parser.node.X2PStrand;
import org.jrdf.sparql.parser.node.XPStrand;

import java.net.URI;

/**
 * Utilities for creating SableCC nodes.
 * @author Tom Adams
 * @version $Revision$
 */
public final class SableCcNodeTestUtil {

    private static final String VARIABLE_PREFIX = SparqlQueryTestUtil.VARIABLE_PREFIX;
    private static final String SPARQL_QUOTE = "'";

    private SableCcNodeTestUtil() {}

    public static ATriple createTripleNodeWithVariable(VariableTripleSpec tripleSpec) {
        PResourceTripleElement subject = createResourceElement(tripleSpec.getSubjectUri());
        PResourceTripleElement predicate = createResourceElement(tripleSpec.getPredicateUri());
        PObjectTripleElement object = createVariableElement(tripleSpec.getVariableName());
        return new ATriple(subject, predicate, object);
    }

    public static ATriple createTripleNodeWithLiteral(LiteralTripleSpec tripleSpec) {
        PResourceTripleElement subject = createResourceElement(tripleSpec.getSubjectUri());
        PResourceTripleElement predicate = createResourceElement(tripleSpec.getPredicateUri());
        PObjectTripleElement object = createLiteralElement(tripleSpec.getLiteral());
        return new ATriple(subject, predicate, object);
    }

    public static PResourceTripleElement createResourceElement(URI subjectUri) {
        return new AResourceResourceTripleElement(new TResource(subjectUri.toString()));
    }

    public static AVariableObjectTripleElement createVariableElement(String variableName) {
        AVariable variable = new AVariable(new TVariableprefix(VARIABLE_PREFIX), new TIdentifier(variableName));
        return new AVariableObjectTripleElement(variable);
    }

    public static ALiteralObjectTripleElement createLiteralElement(String literalText) {
        return new ALiteralObjectTripleElement(createLiteralNode(literalText));
    }

    public static ALiteral createLiteralNode(String literalText) {
        PStrand text = new AUnescapedStrand(new TText(literalText));
        XPStrand strand = new X2PStrand(text);
        return new ALiteral(createQuoteNode(), strand, createQuoteNode());
    }

    public static TQuote createQuoteNode() {
        return new TQuote(SPARQL_QUOTE);
    }
}
