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

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.mem.SortedAttributeValuePairHelper;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import org.jrdf.sparql.parser.SableCcNodeTestUtil;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.util.test.ArgumentTestUtil;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.SparqlQueryTestUtil.VARIABLE_NAME_SUBJECT;
import static org.jrdf.util.test.SparqlQueryTestUtil.VARIABLE_NAME_TITLE;
import static org.jrdf.util.test.TripleTestUtil.LITERAL_BOOK_TITLE;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_1_DC_TITLE_VARIABLE;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_2_DC_TITLE_VARIABLE;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_1;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_2;
import static org.jrdf.util.test.TripleTestUtil.URI_DC_SUBJECT;
import static org.jrdf.util.test.TripleTestUtil.URI_DC_TITLE;

import java.lang.reflect.Modifier;
import java.util.SortedSet;

/**
 * Unit test for {@link TripleBuilderImpl}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class TripleBuilderUnitTest extends TestCase {

    // FIXME TJA: Add test for subject and predicate being non-resources (literal & variable). Should fail in defined way.

    private static final VariableTripleSpec TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE =
            new VariableTripleSpec(URI_BOOK_1, URI_DC_TITLE, VARIABLE_NAME_TITLE);
    private static final VariableTripleSpec TRIPLE_SPEC_BOOK_2_DC_TITLE_VARIABLE =
            new VariableTripleSpec(URI_BOOK_2, URI_DC_TITLE, VARIABLE_NAME_TITLE);
    private static final VariableTripleSpec TRIPLE_SPEC_BOOK_1_DC_SUBJECT_VARIABLE =
            new VariableTripleSpec(URI_BOOK_1, URI_DC_SUBJECT, VARIABLE_NAME_SUBJECT);
    private static final LiteralTripleSpec TRIPLE_SPEC_BOOK_1_DC_SUBJECT_LITERAL =
            new LiteralTripleSpec(URI_BOOK_1, URI_DC_SUBJECT, LITERAL_BOOK_TITLE);
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final SortedAttributeValuePairHelper AVP_HELPER = FACTORY.getNewSortedAttributeValuePairHelper();
    private TripleBuilder tripleBuilder;
    private static final String[] METHOD_PARAM_NAMES = {"tripleNode", "graph"};
    private static final Class[] METHOD_PARAM_TYPES = {ATriple.class, Graph.class};
    private static final ParameterDefinition PARAMS = new ParameterDefinition(METHOD_PARAM_NAMES, METHOD_PARAM_TYPES);

    public void setUp() throws Exception {
        super.setUp();
        AttributeComparator newAttributeComparator = FACTORY.getNewAttributeComparator();
        SortedAttributeFactory newSortedAttributeFactory = new SortedAttributeFactoryImpl(newAttributeComparator, 1);
        tripleBuilder = new TripleBuilderImpl(FACTORY.getNewSortedAttributeValuePairHelper(),
                newSortedAttributeFactory);
    }

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(TripleBuilder.class, TripleBuilderImpl.class);
        ClassPropertiesTestUtil.checkConstructor(TripleBuilderImpl.class, Modifier.PUBLIC,
                SortedAttributeValuePairHelper.class, SortedAttributeFactory.class);
    }

    public void testBuildNullParameters() {
        ArgumentTestUtil.checkMethodNullAssertions(tripleBuilder, "build", PARAMS);
    }

    public void testBuildTripleFromParserNode() {
        checkBuiltTripleWithVariable(TRIPLE_BOOK_1_DC_TITLE_VARIABLE, TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE);
    }

    public void testBuildTripleForParserNode2() {
        checkBuiltTripleWithVariable(TRIPLE_BOOK_2_DC_TITLE_VARIABLE, TRIPLE_SPEC_BOOK_2_DC_TITLE_VARIABLE);
    }

    public void testBuildTripleForParserNode3() {
        checkBuiltTripleWithVariable(TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE, TRIPLE_SPEC_BOOK_1_DC_SUBJECT_VARIABLE);
    }

    public void testBuildTripleForParserNode4() {
        checkBuiltTripleWithLiteral(TRIPLE_BOOK_1_DC_SUBJECT_LITERAL, TRIPLE_SPEC_BOOK_1_DC_SUBJECT_LITERAL);
    }

    private void checkBuiltTripleWithVariable(Triple expectedTriple, VariableTripleSpec actualTriple) {
        ATriple actualTripleNode = SableCcNodeTestUtil.createTripleNodeWithVariable(actualTriple);
        SortedSet<AttributeValuePair> avp = AVP_HELPER.createAvp(expectedTriple, actualTriple.asAttributes());
        checkBuiltTriple(actualTripleNode, avp);
    }

    private void checkBuiltTripleWithLiteral(Triple expectedTriple, LiteralTripleSpec actualTriple) {
        ATriple actualTripleNode = SableCcNodeTestUtil.createTripleNodeWithLiteral(actualTriple);
        SortedSet<AttributeValuePair> avp = AVP_HELPER.createAvp(expectedTriple, actualTriple.asAttributes());
        checkBuiltTriple(actualTripleNode, avp);
    }

    private void checkBuiltTriple(ATriple actualTripleNode, SortedSet<AttributeValuePair> expectedAvp) {
        SortedSet<AttributeValuePair> actualAvp = tripleBuilder.build(actualTripleNode, FACTORY.getNewGraph());
        assertEquals(expectedAvp, actualAvp);
    }
}
