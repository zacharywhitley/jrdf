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

package org.jrdf.query.execute;

import junit.framework.TestCase;
import static org.jrdf.query.execute.ExpressionComparatorImpl.EXPRESSION_COMPARATOR;
import org.jrdf.query.expression.Conjunction;
import static org.jrdf.query.expression.EmptyConstraint.EMPTY_CONSTRAINT;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Union;
import static org.jrdf.util.test.SparqlQueryTestUtil.ANY_SPO;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_1_DC_TITLE_ID_1;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_2_DC_TITLE_ID_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_3_DC_TITLE_ID_3;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NAME;
import static org.jrdf.util.test.TripleTestUtil.createConstraintExpression;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class ExpressionComparatorImplUnitTest extends TestCase {
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_CONJUNCTION
        = new Conjunction<ExpressionVisitor>(BOOK_1_DC_TITLE_ID_1, BOOK_2_DC_TITLE_ID_2);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_AND_3_CONJUNCTION
        = new Conjunction<ExpressionVisitor>(BOOK1_AND_2_CONJUNCTION, BOOK_3_DC_TITLE_ID_3);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_UNION
        = new Union<ExpressionVisitor>(BOOK_1_DC_TITLE_ID_1, BOOK_2_DC_TITLE_ID_2);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_AND_3_UNION
        = new Union<ExpressionVisitor>(BOOK1_AND_2_UNION, BOOK_3_DC_TITLE_ID_3);
    private static final Expression<ExpressionVisitor> ALL_AND_EMPTY = new Conjunction<ExpressionVisitor>(ANY_SPO,
        EMPTY_CONSTRAINT);
    private static final Expression<ExpressionVisitor> FOAF_NAME_EXP_1 = createConstraintExpression("x", FOAF_NAME,
        "name", 1);

    private static final int EQUAL = 0;
    private static final int BEFORE = -1;
    private static final int AFTER = 1;

    private final ExpressionComparator expressionComparator = EXPRESSION_COMPARATOR;

    public void testSimpleConjunctionSize() {
        final int size = BOOK1_AND_2_CONJUNCTION.size();
        assertEquals(2, size);
    }

    public void testComplexConjunctionSize() {
        final int size = BOOK1_AND_2_AND_3_CONJUNCTION.size();
        assertEquals(2, size);
    }

    public void testSimpleUnionSize() {
        final int size = BOOK1_AND_2_UNION.size();
        assertEquals(2, size);
    }

    public void testComplexUnionSize() {
        final int size = BOOK1_AND_2_AND_3_UNION.size();
        assertEquals(2, size);
    }

    public void testAnyConstraintSize() {
        final int size = ANY_SPO.size();
        assertEquals(6, size);
    }

    public void testSingleConstraintSize() {
        int size = FOAF_NAME_EXP_1.size();
        assertEquals(4, size);
        size = BOOK_1_DC_TITLE_ID_1.size();
        assertEquals(2, size);
    }

    public void testConjunction() {
        final int compare = expressionComparator.compare(BOOK1_AND_2_CONJUNCTION, BOOK1_AND_2_AND_3_CONJUNCTION);
        assertEquals(EQUAL, compare);
    }

    public void testEmptyConjunction() {
        final int compare = expressionComparator.compare(ALL_AND_EMPTY, BOOK1_AND_2_CONJUNCTION);
        assertEquals(AFTER, compare);
    }

    public void testDiffConjunction() {
        final int compare = expressionComparator.compare(BOOK1_AND_2_CONJUNCTION, ALL_AND_EMPTY);
        assertEquals(BEFORE, compare);
    }
}
