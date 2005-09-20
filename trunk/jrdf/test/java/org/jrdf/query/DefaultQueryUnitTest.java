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

package org.jrdf.query;

import java.io.Serializable;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.net.URI;

import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.sparql.SparqlQueryTestUtil;
import org.jrdf.graph.Graph;

/**
 * Unit test for {@link DefaultQuery}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultQueryUnitTest extends TestCase {

    static final List<Variable> ONE_VARIABLE = Arrays.asList(new Variable[]{new DefaultVariable("x")});
    private static final List<? extends Variable> ALL_VARIABLES = Variable.ALL_VARIABLES;
    private static final List<Variable> NULL_VARIABLES = null;
    private static final ConstraintExpression NULL_EXPRESSION = null;
    private static final ConstraintExpression CONSTRAINT_EXPRESSION = SparqlQueryTestUtil.CONSTRAINT_BOOK_1_DC_TITLE;

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Query.class, DefaultQuery.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Serializable.class, DefaultQuery.class);
        ClassPropertiesTestUtil.checkConstructor(DefaultQuery.class, Modifier.PUBLIC, List.class,
                ConstraintExpression.class);
    }

    public void testSerialVersionUid() {
        assertEquals(409607492370028929L, DefaultQuery.serialVersionUID);
    }

    public void testNullsInConstructorThrowException() {
        try {
            new DefaultQuery(NULL_VARIABLES, CONSTRAINT_EXPRESSION);
            fail("null variables should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) { }
        try {
            new DefaultQuery(ONE_VARIABLE, NULL_EXPRESSION);
            fail("null expression should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) { }
    }

    public void testGetProjectedVariables() {
        checkGetProjectedVariables(ALL_VARIABLES);
        checkGetProjectedVariables(ONE_VARIABLE);
    }

    public void testGetConstraintExpression() {
        Query query = new DefaultQuery(ALL_VARIABLES, CONSTRAINT_EXPRESSION);
        ConstraintExpression actualExpression = query.getConstraintExpression();
        assertEquals(CONSTRAINT_EXPRESSION, actualExpression);
    }

    private void checkGetProjectedVariables(List<? extends Variable> expected) {
        Query query = new DefaultQuery(expected, CONSTRAINT_EXPRESSION);
        assertEquals(expected, query.getProjectedVariables());
    }
}
