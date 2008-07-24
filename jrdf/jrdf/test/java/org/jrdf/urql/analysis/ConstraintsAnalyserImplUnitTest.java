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

package org.jrdf.urql.analysis;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.Graph;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.urql.builder.LiteralTripleSpec;
import org.jrdf.urql.builder.TripleSpec;
import org.jrdf.urql.builder.VariableTripleSpec;
import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.util.test.TripleTestUtil;

import java.net.URI;

public final class ConstraintsAnalyserImplUnitTest extends TestCase {

    private static final URI URI_BOOK_1 = TripleTestUtil.URI_BOOK_1;
    private static final URI URI_DC_TITLE = TripleTestUtil.URI_DC_TITLE;
    private static final String VARIABLE_NAME_TITLE = SparqlQueryTestUtil.VARIABLE_NAME_TITLE;
    private static final Object EXPECTED_PARSED_VARIABLE = AnyObjectNode.ANY_OBJECT_NODE;
    private static final String FIELD_NO_QUERY = "NO_QUERY";
    private static final String LITERAL_BOOK_TITLE = TripleTestUtil.LITERAL_BOOK_TITLE;
    private static final String EXPECTED_PARSED_LITERAL = LITERAL_BOOK_TITLE;
    private static final VariableTripleSpec TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE =
        new VariableTripleSpec(URI_BOOK_1, URI_DC_TITLE, VARIABLE_NAME_TITLE);
    private static final TripleSpec TRIPLE_SPEC_BOOK_1_DC_TITLE =
        new LiteralTripleSpec(URI_BOOK_1, URI_DC_TITLE, LITERAL_BOOK_TITLE);
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final Graph GRAPH = FACTORY.getNewGraph();
    private static final AttributeValuePairHelper AVP_HELPER = FACTORY.getNewAttributeValuePairHelper();

    public void testMe() {
    }

//    public void checkClassProperties() {
//        ClassPropertiesTestUtil.checkExtensionOf(Analysis.class, SparqlAnalyser.class);
//        ClassPropertiesTestUtil
//                .checkImplementationOfInterfaceAndFinal(SparqlAnalyser.class, SparqlAnalyserImpl.class);
//        ClassPropertiesTestUtil.checkExtensionOf(DepthFirstAdapter.class, SparqlAnalyserImpl.class);
//    }
//
//    public void testNoQueryConstant() {
//        checkNoQueryConstantStaticFinal();
//        checkNoQueryConstantImmutable();
//        checkNoQueryConstantDoesNothing();
//        checkNoQueryConstantType();
//    }
//
//    private void checkNoQueryConstantStaticFinal() {
//        FieldPropertiesTestUtil.checkFieldFinal(SparqlAnalyser.class, FIELD_NO_QUERY);
//        FieldPropertiesTestUtil.checkFieldStatic(SparqlAnalyser.class, FIELD_NO_QUERY);
//    }
//
//    // Note. getQuery() should always return SparqlAnalyser.NO_QUERY when not applied via SableCC framework.
//    public void testGetQueryAlwaysReturnsNoQueryWhenNotApplied() {
//        SparqlAnalyser analyser = createAnalyser(GRAPH);
//        checkFirstGetQueryReturnsNoQuery(analyser);
//        checkFirstGetQueryReturnsNoQuery(analyser);
//    }
//
//    public void testParsingSingleTripleReturnsCorrectQuery() {
//        SparqlAnalyser analyser = createAnalyser(GRAPH);
//        ATriple expectedTriple = createTripleNodeWithVariable();
//        ConstraintImpl<ExpressionVisitor> actual = parseATripleOnce(analyser, expectedTriple);
//        checkAnalysedTriple(expectedTriple, actual);
//    }
//
//    // FIXME TJA: Test using object as URI & blank node
//    public void testParsingMultipleTriplesReturnsCorrectQuery() {
//        SparqlAnalyser analyser = createAnalyser(GRAPH);
//        checkAnalysedTriple(analyser, createTripleNodeWithVariable());
//        checkAnalysedTriple(analyser, createTripleNodeWithVariable());
//        checkAnalysedTriple(analyser, createTripleNodeWithLiteral());
//        checkAnalysedTriple(analyser, createTripleNodeWithVariable());
//        checkAnalysedTriple(analyser, createTripleNodeWithLiteral());
//        checkAnalysedTriple(analyser, createTripleNodeWithLiteral());
//    }
//
//    // FIXME TJA: Do we want this requirement? That we get the same query object back consequutive times?
//    public void testGetQueryReturnsImmutableQueriesConsistently() {
//        SparqlAnalyser analyser = createAnalyser(GRAPH);
//        analyser.caseATriple(createTripleNodeWithLiteral());
//        Query query1 = analyser.getQuery();
//        Query query2 = analyser.getQuery();
//        assertEquals(query1, query2);
//    }
//
//    private void checkAnalysedTriple(SparqlAnalyser analyser, ATriple expectedTriple) {
//        ConstraintImpl<ExpressionVisitor> actual = parseTriple(analyser, expectedTriple);
//        checkAnalysedTriple(expectedTriple, actual);
//    }
//
//    private void checkAnalysedTriple(ATriple expectedTriple, ConstraintImpl<ExpressionVisitor> actual) {
//        checkSubject(expectedTriple, actual);
//        checkPredicate(expectedTriple, actual);
//        checkObject(actual);
//    }
//
//    private void checkSubject(ATriple expectedTriple, ConstraintImpl<ExpressionVisitor> actual) {
//        AResourceResourceTripleElement expectedSubject = (AResourceResourceTripleElement) expectedTriple.getSubject();
//        Triple triple = AVP_HELPER.createTriple(actual.getAvp());
//        URIReference actualSubject = (URIReference) triple.getSubject();
//        checkResource(expectedSubject.getResource(), actualSubject.getURI());
//    }
//
//    private void checkPredicate(ATriple expectedTriple, ConstraintImpl<ExpressionVisitor> actual) {
//        AResourceResourceTripleElement expectedPredicate =
//                (AResourceResourceTripleElement) expectedTriple.getPredicate();
//        Triple triple = AVP_HELPER.createTriple(actual.getAvp());
//        URIReference actualPredicate = (URIReference) triple.getPredicate();
//        checkResource(expectedPredicate.getResource(), actualPredicate.getURI());
//    }
//
//    private void checkObject(ConstraintImpl<ExpressionVisitor> actual) {
//        Triple triple = AVP_HELPER.createTriple(actual.getAvp());
//        checkObject(triple.getObject());
//    }
//
//    private void checkObject(ObjectNode actualObject) {
//        // FIXME TJA: Try to remove instanceof.
//        if (actualObject instanceof Literal) checkObject((Literal) actualObject);
//        else checkVariableObject(actualObject);
//    }
//
//    private void checkObject(Literal actualObject) {
//        assertEquals(EXPECTED_PARSED_LITERAL, actualObject.getLexicalForm());
//    }
//
//    private void checkVariableObject(Object actualObject) {
//        assertEquals(EXPECTED_PARSED_VARIABLE, actualObject);
//    }
//
//    private void checkResource(TResource expectedResource, URI actualUri) {
//        assertEquals(expectedResource.getText(), actualUri.toString());
//    }
//
//    private ConstraintImpl<ExpressionVisitor> parseATripleOnce(SparqlAnalyser analyser, ATriple tripleToParse) {
//        checkFirstGetQueryReturnsNoQuery(analyser);
//        return parseTriple(analyser, tripleToParse);
//    }
//
//    private ConstraintImpl<ExpressionVisitor> parseTriple(SparqlAnalyser analyser, ATriple tripleToParse) {
//        return analyseTriple(analyser, tripleToParse);
//    }
//
//    private ConstraintImpl<ExpressionVisitor> analyseTriple(SparqlAnalyser analyser, ATriple triple) {
//        Query query = analyseQuery(analyser, triple);
//        return (ConstraintImpl<ExpressionVisitor>) query.getConstraintExpression();
//    }
//
//    private Query analyseQuery(SparqlAnalyser analyser, ATriple expectedTriple) {
//        analyser.caseATriple(expectedTriple);
//        return analyser.getQuery();
//    }
//
//    private ATriple createTripleNodeWithVariable() {
//        return SableCcNodeTestUtil.createTripleNodeWithVariable(TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE);
//    }
//
//    private ATriple createTripleNodeWithLiteral() {
//        return SableCcNodeTestUtil.createTripleNodeWithLiteral(TRIPLE_SPEC_BOOK_1_DC_TITLE_LITERAL);
//    }
//
//    private void checkFirstGetQueryReturnsNoQuery(SparqlAnalyser analyser) {
//        assertEquals(SparqlAnalyser.NO_QUERY, analyser.getQuery());
//    }
//
//    private void checkNoQueryConstantDoesNothing() {
//        AssertThrows.assertThrows(UnsupportedOperationException.class, new AssertThrows.Block() {
//            public void execute() throws Throwable {
//                SparqlAnalyser.NO_QUERY.getVariables();
//            }
//        });
//        AssertThrows.assertThrows(UnsupportedOperationException.class, new AssertThrows.Block() {
//            public void execute() throws Throwable {
//                SparqlAnalyser.NO_QUERY.getConstraintExpression();
//            }
//        });
//        AssertThrows.assertThrows(UnsupportedOperationException.class, new AssertThrows.Block() {
//            public void execute() throws Throwable {
//                SparqlAnalyser.NO_QUERY.getSingleAvp();
//            }
//        });
//
//    }
//
//    private void checkNoQueryConstantImmutable() {
//        assertNotNull(SparqlAnalyser.NO_QUERY);
//        Query x = SparqlAnalyser.NO_QUERY;
//        Query y = SparqlAnalyser.NO_QUERY;
//        assertEquals(x, y);
//        assertTrue(x == y);
//    }
//
//    private void checkNoQueryConstantType() {
//        ClassPropertiesTestUtil.checkInstanceImplementsInterface(Query.class, SparqlAnalyser.NO_QUERY);
//    }
//
//    private SparqlAnalyser createAnalyser(Graph graph) {
//        TripleBuilder tripleBuilder = FACTORY.getNewTripleBuilder();
//        return new SparqlAnalyserImpl(tripleBuilder, graph);
//    }
}
