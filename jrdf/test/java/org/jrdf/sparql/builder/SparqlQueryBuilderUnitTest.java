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

/**
 * Unit test for {@link org.jrdf.sparql.SparqlQueryBuilder}.
 *
 * @author Tom Adams
 * @version $Id$
 */
public class SparqlQueryBuilderUnitTest extends TestCase {
    // TODO (AN) Come back and reenable
    public void testBadMan() {
    }

//    private static final SparqlParser SPARQL_PARSER = MockTestUtil.createFromInterface(SparqlParser.class);
//    private static final SparqlParser PARSER_BAD = new MockBadParser();
//    private static final SparqlParser PARSER_GOOD = new MockParser();
//    private static final String METHOD_BUILD_QUERY = "buildQuery";
//    private static final String NULL_STRING = ParameterTestUtil.NULL_STRING;
//    private static final String EMPTY_STRING = ParameterTestUtil.EMPTY_STRING;
//    private static final String SINGLE_SPACE = ParameterTestUtil.SINGLE_SPACE;
//    private static final String QUERY_GOOD = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
//
//    public void testClassProperties() {
//        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(QueryBuilder.class, SparqlQueryBuilder.class);
//        ClassPropertiesTestUtil.checkConstructor(SparqlQueryBuilder.class, Modifier.PUBLIC, SparqlParser.class);
//    }
//
//    public void testBadParams() throws Exception {
//        SparqlQueryBuilder builder = new SparqlQueryBuilder(SPARQL_PARSER);
//        checkBadParam(builder, NULL_STRING);
//        checkBadParam(builder, EMPTY_STRING);
//        checkBadParam(builder, SINGLE_SPACE);
//    }
//
//    public void testExceptionPassthroughFromParser() {
//        AssertThrows.assertThrows(InvalidQuerySyntaxException.class, new AssertThrows.Block() {
//            public void execute() throws Throwable {
//                SparqlQueryBuilder builder1 = new SparqlQueryBuilder(PARSER_BAD);
//                SparqlQueryBuilder builder = builder1;
//                builder.buildQuery(QUERY_GOOD);
//            }
//        });
//    }
//
//    public void testBuildQuery() throws InvalidQuerySyntaxException {
//        SparqlQueryBuilder builder1 = new SparqlQueryBuilder(PARSER_GOOD);
//        QueryBuilder builder = builder1;
//        builder.buildQuery(QUERY_GOOD);
//    }
//
//    private void checkBadParam(SparqlQueryBuilder builder, String param) throws Exception {
//        ParameterTestUtil.checkBadStringParam(builder, METHOD_BUILD_QUERY, param);
//    }
}
