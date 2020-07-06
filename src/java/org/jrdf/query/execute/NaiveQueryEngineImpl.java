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

package org.jrdf.query.execute;

import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.EmptyConstraint;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Union;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.constants.RelationDEE;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.operation.NadicJoin;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.operation.Restrict;
import org.jrdf.query.relation.type.PositionalNodeType;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of a Query Engine that does not try to optimize or transform the query.  Simply evaluates the
 * query tree by performing restrictions, other operations (natural process, etc) and then project.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class NaiveQueryEngineImpl extends ExpressionVisitorAdapter<EvaluatedRelation> implements QueryEngine {
    protected EvaluatedRelation result;
    protected Project project;
    protected Restrict restrict;
    protected NadicJoin naturalJoin;
    protected org.jrdf.query.relation.operation.Union union;
    protected DyadicJoin leftOuterJoin;
    protected Map<AttributeName, PositionalNodeType> allVariables;

    public NaiveQueryEngineImpl(Project newProject, NadicJoin newNaturalJoin, Restrict newRestrict,
        org.jrdf.query.relation.operation.Union newUnion, DyadicJoin newLeftOuterJoin) {
        this.project = newProject;
        this.naturalJoin = newNaturalJoin;
        this.restrict = newRestrict;
        this.union = newUnion;
        this.leftOuterJoin = newLeftOuterJoin;
    }

    public void initialiseBaseRelation(EvaluatedRelation initialRelation) {
        result = initialRelation;
    }

    public void setAllVariables(Map<AttributeName, PositionalNodeType> newAllVariables) {
        this.allVariables = newAllVariables;
    }

    @Override
    public EvaluatedRelation visitProjection(Projection projection) {
        allVariables = projection.getAllVariables();
        EvaluatedRelation expression = getExpression(projection.getNextExpression());
        LinkedHashSet<Attribute> attributes = projection.getAttributes();
        result = project.include(expression, attributes);
        return result;
    }

    @Override
    public EvaluatedRelation visitEmptyConstraint(EmptyConstraint constraint) {
        result = RelationDEE.RELATION_DEE;
        return result;
    }

    @Override
    public EvaluatedRelation visitConstraint(SingleConstraint constraint) {
        result = restrict.restrict(result, constraint.getAvo(allVariables));
        return result;
    }

    @Override
    public EvaluatedRelation visitConjunction(Conjunction conjunction) {
        EvaluatedRelation lhs = getExpression(conjunction.getLhs());
        EvaluatedRelation rhs = getExpression(conjunction.getRhs());
        Set<EvaluatedRelation> relations = new HashSet<EvaluatedRelation>();
        relations.add(lhs);
        relations.add(rhs);
        result = naturalJoin.join(relations);
        return result;
    }

    @Override
    public EvaluatedRelation visitUnion(Union conjunction) {
        EvaluatedRelation lhs = getExpression(conjunction.getLhs());
        EvaluatedRelation rhs = getExpression(conjunction.getRhs());
        result = union.union(lhs, rhs);
        return result;
    }

    @Override
    public EvaluatedRelation visitOptional(Optional optional) {
        // TODO (AN) This really should be nadic and just pass in the rhs
        EvaluatedRelation rhs = getExpression(optional.getRhs());
        EvaluatedRelation lhs;
        if (optional.getLhs() != null) {
            lhs = getExpression(optional.getLhs());
        } else {
            lhs = rhs;
        }
        result = leftOuterJoin.join(lhs, rhs);
        return result;
    }

    @Override
    public EvaluatedRelation visitFilter(Filter filter) {
        EvaluatedRelation lhsRelation = getExpression(filter.getLhs());
        result = restrict.restrict(lhsRelation, filter.getRhs());
        return result;
    }

    protected EvaluatedRelation getExpression(Expression expression) {
        QueryEngine queryEngine = new NaiveQueryEngineImpl(project, naturalJoin, restrict, union, leftOuterJoin);
        queryEngine.initialiseBaseRelation(result);
        queryEngine.setAllVariables(allVariables);
        return expression.accept(queryEngine);
    }
}
