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

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import static org.jrdf.graph.AnyNode.ANY_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.query.Query;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.expression.Union;
import static org.jrdf.query.expression.logic.FalseExpression.FALSE_EXPRESSION;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.AttributeImpl;
import static org.jrdf.query.relation.mem.BoundAVPOperation.BOUND;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import static org.jrdf.query.relation.mem.StrAVPOperation.STR;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.urql.builder.QueryBuilder;
import static org.jrdf.util.test.NodeTestUtil.createLiteral;
import org.jrdf.util.test.ReflectTestUtil;
import static org.jrdf.util.test.TripleTestUtil.createConstraintExpression;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class ExpressionSimplifierImplIntegrationTest extends TestCase {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final Graph GRAPH = FACTORY.getNewGraph();
    private QueryBuilder queryBuilder;

    protected static final SubjectNodeType SUBJECT_NODE_TYPE = new SubjectNodeType();
    protected static final PredicateNodeType PREDICATE_NODE_TYPE = new PredicateNodeType();
    protected static final ObjectNodeType OBJECT_NODE_TYPE = new ObjectNodeType();
    protected static final VariableName ATTRIBUTE_S = new VariableName("s");
    protected static final VariableName ATTRIBUTE_S1 = new VariableName("s1");
    protected static final VariableName ATTRIBUTE_S2 = new VariableName("s2");
    protected static final VariableName ATTRIBUTE_S3 = new VariableName("s3");
    protected static final VariableName ATTRIBUTE_P = new VariableName("p");
    protected static final VariableName ATTRIBUTE_O = new VariableName("o");
    protected static final VariableName ATTRIBUTE_O1 = new VariableName("o1");
    protected static final VariableName ATTRIBUTE_O2 = new VariableName("o2");
    protected static final VariableName ATTRIBUTE_O3 = new VariableName("o3");

    protected static final Attribute ATTR_S = new AttributeImpl(ATTRIBUTE_S, SUBJECT_NODE_TYPE);
    protected static final Attribute ATTR_S1 = new AttributeImpl(ATTRIBUTE_S1, SUBJECT_NODE_TYPE);
    protected static final Attribute ATTR_S2 = new AttributeImpl(ATTRIBUTE_S2, SUBJECT_NODE_TYPE);
    protected static final Attribute ATTR_S3 = new AttributeImpl(ATTRIBUTE_S3, SUBJECT_NODE_TYPE);
    protected static final Attribute ATTR_P = new AttributeImpl(ATTRIBUTE_P, PREDICATE_NODE_TYPE);
    protected static final Attribute ATTR_O = new AttributeImpl(ATTRIBUTE_O, OBJECT_NODE_TYPE);
    protected static final Attribute ATTR_O1 = new AttributeImpl(ATTRIBUTE_O1, OBJECT_NODE_TYPE);
    protected static final Attribute ATTR_O2 = new AttributeImpl(ATTRIBUTE_O2, OBJECT_NODE_TYPE);
    protected static final Attribute ATTR_O3 = new AttributeImpl(ATTRIBUTE_O3, OBJECT_NODE_TYPE);

    private ExpressionSimplifier simplifier;
    private static final Expression<ExpressionVisitor> SPO_CONSTRAINT = createConstraintExpression("s", "p", "o");
    private static final Expression<ExpressionVisitor> SPO1_CONSTRAINT = createConstraintExpression("s", "p", "o1");
    private static final Expression<ExpressionVisitor> S1PO1_CONSTRAINT = createConstraintExpression("s1", "p", "o1");
    private static final Expression<ExpressionVisitor> S1PO_CONSTRAINT = createConstraintExpression("s1", "p", "o");
    private static final Expression<ExpressionVisitor> S2PO2_CONSTRAINT = createConstraintExpression("s2", "p", "o2");
    private static final Expression<ExpressionVisitor> S3PO3_CONSTRAINT = createConstraintExpression("s3", "p", "o3");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FACTORY.refresh();
        queryBuilder = FACTORY.getNewQueryBuilder();
        simplifier = new ExpressionSimplifierImpl();
    }

    public void testSimpleQuery() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p 'hello'@en }";
        Literal literal = createLiteral("hello", "en");
        Expression<ExpressionVisitor> constraint = createConstraintExpression("s", "p", literal, 1);
        getExpression(queryText, constraint);
        queryText = "ASK WHERE { ?s ?p 'hello'@en }";
        constraint = createConstraintExpression("s", "p", literal, 2);
        getExpression(queryText, constraint);
    }

    public void testSimpleFilter() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o FILTER (str(?o) = \"abc\") }";
        Literal literal = createLiteral("abc");
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj = createSingleAVP(ATTR_O, literal, STR);
        LinkedHashMap<Attribute, ValueOperation> map = createSingleAVP(subj, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint = new SingleConstraint<ExpressionVisitor>(map);
        getExpression(queryText, constraint);
        queryText = "ASK WHERE { ?s ?p ?o FILTER (str(?o) = \"abc\") }";
        getExpression(queryText, constraint);
    }

    public void testSimpleEqualFilter1() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1) }";
        Conjunction<ExpressionVisitor> conj = new Conjunction<ExpressionVisitor>(SPO_CONSTRAINT, S1PO_CONSTRAINT);
        getExpression(queryText, conj);
    }

    public void testSimpleEqualFilter2() throws Exception {
        String queryText = "ASK WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1) }";
        Conjunction<ExpressionVisitor> conj = new Conjunction<ExpressionVisitor>(SPO1_CONSTRAINT, S1PO1_CONSTRAINT);
        getExpression(queryText, conj);
    }

    public void testComplexFilter() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) = \"abc\") }";
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        Literal literal = createLiteral("abc");
        Map<Attribute, ValueOperation> obj = createSingleAVP(ATTR_O, literal, STR);
        final LinkedHashMap<Attribute, ValueOperation> avp1 = createSingleAVP(subj, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint1 = new SingleConstraint<ExpressionVisitor>(avp1);
        Attribute attrS1 = new AttributeImpl(ATTRIBUTE_S1, SUBJECT_NODE_TYPE);
        Map<Attribute, ValueOperation> subj1 = createSingleAVP(attrS1, ANY_SUBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp2 = createSingleAVP(subj1, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint2 = new SingleConstraint<ExpressionVisitor>(avp2);
        Conjunction<ExpressionVisitor> conj = new Conjunction<ExpressionVisitor>(constraint1, constraint2);
        getExpression(queryText, conj);
        queryText = "ASK WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) = \"abc\") }";
        getExpression(queryText, conj);
    }

    public void testComplexFilter1() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) != \"abc\") }";
        Literal literal = createLiteral("abc");
        Map<Attribute, ValueOperation> filterLhs = createSingleAVP(ATTR_O, ANY_NODE, STR);
        Expression<ExpressionVisitor> strOp = new StrOperator<ExpressionVisitor>(filterLhs);
        Map<Attribute, ValueOperation> filterRhs = createSingleAVP(ATTR_O, literal, EQUALS);
        Expression<ExpressionVisitor> singleValue = new SingleValue<ExpressionVisitor>(filterRhs);
        NEqualsExpression<ExpressionVisitor> neq = new NEqualsExpression<ExpressionVisitor>(strOp, singleValue);
        Conjunction<ExpressionVisitor> conj = new Conjunction<ExpressionVisitor>(SPO_CONSTRAINT, S1PO_CONSTRAINT);
        Filter<ExpressionVisitor> filter = new Filter<ExpressionVisitor>(conj, neq);
        getExpression(queryText, filter);
    }

    public void testComplexFilter2() throws Exception {
        String queryText = "ASK WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) != \"abc\") }";
        Literal literal = createLiteral("abc");
        Map<Attribute, ValueOperation> filterLhs = createSingleAVP(ATTR_O1, ANY_NODE, STR);
        Expression<ExpressionVisitor> strOp = new StrOperator<ExpressionVisitor>(filterLhs);
        Map<Attribute, ValueOperation> filterRhs = createSingleAVP(ATTR_O1, literal, EQUALS);
        Expression<ExpressionVisitor> singleValue = new SingleValue<ExpressionVisitor>(filterRhs);
        NEqualsExpression<ExpressionVisitor> neq = new NEqualsExpression<ExpressionVisitor>(strOp, singleValue);
        Conjunction<ExpressionVisitor> conj = new Conjunction<ExpressionVisitor>(SPO1_CONSTRAINT, S1PO1_CONSTRAINT);
        Filter<ExpressionVisitor> filter = new Filter<ExpressionVisitor>(conj, neq);
        getExpression(queryText, filter);
    }

    public void testRightUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . { { ?s1 ?p ?o1 } UNION { ?s2 ?p ?o2 } } }";
        Conjunction<ExpressionVisitor> conj1 = new Conjunction<ExpressionVisitor>(SPO_CONSTRAINT, S1PO1_CONSTRAINT);
        Conjunction<ExpressionVisitor> conj2 = new Conjunction<ExpressionVisitor>(SPO_CONSTRAINT, S2PO2_CONSTRAINT);
        Union<ExpressionVisitor> union = new Union<ExpressionVisitor>(conj1, conj2);
        getExpression(queryText, union);
    }

    public void testLeftUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { { { ?s1 ?p ?o1 } UNION { ?s2 ?p ?o2 } } . ?s ?p ?o }";
        Conjunction<ExpressionVisitor> conj1 = new Conjunction<ExpressionVisitor>(SPO_CONSTRAINT, S1PO1_CONSTRAINT);
        Conjunction<ExpressionVisitor> conj2 = new Conjunction<ExpressionVisitor>(SPO_CONSTRAINT, S2PO2_CONSTRAINT);
        Union<ExpressionVisitor> union = new Union<ExpressionVisitor>(conj1, conj2);
        getExpression(queryText, union);
    }

    public void testTwoUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { { { ?s ?p ?o } UNION { ?s1 ?p ?o1 } } . " +
            "{ { ?s2 ?p ?o2 } UNION { ?s3 ?p ?o3 } } }";
        Conjunction<ExpressionVisitor> conj1 = new Conjunction<ExpressionVisitor>(SPO_CONSTRAINT, S2PO2_CONSTRAINT);
        Conjunction<ExpressionVisitor> conj2 = new Conjunction<ExpressionVisitor>(SPO_CONSTRAINT, S3PO3_CONSTRAINT);
        Conjunction<ExpressionVisitor> conj3 = new Conjunction<ExpressionVisitor>(S1PO1_CONSTRAINT, S2PO2_CONSTRAINT);
        Conjunction<ExpressionVisitor> conj4 = new Conjunction<ExpressionVisitor>(S1PO1_CONSTRAINT, S3PO3_CONSTRAINT);
        Union<ExpressionVisitor> union1 = new Union<ExpressionVisitor>(conj1, conj2);
        Union<ExpressionVisitor> union2 = new Union<ExpressionVisitor>(conj3, conj4);
        Union<ExpressionVisitor> union = new Union<ExpressionVisitor>(union1, union2);
        getExpression(queryText, union);
    }

    public void testBooleanEquals() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o FILTER (bound(?o) = true) }";
        Map<Attribute, ValueOperation> obj = createSingleAVP(ATTR_O, ANY_NODE, BOUND);
        LogicExpression<ExpressionVisitor> boundExp = new BoundOperator<ExpressionVisitor>(obj);
        Filter<ExpressionVisitor> filter = new Filter<ExpressionVisitor>(SPO_CONSTRAINT, boundExp);
        getExpression(queryText, filter);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER (false = true) }";
        filter = new Filter<ExpressionVisitor>(SPO_CONSTRAINT, FALSE_EXPRESSION);
        getExpression(queryText, filter);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER (true = false) }";
        filter = new Filter<ExpressionVisitor>(SPO_CONSTRAINT, FALSE_EXPRESSION);
        getExpression(queryText, filter);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER (bound(?o) = false) }";
        LogicExpression<ExpressionVisitor> notExp = new LogicNotExpression<ExpressionVisitor>(boundExp);
        filter = new Filter<ExpressionVisitor>(SPO_CONSTRAINT, notExp);
        getExpression(queryText, filter);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER (false = bound(?o)) }";
        filter = new Filter<ExpressionVisitor>(SPO_CONSTRAINT, notExp);
        getExpression(queryText, filter);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER ((bound(?o) && bound(?s)) = true) }";
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_NODE, BOUND);
        LogicExpression<ExpressionVisitor> boundExp1 = new BoundOperator<ExpressionVisitor>(subj);
        LogicExpression<ExpressionVisitor> andExp = new LogicAndExpression<ExpressionVisitor>(boundExp, boundExp1);
        filter = new Filter<ExpressionVisitor>(SPO_CONSTRAINT, andExp);
        getExpression(queryText, filter);
    }

    protected LinkedHashMap<Attribute, ValueOperation> createSingleAVP(Map<Attribute, ValueOperation>... avps) {
        LinkedHashMap<Attribute, ValueOperation> map = new LinkedHashMap<Attribute, ValueOperation>();
        for (Map<Attribute, ValueOperation> avp : avps) {
            map.putAll(avp);
        }
        return map;
    }

    protected Map<Attribute, ValueOperation> createSingleAVP(Attribute attrO, Node node, AVPOperation operation) {
        Map<Attribute, ValueOperation> lhs = new HashMap<Attribute, ValueOperation>();
        ValueOperation lvalue = new ValueOperationImpl(node, operation);
        lhs.put(attrO, lvalue);
        return lhs;
    }

    private void getExpression(String queryText, Expression<ExpressionVisitor> expected) throws Exception {
        Query query = queryBuilder.buildQuery(GRAPH, queryText);
        Expression<ExpressionVisitor> expression = getQueryExpression(query);
        if (expression instanceof Projection) {
            expression = getExpressionField(expression, Projection.class, "nextExpression");
        } else if (expression instanceof Ask) {
            expression = getExpressionField(expression, Ask.class, "nextExpression");
        } else {
            expression = null;
        }
        System.err.println("Expected: " + expected);
        System.err.println("Actual: " + expression);
        assertEquals(expected, expression);
    }

    private Expression<ExpressionVisitor> getQueryExpression(Query query) {
        Expression<ExpressionVisitor> expression = query.getNext();
        expression.accept(simplifier);
        expression = simplifier.getExpression();
        if (simplifier.parseAgain()) {
            expression.accept(simplifier);
        }
        return simplifier.getExpression();
    }

    private Expression<ExpressionVisitor> getExpressionField(Object obj, Class<?> cls, String fieldName)
        throws IllegalAccessException {
        Field field = ReflectTestUtil.getField(cls, fieldName);
        field.setAccessible(true);
        return (Expression<ExpressionVisitor>) field.get(obj);
    }
}
