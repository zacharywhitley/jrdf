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
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.QueryBuilder;
import org.jrdf.util.param.ParameterTestUtil;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.sparql.SparqlQueryBuilder;

/**
 * Unit test for {@link org.jrdf.sparql.SparqlQueryBuilder}.
 * @author Tom Adams
 * @version $Revision$
 */
public class SparqlQueryBuilderUnitTest extends TestCase {
    private static final String BUILD_QUERY_METHOD = "buildQuery";
    private static final String NULL_STRING = ParameterTestUtil.NULL_STRING;
    private static final String EMPTY_STRING = ParameterTestUtil.EMPTY_STRING;
    private static final String SINGLE_SPACE = ParameterTestUtil.SINGLE_SPACE;

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(QueryBuilder.class, SparqlQueryBuilder.class);
    }

    public void testBadParams() throws Exception {
        SparqlQueryBuilder builder = new SparqlQueryBuilder();
        checkBadParam(builder, NULL_STRING);
        checkBadParam(builder, EMPTY_STRING);
        checkBadParam(builder, SINGLE_SPACE);
    }

    public void testBuildGoodQuery() throws InvalidQuerySyntaxException {
        // TODO: Is there any more testing we can do on the form of the query?
//    checkGoodQuery("select $s $p $o from <rmi://localhost/server1#> where $s $p $o ;");
    }

    public void testBuildUnsupportedUpdateQuery() throws InvalidQuerySyntaxException {
//    checkUnsupportedQuery(" backup <rmi://localhost/server1> to <file:/tmp/bar.gz>;");
    }

    public void testBuildBadQuery() throws InvalidQuerySyntaxException {
//    checkBadQuery("select $s $p $o from <rmi://localhost/server1#> where $s $p $o ");
    }

//  private void checkGoodQuery(String query) throws InvalidQuerySyntaxException {
//    assertNotNull(new SparqlQueryBuilder().buildQuery(query));
//  }

//  private void checkBadQuery(String badQuery) {
//    try {
//      new SparqlQueryBuilder().buildQuery(badQuery);
//    } catch (InvalidQuerySyntaxException expected) {
//    } catch (IllegalArgumentException expected) {  }
//  }

//  private void checkUnsupportedQuery(String unsupportedQuery) throws InvalidQuerySyntaxException {
//    try {
//      new SparqlQueryBuilder().buildQuery(unsupportedQuery);
//    } catch (IllegalArgumentException expected) { }
//  }

    private void checkBadParam(SparqlQueryBuilder builder, String param) throws Exception {
        ParameterTestUtil.checkBadStringParam(builder, BUILD_QUERY_METHOD, param);
    }
}
