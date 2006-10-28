/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.query.execute;

import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Constraint;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.Union;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairImpl;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.operation.NadicJoin;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.operation.Restrict;
import org.jrdf.query.relation.type.NodeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * An implementation of a Query Engine that does not try to optimize or transform the query.  Simply evaluates the
 * query tree by performing restrinctions, other operations (natural process, etc) and then project.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class NaiveQueryEngineImpl extends ExpressionVisitorAdapter implements QueryEngine {
    private Relation result;
    private Project project;
    private Restrict restrict;
    private NadicJoin naturalJoin;
    private org.jrdf.query.relation.operation.Union union;
    private DyadicJoin fullOuterJoin;
    private Map<String, NodeType> allVariables;

    public NaiveQueryEngineImpl(Project project, NadicJoin naturalJoin, Restrict restrict,
            org.jrdf.query.relation.operation.Union union, DyadicJoin fullOuterJoin) {
        this.project = project;
        this.naturalJoin = naturalJoin;
        this.restrict = restrict;
        this.union = union;
        this.fullOuterJoin = fullOuterJoin;
    }

    public Relation getResult() {
        return result;
    }

    public void setResult(Relation newResult) {
        result = newResult;
    }

    public void setAllVariables(Map<String, NodeType> allVariables) {
        this.allVariables = allVariables;
    }

    @Override
    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection) {
        setAllVariables(projection.getAllVariables());
        Relation expression = getExpression(projection.getNextExpression());
        Set<Attribute> attributes = projection.getAttributes();
        result = project.include(expression, attributes);
    }

    @Override
    public <V extends ExpressionVisitor> void visitConstraint(Constraint<V> constraint) {
        SortedSet<AttributeValuePair> singleAvp = constraint.getAvp();
        result = restrict.restrict(result, replaceAttributes(singleAvp));
    }

    @Override
    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        Relation lhs = getExpression(conjunction.getLhs());
        Relation rhs = getExpression(conjunction.getRhs());
        Set<Relation> relations = new HashSet<Relation>();
        relations.add(lhs);
        relations.add(rhs);
        result = naturalJoin.join(relations);
    }

    @Override
    public <V extends ExpressionVisitor> void visitUnion(Union<V> conjunction) {
        Relation lhs = getExpression(conjunction.getLhs());
        Relation rhs = getExpression(conjunction.getRhs());
        result = union.union(lhs, rhs);
    }

    @Override
    public <V extends ExpressionVisitor> void visitOptional(Optional<V> optional) {
        // TODO (AN) This really should be nadic and just pass in the rhs
        Relation rhs = getExpression(optional.getRhs());
        Relation lhs;
        if (optional.getLhs() != null) {
            lhs = getExpression(optional.getLhs());
        } else {
            lhs = rhs;
        }
        result = fullOuterJoin.join(lhs, rhs);
    }

    @SuppressWarnings({"unchecked"})
    private <V extends ExpressionVisitor> Relation getExpression(Expression<V> expression) {
        QueryEngine queryEngine = new NaiveQueryEngineImpl(project, naturalJoin, restrict, union, fullOuterJoin);
        queryEngine.setResult(result);
        queryEngine.setAllVariables(allVariables);
        expression.accept((V) queryEngine);
        return queryEngine.getResult();
    }

    private List<AttributeValuePair> replaceAttributes(SortedSet<AttributeValuePair> singleAvp) {
        List<AttributeValuePair> newAvps = new ArrayList<AttributeValuePair>();
        for (AttributeValuePair avp : singleAvp) {
            Attribute existingAttribute = avp.getAttribute();
            Attribute newAttribute;
            if (allVariables != null) {
                newAttribute = createNewAttribute(existingAttribute);
            } else {
                newAttribute = existingAttribute;
            }

            newAvps.add(new AttributeValuePairImpl(newAttribute, avp.getValue()));
        }
        return newAvps;
    }

    private Attribute createNewAttribute(Attribute existingAttribute) {
        Attribute newAttribute;
        AttributeName existingAttributeName = existingAttribute.getAttributeName();
        String existingLiteral = existingAttributeName.getLiteral();
        NodeType newNodeType = allVariables.get(existingLiteral);
        if (newNodeType == null) {
            newNodeType = existingAttribute.getType();
        }
        newAttribute = new AttributeImpl(existingAttributeName, newNodeType);
        return newAttribute;
    }
}
