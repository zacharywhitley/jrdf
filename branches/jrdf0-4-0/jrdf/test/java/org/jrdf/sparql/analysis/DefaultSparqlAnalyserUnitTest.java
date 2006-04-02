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

package org.jrdf.sparql.analysis;

import junit.framework.TestCase;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.URIReference;
import org.jrdf.query.ConstraintTriple;
import org.jrdf.query.Query;
import org.jrdf.sparql.builder.LiteralTripleSpec;
import org.jrdf.sparql.builder.VariableTripleSpec;
import org.jrdf.sparql.parser.SableCcNodeTestUtil;
import org.jrdf.sparql.parser.analysis.Analysis;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.sparql.parser.node.AResourceResourceTripleElement;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.TResource;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.util.test.TripleTestUtil;
import org.jrdf.util.test.FieldPropertiesTestUtil;

import java.net.URI;

/**
 * Unit test for {@link DefaultSparqlAnalyser}.
 *
 * @author Tom Adams
 * @version $Id$
 */
public final class DefaultSparqlAnalyserUnitTest extends TestCase {

    private static final URI URI_BOOK_1 = TripleTestUtil.URI_BOOK_1;
    private static final URI URI_DC_TITLE = TripleTestUtil.URI_DC_TITLE;
    private static final String VARIABLE_NAME_TITLE = SparqlQueryTestUtil.VARIABLE_NAME_TITLE;
    private static final Object EXPECTED_PARSED_VARIABLE = AnyObjectNode.ANY_OBJECT_NODE;
    private static final String FIELD_NO_QUERY = "NO_QUERY";
    private static final String LITERAL_BOOK_TITLE = TripleTestUtil.LITERAL_BOOK_TITLE;
    private static final String EXPECTED_PARSED_LITERAL = LITERAL_BOOK_TITLE;
    private static final VariableTripleSpec TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE =
            new VariableTripleSpec(URI_BOOK_1, URI_DC_TITLE, VARIABLE_NAME_TITLE);
    private static final LiteralTripleSpec TRIPLE_SPEC_BOOK_1_DC_TITLE_LITERAL =
            new LiteralTripleSpec(URI_BOOK_1, URI_DC_TITLE, LITERAL_BOOK_TITLE);

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkExtensionOf(Analysis.class, SparqlAnalyser.class);
        ClassPropertiesTestUtil
                .checkImplementationOfInterfaceAndFinal(SparqlAnalyser.class, DefaultSparqlAnalyser.class);
        ClassPropertiesTestUtil.checkExtensionOf(DepthFirstAdapter.class, DefaultSparqlAnalyser.class);
    }

    public void testNoQueryConstant() {
        checkNoQueryConstantStaticFinal();
        checkNoQueryConstantImmutable();
        checkNoQueryConstantDoesNothing();
        checkNoQueryConstantType();
    }

    private void checkNoQueryConstantStaticFinal() {
        FieldPropertiesTestUtil.checkFieldFinal(SparqlAnalyser.class, FIELD_NO_QUERY);
        FieldPropertiesTestUtil.checkFieldStatic(SparqlAnalyser.class, FIELD_NO_QUERY);
    }

    // Note. getQuery() should always return SparqlAnalyser.NO_QUERY when not applied via SableCC framework.
    public void testGetQueryAlwaysReturnsNoQueryWhenNotApplied() {
        SparqlAnalyser analyser = createAnalyser();
        checkFirstGetQueryReturnsNoQuery(analyser);
        checkFirstGetQueryReturnsNoQuery(analyser);
    }

    public void testParsingSingleTripleReturnsCorrectQuery() {
        DefaultSparqlAnalyser analyser = createAnalyser();
        ATriple expectedTriple = createTripleNodeWithVariable();
        ConstraintTriple actualTriple = parseATripleOnce(analyser, expectedTriple);
        checkAnalysedTriple(expectedTriple, actualTriple);
    }

    // FIXME TJA: Test using object as URI & blank node
    public void testParsingMultipleTriplesReturnsCorrectQuery() {
        DefaultSparqlAnalyser analyser = createAnalyser();
        checkAnalysedTriple(analyser, createTripleNodeWithVariable());
        checkAnalysedTriple(analyser, createTripleNodeWithVariable());
        checkAnalysedTriple(analyser, createTripleNodeWithLiteral());
        checkAnalysedTriple(analyser, createTripleNodeWithVariable());
        checkAnalysedTriple(analyser, createTripleNodeWithLiteral());
        checkAnalysedTriple(analyser, createTripleNodeWithLiteral());
    }

    // FIXME TJA: Do we want this requirement? That we get the same query object back consequutive times?
    public void testGetQueryReturnsImmutableQueriesConsistently() {
        DefaultSparqlAnalyser analyser = createAnalyser();
        analyser.outATriple(createTripleNodeWithLiteral());
        Query query1 = analyser.getQuery();
        Query query2 = analyser.getQuery();
        assertEquals(query1, query2);
    }

    private void checkAnalysedTriple(DefaultSparqlAnalyser analyser, ATriple expectedTriple) {
        ConstraintTriple actualTriple = parseTriple(analyser, expectedTriple);
        checkAnalysedTriple(expectedTriple, actualTriple);
    }

    private void checkAnalysedTriple(ATriple expectedTriple, ConstraintTriple actualTriple) {
        checkSubject(expectedTriple, actualTriple);
        checkPredicate(expectedTriple, actualTriple);
        checkObject(actualTriple);
    }

    private void checkSubject(ATriple expectedTriple, ConstraintTriple actualTriple) {
        AResourceResourceTripleElement expectedSubject = (AResourceResourceTripleElement) expectedTriple.getSubject();
        URIReference actualSubject = (URIReference) actualTriple.getTriple().getSubject();
        checkResource(expectedSubject.getResource(), actualSubject.getURI());
    }

    private void checkPredicate(ATriple expectedTriple, ConstraintTriple actualTriple) {
        AResourceResourceTripleElement expectedPredicate =
                (AResourceResourceTripleElement) expectedTriple.getPredicate();
        URIReference actualPredicate = (URIReference) actualTriple.getTriple().getPredicate();
        checkResource(expectedPredicate.getResource(), actualPredicate.getURI());
    }

    private void checkObject(ConstraintTriple actualTriple) {
        checkObject(actualTriple.getTriple().getObject());
    }

    private void checkObject(ObjectNode actualObject) {
        // FIXME TJA: Try to remove instanceof.
        if (actualObject instanceof Literal) checkObject((Literal) actualObject);
        else checkVariableObject(actualObject);
    }

    private void checkObject(Literal actualObject) {
        assertEquals(EXPECTED_PARSED_LITERAL, actualObject.getLexicalForm());
    }

    private void checkVariableObject(Object actualObject) {
        assertEquals(EXPECTED_PARSED_VARIABLE, actualObject);
    }

    private void checkResource(TResource expectedResource, URI actualUri) {
        assertEquals(expectedResource.getText(), actualUri.toString());
    }

    private ConstraintTriple parseATripleOnce(DefaultSparqlAnalyser analyser, ATriple tripleToParse) {
        checkFirstGetQueryReturnsNoQuery(analyser);
        return parseTriple(analyser, tripleToParse);
    }

    private ConstraintTriple parseTriple(DefaultSparqlAnalyser analyser, ATriple tripleToParse) {
        return analyseTriple(analyser, tripleToParse);
    }

    private ConstraintTriple analyseTriple(DefaultSparqlAnalyser analyser, ATriple triple) {
        Query query = analyseQuery(analyser, triple);
        return (ConstraintTriple) query.getConstraintExpression();
    }

    private Query analyseQuery(DefaultSparqlAnalyser analyser, ATriple expectedTriple) {
        analyser.outATriple(expectedTriple);
        return analyser.getQuery();
    }

    private ATriple createTripleNodeWithVariable() {
        return SableCcNodeTestUtil.createTripleNodeWithVariable(TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE);
    }

    private ATriple createTripleNodeWithLiteral() {
        return SableCcNodeTestUtil.createTripleNodeWithLiteral(TRIPLE_SPEC_BOOK_1_DC_TITLE_LITERAL);
    }

    private void checkFirstGetQueryReturnsNoQuery(SparqlAnalyser analyser) {
        assertEquals(SparqlAnalyser.NO_QUERY, analyser.getQuery());
    }

    private void checkNoQueryConstantDoesNothing() {
        try {
            SparqlAnalyser.NO_QUERY.getProjectedVariables();
            fail("SparqlAnalysis.NO_QUERY.getProjectedVariables() should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            SparqlAnalyser.NO_QUERY.getConstraintExpression();
            fail("SparqlAnalysis.NO_QUERY.getConstraintExpression() should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }

    private void checkNoQueryConstantImmutable() {
        assertNotNull(SparqlAnalyser.NO_QUERY);
        Query x = SparqlAnalyser.NO_QUERY;
        Query y = SparqlAnalyser.NO_QUERY;
        assertEquals(x, y);
        assertTrue(x == y);
    }

    private void checkNoQueryConstantType() {
        ClassPropertiesTestUtil.checkInstanceImplementsInterface(Query.class, SparqlAnalyser.NO_QUERY);
    }

    private DefaultSparqlAnalyser createAnalyser() {
        return new DefaultSparqlAnalyser();
    }
}
