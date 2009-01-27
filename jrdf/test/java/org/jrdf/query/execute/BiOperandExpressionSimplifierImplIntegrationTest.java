/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query.execute;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import static org.jrdf.query.execute.ExpressionComparatorImpl.EXPRESSION_COMPARATOR;
import org.jrdf.query.expression.BiOperandExpression;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Union;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id :$
 */
public class BiOperandExpressionSimplifierImplIntegrationTest extends ExpressionSimplifierImplIntegrationTest {
    private BiOperandExpressionSimplifier simplifier;
    private Map<Attribute, ValueOperation> subj;
    private Map<Attribute, ValueOperation> pred;
    private Map<Attribute, ValueOperation> obj;
    private LinkedHashMap<Attribute, ValueOperation> avp1;
    private SingleConstraint<ExpressionVisitor> constraint1;
    private Map<Attribute, ValueOperation> subj1;
    private Map<Attribute, ValueOperation> obj1;
    private LinkedHashMap<Attribute, ValueOperation> avp2;
    private SingleConstraint<ExpressionVisitor> constraint2;
    private Map<Attribute, ValueOperation> subj2;
    private Map<Attribute, ValueOperation> obj2;
    private LinkedHashMap<Attribute, ValueOperation> avp3;
    private SingleConstraint<ExpressionVisitor> constraint3;
    private Map<Attribute, ValueOperation> subj3;
    private Map<Attribute, ValueOperation> obj3;
    private LinkedHashMap<Attribute, ValueOperation> avp4;
    private SingleConstraint<ExpressionVisitor> constraint4;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        simplifier = new BiOperandExpressionSimplifierImpl(EXPRESSION_COMPARATOR);
        subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        obj = createSingleAVP(ATTR_O, ANY_OBJECT_NODE, EQUALS);
        avp1 = createSingleAVP(subj, pred, obj);
        constraint1 = new SingleConstraint<ExpressionVisitor>(avp1);
        subj1 = createSingleAVP(ATTR_S1, ANY_SUBJECT_NODE, EQUALS);
        obj1 = createSingleAVP(ATTR_O1, ANY_OBJECT_NODE, EQUALS);
        avp2 = createSingleAVP(subj1, pred, obj1);
        constraint2 = new SingleConstraint<ExpressionVisitor>(avp2);
        subj2 = createSingleAVP(ATTR_S2, ANY_SUBJECT_NODE, EQUALS);
        obj2 = createSingleAVP(ATTR_O2, ANY_OBJECT_NODE, EQUALS);
        avp3 = createSingleAVP(subj2, pred, obj2);
        constraint3 = new SingleConstraint<ExpressionVisitor>(avp3);
        subj3 = createSingleAVP(ATTR_S3, ANY_SUBJECT_NODE, EQUALS);
        obj3 = createSingleAVP(ATTR_O3, ANY_OBJECT_NODE, EQUALS);
        avp4 = createSingleAVP(subj3, pred, obj3);
        constraint4 = new SingleConstraint<ExpressionVisitor>(avp4);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSimplifierConjunction() {
        Conjunction<ExpressionVisitor> conj1 = new Conjunction<ExpressionVisitor>(constraint1, constraint2);
        Conjunction<ExpressionVisitor> conj2 = new Conjunction<ExpressionVisitor>(conj1, constraint3);
        Conjunction<ExpressionVisitor> conj3 = new Conjunction<ExpressionVisitor>(conj2, constraint4);
        List<Expression<ExpressionVisitor>> expected =
            createExpected(constraint1, constraint2, constraint3, constraint4);
        checkResult(conj3, expected);

        conj1 = new Conjunction<ExpressionVisitor>(constraint4, constraint3);
        conj2 = new Conjunction<ExpressionVisitor>(conj1, constraint2);
        conj3 = new Conjunction<ExpressionVisitor>(conj2, constraint1);
        expected = createExpected(constraint4, constraint3, constraint2, constraint1);
        checkResult(conj3, expected);

        conj1 = new Conjunction<ExpressionVisitor>(constraint4, constraint3);
        conj2 = new Conjunction<ExpressionVisitor>(constraint2, constraint1);
        conj3 = new Conjunction<ExpressionVisitor>(conj1, conj2);
        expected = createExpected(constraint4, constraint3, constraint2, constraint1);
        checkResult(conj3, expected);
    }

    public void testSimplifierUnion() {
        Union<ExpressionVisitor> union1 = new Union<ExpressionVisitor>(constraint1, constraint2);
        Union<ExpressionVisitor> union2 = new Union<ExpressionVisitor>(union1, constraint3);
        Union<ExpressionVisitor> union3 = new Union<ExpressionVisitor>(union2, constraint4);
        List<Expression<ExpressionVisitor>> expected =
            createExpected(constraint1, constraint2, constraint3, constraint4);
        checkResult(union3, expected);

        union1 = new Union<ExpressionVisitor>(constraint4, constraint3);
        union2 = new Union<ExpressionVisitor>(union1, constraint2);
        union3 = new Union<ExpressionVisitor>(union2, constraint1);
        expected = createExpected(constraint4, constraint3, constraint2, constraint1);
        checkResult(union3, expected);

        union1 = new Union<ExpressionVisitor>(constraint4, constraint3);
        union2 = new Union<ExpressionVisitor>(constraint2, constraint1);
        union3 = new Union<ExpressionVisitor>(union1, union2);
        expected = createExpected(constraint4, constraint3, constraint2, constraint1);
        checkResult(union3, expected);
    }

    private List<Expression<ExpressionVisitor>> createExpected(Expression<ExpressionVisitor>... expressions) {
        List<Expression<ExpressionVisitor>> expected = new LinkedList<Expression<ExpressionVisitor>>();
        for (Expression<ExpressionVisitor> exp : expressions) {
            expected.add(exp);
        }
        return expected;
    }

    private void checkResult(BiOperandExpression<ExpressionVisitor> expression,
                             List<Expression<ExpressionVisitor>> expected) {
        List<Expression<ExpressionVisitor>> actual =
            simplifier.flattenAndSortConjunction(expression, expression.getClass());
        assertEquals(expected, actual);
    }
}
