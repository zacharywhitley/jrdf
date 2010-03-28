/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.sparql.parser;

import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Constraint;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;

import java.net.URI;

import static org.jrdf.graph.AnyNode.ANY_NODE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createAttValue;
import static org.jrdf.util.test.NodeTestUtil.createLiteral;

public class OperatorTestUtil {
    private Operator op;
    private Node node;

    OperatorTestUtil(Operator newOp) {
        this.op = newOp;
    }

    OperatorTestUtil(Node newNode) {
        this.node = newNode;
    }

    public static OperatorTestUtil literal(String literal) {
        return new OperatorTestUtil(createLiteral(literal));
    }

    public static OperatorTestUtil literal(String literal, URI datatype) {
        return new OperatorTestUtil(createLiteral(literal, datatype));
    }

    public static OperatorTestUtil str(final Attribute att) {
        return new OperatorTestUtil(new StrOperator(createAttValue(att, ANY_NODE)));
    }

    public static OperatorTestUtil lang(final Attribute att) {
        return new OperatorTestUtil(new LangOperator(createAttValue(att, ANY_NODE)));
    }

    public static LogicExpression bound(final Attribute att) {
        return new BoundOperator(createAttValue(att, ANY_NODE));
    }

    public LogicExpression eq(Literal literal) {
        Constraint value = new SingleValue(createAttValue(getAttribute(op), literal));
        return new EqualsExpression(op, value);
    }

    public LogicExpression lt(Literal literal) {
        Constraint value = new SingleValue(createAttValue(getAttribute(op), literal));
        return new LessThanExpression(op, value);
    }

    public LogicExpression gt(Literal literal) {
        Constraint value = new SingleValue(createAttValue(getAttribute(op), literal));
        return new LessThanExpression(value, op);
    }

    public LogicExpression neq(Literal literal) {
        Constraint value = new SingleValue(createAttValue(getAttribute(op), literal));
        return new NEqualsExpression(op, value);
    }

    public LogicExpression eq(OperatorTestUtil util) {
        if (op != null && util.op != null) {
            return new EqualsExpression(op, util.op);
        } else if (util.op != null) {
            Constraint value = new SingleValue(createAttValue(getAttribute(util.op), node));
            return new EqualsExpression(value, util.op);
        } else {
            Constraint value = new SingleValue(createAttValue(getAttribute(op), util.node));
            return new EqualsExpression(op, value);
        }
    }

    public LogicExpression neq(OperatorTestUtil util) {
        if (util.op != null) {
            Constraint value = new SingleValue(createAttValue(getAttribute(util.op), node));
            return new NEqualsExpression(value, util.op);
        } else {
            Constraint value = new SingleValue(createAttValue(getAttribute(op), util.node));
            return new NEqualsExpression(op, value);
        }
    }

    public static LogicExpression not(final LogicExpression exp) {
        return new LogicNotExpression(exp);
    }

    public static LogicExpression and(final LogicExpression lhs, final LogicExpression rhs) {
        return new LogicAndExpression(lhs, rhs);
    }

    private Attribute getAttribute(final Operator operator) {
        return operator.getValue().keySet().iterator().next();
    }
}
