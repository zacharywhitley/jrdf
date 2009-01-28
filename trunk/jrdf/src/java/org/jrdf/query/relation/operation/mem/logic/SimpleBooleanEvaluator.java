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

import static org.jrdf.graph.AnyNode.ANY_NODE;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
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
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.operation.BooleanEvaluator;
import org.jrdf.vocabulary.XSD;

import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id:$
 */
public class SimpleBooleanEvaluator extends ExpressionVisitorAdapter implements BooleanEvaluator {
    private final NodeComparator nodeComparator;
    private boolean contradiction;
    private Tuple tuple;
    private Node value;

    public SimpleBooleanEvaluator(NodeComparator nodeComparator) {
        this.nodeComparator = nodeComparator;
    }

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }

    public Node getValue() {
        return value;
    }

    @Override
    public void visitSingleValue(SingleValue singleValue, ExpressionVisitor v) {
        Map<Attribute, ValueOperation> avo = singleValue.getAVO();
        Attribute attribute = avo.keySet().iterator().next();
        Node node = avo.get(attribute).getValue();
        final ValueOperation valueOperation = tuple.getValueOperation(attribute);
        if (ANY_NODE.equals(node)) {
            if (valueOperation != null) {
                value = valueOperation.getValue();
            } else {
                value = null;
            }
        } else {
            value = node;
        }
    }

    @Override
    public void visitStr(StrOperator str, ExpressionVisitor v) {
        final ValueOperation valueOperation = getValueOperation(str);
        if (valueOperation != null) {
            Node node = valueOperation.getValue();
            if (Literal.class.isAssignableFrom(node.getClass())) {
                Literal literal = (Literal) valueOperation.getValue();
                value = new LiteralImpl(literal.getLexicalForm());
            }
        }
    }

    @Override
    public void visitLang(LangOperator lang, ExpressionVisitor v) {
        final ValueOperation valueOperation = getValueOperation(lang);
        if (valueOperation != null) {
            Node node = valueOperation.getValue();
            if (Literal.class.isAssignableFrom(node.getClass())) {
                Literal literal = (Literal) node;
                value = new LiteralImpl(literal.getLanguage());
            }
        }
    }

    @Override
    public void visitBound(BoundOperator bound, ExpressionVisitor v) {
        final ValueOperation valueOperation = getValueOperation(bound);
        contradiction = (valueOperation == null);
        value = new LiteralImpl(Boolean.toString(contradiction), XSD.BOOLEAN);
    }

    @Override
    public void visitLogicAnd(LogicAndExpression andExpression, ExpressionVisitor v) {
        andExpression.getLhs().accept(this);
        boolean lhsBoolean = contradiction;
        andExpression.getRhs().accept(this);
        contradiction = lhsBoolean || contradiction;
    }

    @Override
    public void visitLogicOr(LogicOrExpression orExpression, ExpressionVisitor v) {
        orExpression.getLhs().accept(this);
        boolean lhsBoolean = contradiction;
        orExpression.getRhs().accept(this);
        contradiction = lhsBoolean && contradiction;
    }

    @Override
    public void visitLogicNot(LogicNotExpression notExpression, ExpressionVisitor v) {
        notExpression.getExpression().accept(this);
        contradiction = !contradiction;
    }

    @Override
    public void visitEqualsExpression(EqualsExpression equalsExpression, ExpressionVisitor v) {
        Node lhsValue = getValue(tuple, equalsExpression.getLhs());
        Node rhsValue = getValue(tuple, equalsExpression.getRhs());
        contradiction = compareNodes(lhsValue, rhsValue) != 0;
    }

    @Override
    public void visitNEqualsExpression(NEqualsExpression nEqualsExpression, ExpressionVisitor v) {
        Node lhsValue = getValue(tuple, nEqualsExpression.getLhs());
        Node rhsValue = getValue(tuple, nEqualsExpression.getRhs());
        contradiction = compareNodes(lhsValue, rhsValue) == 0;
    }

    @Override
    public void visitLessThanExpression(LessThanExpression lessThanExpression, ExpressionVisitor v) {
        Node lhsValue = getValue(tuple, lessThanExpression.getLhs());
        Node rhsValue = getValue(tuple, lessThanExpression.getRhs());
        contradiction = compareNodes(lhsValue, rhsValue) >= 0;
    }

    @Override
    public void visitTrue(TrueExpression trueExp, ExpressionVisitor v) {
        contradiction = false;
    }

    @Override
    public void visitFalse(FalseExpression falseExp, ExpressionVisitor v) {
        contradiction = true;
    }

    private ValueOperation getValueOperation(Operator str) {
        final Map<Attribute, ValueOperation> avp = str.getAVO();
        Attribute attribute = avp.keySet().iterator().next();
        return tuple.getValueOperation(attribute);
    }

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

    public Node getValue(Tuple tuple, Expression expression) {
        BooleanEvaluator evaluator = new SimpleBooleanEvaluator(nodeComparator);
        evaluator.setTuple(tuple);
        expression.accept(evaluator);
        return evaluator.getValue();
    }

    public boolean evaluate(Tuple tuple, LogicExpression expression) {
        setTuple(tuple);
        expression.accept(this);
        return !contradiction;
    }
}