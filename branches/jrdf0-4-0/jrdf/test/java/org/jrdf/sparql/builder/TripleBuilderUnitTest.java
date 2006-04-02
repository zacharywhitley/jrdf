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
import org.jrdf.graph.Triple;
import org.jrdf.sparql.parser.SableCcNodeTestUtil;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.util.test.TripleTestUtil;

import java.lang.reflect.Modifier;
import java.net.URI;

/**
 * Unit test for {@link TripleBuilder}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class TripleBuilderUnitTest extends TestCase {

    // FIXME TJA: Add test for subject and predicate being non-resources (literal & variable). Should fail in defined way.

    private static final URI URI_DC_TITLE = TripleTestUtil.URI_DC_TITLE;
    private static final URI URI_DC_SUBJECT = TripleTestUtil.URI_DC_SUBJECT;
    private static final Triple TRIPLE_BOOK_1_DC_TITLE_VARIABLE = TripleTestUtil.TRIPLE_BOOK_1_DC_TITLE_VARIABLE;
    private static final Triple TRIPLE_BOOK_2_DC_TITLE_VARIABLE = TripleTestUtil.TRIPLE_BOOK_2_DC_TITLE_VARIABLE;
    private static final Triple TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE = TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE;
    private static final Triple TRIPLE_BOOK_1_DC_SUBJECT_LITERAL = TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL;
    private static final String VARIABLE_NAME_TITLE = SparqlQueryTestUtil.VARIABLE_NAME_TITLE;
    private static final String VARIABLE_NAME_SUBJECT = SparqlQueryTestUtil.VARIABLE_NAME_SUBJECT;
    private static final String LITERAL_BOOK_TITLE = TripleTestUtil.LITERAL_BOOK_TITLE;
    private static final URI URI_BOOK_1 = TripleTestUtil.URI_BOOK_1;
    private static final URI URI_BOOK_2 = TripleTestUtil.URI_BOOK_2;
    private static final VariableTripleSpec TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE =
            new VariableTripleSpec(URI_BOOK_1, URI_DC_TITLE, VARIABLE_NAME_TITLE);
    private static final VariableTripleSpec TRIPLE_SPEC_BOOK_2_DC_TITLE_VARIABLE =
            new VariableTripleSpec(URI_BOOK_2, URI_DC_TITLE, VARIABLE_NAME_TITLE);
    private static final VariableTripleSpec TRIPLE_SPEC_BOOK_1_DC_SUBJECT_VARIABLE =
            new VariableTripleSpec(URI_BOOK_1, URI_DC_SUBJECT, VARIABLE_NAME_SUBJECT);
    private static final LiteralTripleSpec TRIPLE_SPEC_BOOK_1_DC_SUBJECT_LITERAL =
            new LiteralTripleSpec(URI_BOOK_1, URI_DC_SUBJECT, LITERAL_BOOK_TITLE);
    private static final TripleBuilder BUILDER = new TripleBuilder();

    public void testClassProperties() {
        // FIXME TJA: Reenable this if we figure out how to do it generically.
        //ClassPropertiesTestUtil.checkExtensionOf(LocalObjectBuilder.class, TripleBuilder.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(TripleBuilder.class, TripleBuilder.class);
        ClassPropertiesTestUtil.checkConstructor(TripleBuilder.class, Modifier.PUBLIC, NO_ARG_CONSTRUCTOR);
    }

    public void testBuildTripleFromParserNode() {
        Triple variable = TRIPLE_BOOK_1_DC_TITLE_VARIABLE;
        checkBuiltTripleWithVariable(variable, TRIPLE_SPEC_BOOK_1_DC_TITLE_VARIABLE);
        checkBuiltTripleWithVariable(TRIPLE_BOOK_2_DC_TITLE_VARIABLE, TRIPLE_SPEC_BOOK_2_DC_TITLE_VARIABLE);
        checkBuiltTripleWithVariable(TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE, TRIPLE_SPEC_BOOK_1_DC_SUBJECT_VARIABLE);
        checkBuiltTripleWithLiteral(TRIPLE_BOOK_1_DC_SUBJECT_LITERAL, TRIPLE_SPEC_BOOK_1_DC_SUBJECT_LITERAL);
    }

    public void testNullThrowsException() {
        try {
            BUILDER.build(null);
            fail("build(null) should have thrown IllegalArgumentException");
        } catch (Exception expected) {
        }
    }

    private void checkBuiltTripleWithVariable(Triple expectedTriple, VariableTripleSpec actualTriple) {
        ATriple actualTripleNode = SableCcNodeTestUtil.createTripleNodeWithVariable(actualTriple);
        checkBuiltTriple(expectedTriple, actualTripleNode);
    }

    private void checkBuiltTripleWithLiteral(Triple expectedTriple, LiteralTripleSpec actualTriple) {
        ATriple actualTripleNode = SableCcNodeTestUtil.createTripleNodeWithLiteral(actualTriple);
        checkBuiltTriple(expectedTriple, actualTripleNode);
    }

    private void checkBuiltTriple(Triple expectedTriple, ATriple actualTripleNode) {
        Triple actualBuiltTriple = BUILDER.build(actualTripleNode);
        assertEquals(expectedTriple, actualBuiltTriple);
    }
}
