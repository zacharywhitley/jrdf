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
import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.operation.ValueEvaluator;
import org.jrdf.vocabulary.XSD;

import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id:$
 */
public class ValueEvaluatorImpl extends ExpressionVisitorAdapter<Node> implements ValueEvaluator<Node> {
    private Tuple tuple;

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }

    @Override
    public Node visitSingleValue(SingleValue singleValue) {
        Map<Attribute, Node> avo = singleValue.getValue();
        Attribute attribute = avo.keySet().iterator().next();
        Node node = avo.get(attribute);
        final Node newValue = tuple.getValue(attribute);
        if (ANY_NODE.equals(node)) {
            if (newValue != null) {
                return newValue;
            } else {
                return null;
            }
        } else {
            return node;
        }
    }

    @Override
    public Node visitStr(StrOperator str) {
        final Node value = getValue(str);
        if (value != null) {
            Node node = value;
            if (Literal.class.isAssignableFrom(node.getClass())) {
                Literal literal = (Literal) value;
                return new LiteralImpl(literal.getLexicalForm());
            }
        }
        return null;
    }

    @Override
    public Node visitLang(LangOperator lang) {
        final Node value = getValue(lang);
        if (value != null) {
            Node node = value;
            if (Literal.class.isAssignableFrom(node.getClass())) {
                Literal literal = (Literal) node;
                return new LiteralImpl(literal.getLanguage());
            }
        }
        return null;
    }

    @Override
    public Node visitBound(BoundOperator bound) {
        final Node valueOperation = getValue(bound);
        boolean contradiction = (valueOperation == null);
        return new LiteralImpl(Boolean.toString(contradiction), XSD.BOOLEAN);
    }

    private Node getValue(Operator operator) {
        final Map<Attribute, Node> avp = operator.getValue();
        Attribute attribute = avp.keySet().iterator().next();
        return tuple.getValue(attribute);
    }

    public Node getValue(Tuple tuple, Expression expression) {
        ValueEvaluator<Node> evaluator = new ValueEvaluatorImpl();
        evaluator.setTuple(tuple);
        return expression.accept(evaluator);
    }
}