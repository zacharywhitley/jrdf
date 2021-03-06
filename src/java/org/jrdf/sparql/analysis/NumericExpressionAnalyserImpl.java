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

package org.jrdf.sparql.analysis;

import org.jrdf.graph.AnyNode;
import org.jrdf.graph.Node;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PositionalNodeType;
import org.jrdf.sparql.builder.LiteralBuilder;
import org.jrdf.sparql.builder.URIReferenceBuilder;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.sparql.parser.node.ABoundBuiltincall;
import org.jrdf.sparql.parser.node.AFalseBooleanLiteral;
import org.jrdf.sparql.parser.node.AIriRefIriRefOrPrefixedName;
import org.jrdf.sparql.parser.node.ALangBuiltincall;
import org.jrdf.sparql.parser.node.APrefixedNameIriRefOrPrefixedName;
import org.jrdf.sparql.parser.node.ARdfLiteralPrimaryExpression;
import org.jrdf.sparql.parser.node.AStrBuiltincall;
import org.jrdf.sparql.parser.node.ATrueBooleanLiteral;
import org.jrdf.sparql.parser.node.AVariable;
import org.jrdf.sparql.parser.parser.ParserException;

import java.util.HashMap;
import java.util.Map;

import static org.jrdf.query.expression.logic.FalseExpression.FALSE_EXPRESSION;
import static org.jrdf.query.expression.logic.TrueExpression.TRUE_EXPRESSION;
import static org.jrdf.query.relation.constants.NullaryAttribute.NULLARY_ATTRIBUTE;

public class NumericExpressionAnalyserImpl extends DepthFirstAdapter implements NumericExpressionAnalyser {
    private ParserException exception;
    private AttributeName attributeName;
    private org.jrdf.graph.Node value;
    private LiteralBuilder literalBuilder;
    private VariableCollector collector;
    private URIReferenceBuilder uriBuilder;
    private Expression expression;

    public NumericExpressionAnalyserImpl(LiteralBuilder newLiteralBuilder, VariableCollector newCollector,
        URIReferenceBuilder newUriBuilder) {
        this.literalBuilder = newLiteralBuilder;
        this.collector = newCollector;
        this.uriBuilder = newUriBuilder;
    }

    public Expression getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    @Override
    public void caseABoundBuiltincall(ABoundBuiltincall node) {
        node.getBracketedVar().apply(this);
        try {
            this.expression = new BoundOperator(getSingleAvp());
        } catch (ParserException e) {
            this.exception = e;
        }
    }

    @Override
    public void caseALangBuiltincall(ALangBuiltincall node) {
        node.getBracketedExpression().apply(this);
        try {
            this.expression = new LangOperator(getSingleAvp());
        } catch (ParserException e) {
            this.exception = e;
        }
    }

    @Override
    public void caseAStrBuiltincall(AStrBuiltincall node) {
        try {
            node.getBracketedExpression().apply(this);
            this.expression = new StrOperator(getSingleAvp());
        } catch (ParserException e) {
            this.exception = e;
        }
    }

    @Override
    public void caseARdfLiteralPrimaryExpression(ARdfLiteralPrimaryExpression node) {
        try {
            this.value = literalBuilder.createLiteral(node);
            this.expression = new SingleValue(getSingleAvp());
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAVariable(AVariable node) {
        this.attributeName = new VariableName(node.getVariablename().getText());
        this.value = AnyNode.ANY_NODE;
        try {
            this.expression = new SingleValue(getSingleAvp());
        } catch (ParserException e) {
            this.exception = e;
        }
    }

    @Override
    public void caseAIriRefIriRefOrPrefixedName(AIriRefIriRefOrPrefixedName node) {
        try {
            this.value = uriBuilder.createURIReference(node);
            this.expression = new SingleValue(getSingleAvp());
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAPrefixedNameIriRefOrPrefixedName(APrefixedNameIriRefOrPrefixedName node) {
        try {
            this.value = uriBuilder.createURIReference(node);
            this.expression = new SingleValue(getSingleAvp());
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseATrueBooleanLiteral(ATrueBooleanLiteral node) {
        try {
            if (attributeName == null) {
                attributeName = NULLARY_ATTRIBUTE.getAttributeName();
            }
            this.value = literalBuilder.createLiteral(node);
            this.expression = TRUE_EXPRESSION;
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAFalseBooleanLiteral(AFalseBooleanLiteral node) {
        try {
            if (attributeName == null) {
                attributeName = NULLARY_ATTRIBUTE.getAttributeName();
            }
            this.value = literalBuilder.createLiteral(node);
            this.expression = FALSE_EXPRESSION;
        } catch (ParserException e) {
            exception = e;
        }
    }

    private Map<Attribute, Node> getSingleAvp() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        Map<Attribute, Node> returnValue = new HashMap<Attribute, Node>(1);
        final Map<AttributeName, PositionalNodeType> namePosMap = collector.getAttributes();
        if (attributeName == null) {
            returnValue.put(NULLARY_ATTRIBUTE, value);
        } else {
            NodeType type = namePosMap.get(attributeName);
            // TODO This may not be correct - shouldn't it be SubjectPredicateObjectNodeType or an error
            // Currently leaning towards an error.
            type = (type == null) ? new ObjectNodeType() : type;
            Attribute attribute = new AttributeImpl(attributeName, type);
            returnValue.put(attribute, value);
            collector.addConstraints(returnValue);
        }
        return returnValue;
    }
}
