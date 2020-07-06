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

package org.jrdf.query.relation.operation.mem.logic;

import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.FalseExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicOrExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.expression.logic.TrueExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.operation.BooleanEvaluator;

import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id:$
 */
public class BooleanEvaluatorImpl extends ExpressionVisitorAdapter<Boolean> implements BooleanEvaluator<Boolean> {
    private static final ValueEvaluatorImpl VALUE_EVALUATOR = new ValueEvaluatorImpl();
    private final NodeComparator nodeComparator;
    private Tuple tuple;

    public BooleanEvaluatorImpl(NodeComparator newNodeComparator) {
        this.nodeComparator = newNodeComparator;
    }

    public void setTuple(Tuple newTuple) {
        this.tuple = newTuple;
    }

    @Override
    public Boolean visitBound(BoundOperator bound) {
        final Node value = getNextValue(bound);
        return (value != null);
    }

    @Override
    public Boolean visitLogicAnd(LogicAndExpression andExpression) {
        return andExpression.getLhs().accept(this) && andExpression.getRhs().accept(this);
    }

    @Override
    public Boolean visitLogicOr(LogicOrExpression orExpression) {
        return orExpression.getLhs().accept(this) || orExpression.getRhs().accept(this);
    }

    @Override
    public Boolean visitLogicNot(LogicNotExpression notExpression) {
        return !notExpression.getExpression().accept(this);
    }

    @Override
    public Boolean visitEqualsExpression(EqualsExpression equalsExpression) {
        Node lhsValue = VALUE_EVALUATOR.getValue(tuple, equalsExpression.getLhs());
        Node rhsValue = VALUE_EVALUATOR.getValue(tuple, equalsExpression.getRhs());
        return compareNodes(lhsValue, rhsValue) == 0;
    }

    @Override
    public Boolean visitNEqualsExpression(NEqualsExpression nEqualsExpression) {
        Node lhsValue = VALUE_EVALUATOR.getValue(tuple, nEqualsExpression.getLhs());
        Node rhsValue = VALUE_EVALUATOR.getValue(tuple, nEqualsExpression.getRhs());
        return compareNodes(lhsValue, rhsValue) != 0;
    }

    @Override
    public Boolean visitLessThanExpression(LessThanExpression lessThanExpression) {
        Node lhsValue = VALUE_EVALUATOR.getValue(tuple, lessThanExpression.getLhs());
        Node rhsValue = VALUE_EVALUATOR.getValue(tuple, lessThanExpression.getRhs());
        return compareNodes(lhsValue, rhsValue) < 0;
    }

    @Override
    public Boolean visitTrue(TrueExpression trueExp) {
        return true;
    }

    @Override
    public Boolean visitFalse(FalseExpression falseExp) {
        return false;
    }

    public boolean evaluate(Tuple newTuple, LogicExpression expression) {
        tuple = newTuple;
        return expression.accept(this);
    }

    // TODO Fixme AN Handling nulls!!  Maybe the getValue should return an EmptyNode or NullaryNode or some node rather
    // than null.
    private int compareNodes(Node lNode, Node rNode) {
        int result;
        if (lNode == null && rNode == null) {
            result = 0;
        } else if (lNode == null) {
            result = -1;
        } else if (rNode == null) {
            result = 1;
        } else {
            result = nodeComparator.compare(lNode, rNode);
        }
        return result;
    }

    private Node getNextValue(Operator operator) {
        final Map<Attribute, Node> avp = operator.getValue();
        Attribute attribute = avp.keySet().iterator().next();
        return tuple.getValue(attribute);
    }
}