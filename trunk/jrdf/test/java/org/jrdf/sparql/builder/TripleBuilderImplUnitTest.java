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
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import org.jrdf.query.relation.mem.SortedAttributeValuePairHelper;
import org.jrdf.sparql.parser.node.AEscapedStrand;
import org.jrdf.sparql.parser.node.ALiteral;
import org.jrdf.sparql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.sparql.parser.node.AResourceResourceTripleElement;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.AVariable;
import org.jrdf.sparql.parser.node.AVariableObjectTripleElement;
import org.jrdf.sparql.parser.node.PObjectTripleElement;
import org.jrdf.sparql.parser.node.PResourceTripleElement;
import org.jrdf.sparql.parser.node.PStrand;
import org.jrdf.sparql.parser.node.TEscapedtext;
import org.jrdf.sparql.parser.node.TIdentifier;
import org.jrdf.sparql.parser.node.TQuote;
import org.jrdf.sparql.parser.node.TResource;
import org.jrdf.sparql.parser.node.TVariableprefix;
import org.jrdf.util.test.ClassPropertiesTestUtil;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public final class TripleBuilderImplUnitTest extends TestCase {

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

    public void setUp() throws Exception {
        super.setUp();
        AttributeComparator newAttributeComparator = FACTORY.getNewAttributeComparator();
        SortedAttributeFactory newSortedAttributeFactory = new SortedAttributeFactoryImpl(newAttributeComparator, 1);
        tripleBuilder = new TripleBuilderImpl(FACTORY.getNewGraph(), FACTORY.getNewSortedAttributeValuePairHelper(),
                newSortedAttributeFactory);
    }

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(TripleBuilder.class, TripleBuilderImpl.class);
        ClassPropertiesTestUtil.checkConstructor(TripleBuilderImpl.class, Modifier.PUBLIC, Graph.class,
                SortedAttributeValuePairHelper.class, SortedAttributeFactory.class);
    }

    public void testBuildTripleFromParserNode() {
        ATriple triple = createTripleWithVariable(URI_BOOK_1, URI_DC_TITLE, VARIABLE_NAME_TITLE);
        checkBuiltTripleWithVariable(TRIPLE_BOOK_1_DC_TITLE_VARIABLE, TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE, triple);
    }


    public void testBuildTripleForParserNode2() {
        ATriple triple = createTripleWithVariable(URI_BOOK_2, URI_DC_TITLE, VARIABLE_NAME_TITLE);
        checkBuiltTripleWithVariable(TRIPLE_BOOK_2_DC_TITLE_VARIABLE, TRIPLE_SPEC_BOOK_2_DC_TITLE_VARIABLE, triple);
    }

    public void testBuildTripleForParserNode3() {
        ATriple triple = createTripleWithVariable(URI_BOOK_1, URI_DC_SUBJECT, VARIABLE_NAME_SUBJECT);
        checkBuiltTripleWithVariable(TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE, TRIPLE_SPEC_BOOK_1_DC_SUBJECT_VARIABLE, triple);
    }

    public void testBuildTripleForParserNode4() {
        ATriple triple = createTripleWithLiteral(URI_BOOK_1, URI_DC_SUBJECT, LITERAL_BOOK_TITLE);
        checkBuiltTripleWithLiteral(TRIPLE_BOOK_1_DC_SUBJECT_LITERAL, TRIPLE_SPEC_BOOK_1_DC_SUBJECT_LITERAL, triple);
    }

    private void checkBuiltTripleWithVariable(Triple expectedTriple, VariableTripleSpec actualTriple, ATriple triple) {
        SortedSet<AttributeValuePair> avp = AVP_HELPER.createAvp(expectedTriple, actualTriple.asAttributes());
        checkBuildTriple(avp, triple);
    }

    private void checkBuiltTripleWithLiteral(Triple expectedTriple, LiteralTripleSpec actualTriple, ATriple triple) {
        SortedSet<AttributeValuePair> avp = AVP_HELPER.createAvp(expectedTriple, actualTriple.asAttributes());
        checkBuildTriple(avp, triple);
    }

    private void checkBuildTriple(SortedSet<AttributeValuePair> expectedAvp, ATriple triple) {
        triple.apply(tripleBuilder);
        SortedSet<AttributeValuePair> actualAvp = tripleBuilder.getTriples();
        assertEquals(expectedAvp, actualAvp);
    }

    private ATriple createTripleWithVariable(URI subject, URI predicate, String object) {
        PResourceTripleElement subjectElement = createResourceTripleElement(subject);
        PResourceTripleElement predicateElement = createResourceTripleElement(predicate);
        PObjectTripleElement objectElement = createVariableTripleElement(object);
        ATriple triple = new ATriple(subjectElement, predicateElement, objectElement);
        return triple;
    }

    private ATriple createTripleWithLiteral(URI subject, URI predicate, String object) {
        PResourceTripleElement subjectElement = createResourceTripleElement(subject);
        PResourceTripleElement predicateElement = createResourceTripleElement(predicate);
        PObjectTripleElement objectElement = createLiteralTripleElement(object);
        ATriple triple = new ATriple(subjectElement, predicateElement, objectElement);
        return triple;
    }

    private PObjectTripleElement createLiteralTripleElement(String object) {
        List<PStrand> strand = new ArrayList<PStrand>();
        strand.add(new AEscapedStrand(new TEscapedtext(object)));
        ALiteral literal = new ALiteral(new TQuote("'"), strand, new TQuote("'"));
        return new ALiteralObjectTripleElement(literal);
    }

    private AResourceResourceTripleElement createResourceTripleElement(URI uri) {
        return new AResourceResourceTripleElement(new TResource(uri.toString()));
    }

    private AVariableObjectTripleElement createVariableTripleElement(String variableNameTitle) {
        TVariableprefix variableprefix = new TVariableprefix("?");
        TIdentifier identifier = new TIdentifier(variableNameTitle);
        return new AVariableObjectTripleElement(new AVariable(variableprefix, identifier));
    }
}
