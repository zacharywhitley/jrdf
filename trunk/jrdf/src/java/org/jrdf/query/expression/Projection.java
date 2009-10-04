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

package org.jrdf.query.expression;

import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.PositionalNodeType;
import org.jrdf.urql.analysis.VariableCollector;
import org.jrdf.urql.parser.node.TIdentifier;
import org.jrdf.urql.parser.parser.ParserException;
import org.jrdf.util.EqualsUtil;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Variables in a SELECT cause.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public final class Projection implements Expression {
    private static final long serialVersionUID = -202508451953503285L;
    private static final int DUMMY_HASHCODE = 47;
    private VariableCollector varCollector;
    private LinkedHashSet<Attribute> attributes;
    private LinkedHashSet<AttributeName> declaredVariables;
    private Expression nextExpression;
    private Map<AttributeName, PositionalNodeType> allVariables;

    private Projection() {
    }

    public Projection(VariableCollector newCollector, LinkedHashSet<AttributeName> newDeclaredVariables,
        Expression newNextExpression) throws ParserException {
        checkNotNull(newCollector, newDeclaredVariables, newNextExpression);
        this.varCollector = newCollector;
        this.declaredVariables = newDeclaredVariables;
        this.nextExpression = newNextExpression;
        this.attributes = extractAttributes();
        this.allVariables = newCollector.getAttributes();
    }

    public Map<Attribute, Node> getValue() {
        return nextExpression.getValue();
    }

    public LinkedHashSet<Attribute> getAttributes() {
        return attributes;
    }

    public Map<AttributeName, PositionalNodeType> getAllVariables() {
        return allVariables;
    }

    public Expression getNextExpression() {
        return nextExpression;
    }

    public void setNextExpression(Expression expression) {
        nextExpression = expression;
    }

    public int size() {
        return nextExpression.size();
    }

    public boolean equals(Object obj) {
        if (EqualsUtil.isNull(obj)) {
            return false;
        }
        if (EqualsUtil.sameReference(this, obj)) {
            return true;
        }
        if (EqualsUtil.differentClasses(this, obj)) {
            return false;
        }
        return determineEqualityFromFields(this, (Projection) obj);
    }

    public int hashCode() {
        int hash = DUMMY_HASHCODE + attributes.hashCode();
        return hash * DUMMY_HASHCODE + nextExpression.hashCode();
    }

    /**
     * Delegates to <code>getAvp().toString()</code>.
     */
    public String toString() {
        return "SELECT { " + varCollector + " } \n" + nextExpression;
    }

    public <R> R accept(ExpressionVisitor<R> v) {
        return v.visitProjection(this);
    }

    private LinkedHashSet<Attribute> extractAttributes() throws ParserException {
        LinkedHashSet<Attribute> newAttributes = new LinkedHashSet<Attribute>();
        Map<AttributeName, PositionalNodeType> variables = varCollector.getAttributes();
        for (AttributeName variable : declaredVariables) {
            NodeType type = variables.get(variable);
            if (type == null) {
                String literal = variable.getLiteral();
                throw new ParserException(new TIdentifier(literal), "Failed to find variable " +
                    literal + " in where clause. ");
            } else {
                Attribute attribute = new AttributeImpl(variable, type);
                newAttributes.add(attribute);
            }
        }
        return newAttributes;
    }

    private boolean determineEqualityFromFields(Projection o1, Projection o2) {
        return o1.getAttributes().equals(o2.getAttributes());
    }
}