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

package org.jrdf.urql.builder;

import org.hamcrest.CoreMatchers;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Node;
import org.jrdf.graph.Triple;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import org.jrdf.urql.parser.node.ATriple;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivateFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.SparqlQueryTestUtil.VARIABLE_NAME_SUBJECT;
import static org.jrdf.util.test.SparqlQueryTestUtil.VARIABLE_NAME_TITLE;
import static org.jrdf.util.test.TripleTestUtil.LITERAL_BOOK_TITLE;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_1_DC_TITLE_VARIABLE;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_BOOK_2_DC_TITLE_VARIABLE;
import static org.jrdf.util.test.TripleTestUtil.TRIPLE_VARIABLE_VARIABLE_SUBJECT;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_1;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_2;
import static org.jrdf.util.test.TripleTestUtil.URI_DC_SUBJECT;
import static org.jrdf.util.test.TripleTestUtil.URI_DC_TITLE;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.Map;


public final class TripleBuilderImplUnitTest {
    // FIXME TJA: Add test for subject and predicate being non-resources (literal & variable). Should fail in defined
    // way.
    private static final TripleSpec BOOK_1_DC_TITLE_VARIABLE =
        new VariableTripleSpec(URI_BOOK_1, URI_DC_TITLE, VARIABLE_NAME_TITLE);
    private static final TripleSpec BOOK_2_DC_TITLE_VARIABLE =
        new VariableTripleSpec(URI_BOOK_2, URI_DC_TITLE, VARIABLE_NAME_TITLE);
    private static final TripleSpec BOOK_1_DC_SUBJECT_VARIABLE =
        new VariableTripleSpec(URI_BOOK_1, URI_DC_SUBJECT, VARIABLE_NAME_SUBJECT);
    private static final TripleSpec BOOK_1_DC_SUBJECT_BOOK_TITLE =
        new LiteralTripleSpec(URI_BOOK_1, URI_DC_SUBJECT, LITERAL_BOOK_TITLE);
    private static final TripleSpec VARIABLE_VARIABLE_BOOK_TITLE =
        new VariableResourceTripleSpec(VARIABLE_NAME_SUBJECT, VARIABLE_NAME_TITLE, URI_DC_SUBJECT);
    private static final Class<?>[] PARAM_TYPES = {Graph.class, SortedAttributeFactory.class};
    private static final String[] PARAMETER_NAMES = {"graph", "sortedAttributeFactory"};
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final AttributeValuePairHelper AVP_HELPER = FACTORY.getNewAttributeValuePairHelper();
    private TripleBuilder tripleBuilder;

    @Before
    public void setUp() throws Exception {
        AttributeComparator newAttributeComparator = FACTORY.getNewAttributeComparator();
        SortedAttributeFactory newSortedAttributeFactory = new SortedAttributeFactoryImpl(newAttributeComparator, 1);
        tripleBuilder = new TripleBuilderImpl(FACTORY.getNewGraph(), newSortedAttributeFactory);
    }

    @Test
    public void classProperties() {
        checkImplementationOfInterfaceAndFinal(TripleBuilder.class, TripleBuilderImpl.class);
        checkConstructor(TripleBuilderImpl.class, Modifier.PUBLIC, PARAM_TYPES);
        checkConstructNullAssertion(TripleBuilderImpl.class, PARAM_TYPES);
        checkConstructorSetsFieldsAndFieldsPrivateFinal(TripleBuilderImpl.class, PARAM_TYPES, PARAMETER_NAMES);
    }

    @Test
    public void testBuildTripleFromParserNode() throws Exception {
        checkBuiltTripleWithVariable(TRIPLE_BOOK_1_DC_TITLE_VARIABLE, BOOK_1_DC_TITLE_VARIABLE);
    }

    @Test
    public void testBuildTripleForParserNode2() throws Exception {
        checkBuiltTripleWithVariable(TRIPLE_BOOK_2_DC_TITLE_VARIABLE, BOOK_2_DC_TITLE_VARIABLE);
    }

    @Test
    public void testBuildTripleForParserNode3() throws Exception {
        checkBuiltTripleWithVariable(TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE, BOOK_1_DC_SUBJECT_VARIABLE);
    }

    @Test
    public void testBuildTripleForParserNode4() throws Exception {
        checkBuiltTripleWithLiteral(TRIPLE_BOOK_1_DC_SUBJECT_LITERAL, BOOK_1_DC_SUBJECT_BOOK_TITLE);
    }

    @Test
    public void testBuildTripleForParserNode5() throws Exception {
        checkBuiltTripleWithLiteral(TRIPLE_VARIABLE_VARIABLE_SUBJECT, VARIABLE_VARIABLE_BOOK_TITLE);
    }

    private void checkBuiltTripleWithVariable(Triple expectedTriple, TripleSpec actualTriple) throws Exception {
        Map<Attribute, Node> avp = AVP_HELPER.createLinkedAvo(expectedTriple, actualTriple.asAttributes());
        checkBuildTriple(avp, actualTriple.getTriple());
    }

    private void checkBuiltTripleWithLiteral(Triple expectedTriple, TripleSpec actualTriple) throws Exception {
        Map<Attribute, Node> avp = AVP_HELPER.createLinkedAvo(expectedTriple, actualTriple.asAttributes());
        checkBuildTriple(avp, actualTriple.getTriple());
    }

    private void checkBuildTriple(Map<Attribute, Node> expectedAvp, ATriple triple) throws Exception {
        triple.apply(tripleBuilder);
        Map<Attribute, Node> actualAvp = tripleBuilder.getTriples();
        assertThat(actualAvp, CoreMatchers.equalTo(expectedAvp));
    }
}
