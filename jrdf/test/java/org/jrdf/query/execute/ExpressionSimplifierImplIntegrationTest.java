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
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.expression.Union;
import static org.jrdf.query.expression.logic.FalseExpression.FALSE_EXPRESSION;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createAttValue;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createAttValueMap;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.sparql.builder.QueryBuilder;
import static org.jrdf.util.test.NodeTestUtil.createLiteral;
import org.jrdf.util.test.ReflectTestUtil;
import org.jrdf.util.test.TripleTestUtil;

import static org.jrdf.util.test.TripleTestUtil.triple;

import java.lang.reflect.Field;
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

    private ExpressionSimplifier<Void> simplifier;
    private static final Expression SPO_CONSTRAINT = TripleTestUtil.triple("s", "p", "o");
    private static final Expression SPO1_CONSTRAINT = TripleTestUtil.triple("s", "p", "o1");
    private static final Expression S1PO1_CONSTRAINT = TripleTestUtil.triple("s1", "p", "o1");
    private static final Expression S1PO_CONSTRAINT = TripleTestUtil.triple("s1", "p", "o");
    private static final Expression S2PO2_CONSTRAINT = TripleTestUtil.triple("s2", "p", "o2");
    private static final Expression S3PO3_CONSTRAINT = TripleTestUtil.triple("s3", "p", "o3");

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
        Expression constraint = triple("s", "p", literal, 1);
        checkExpression(constraint, queryText);
        queryText = "ASK WHERE { ?s ?p 'hello'@en }";
        constraint = triple("s", "p", literal, 2);
        checkExpression(constraint, queryText);
    }

    public void testSimpleFilter() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o FILTER (str(?o) = \"abc\") }";
        Map<Attribute, Node> subj = createAttValue(ATTR_S, ANY_SUBJECT_NODE);
        Map<Attribute, Node> pred = createAttValue(ATTR_P, ANY_PREDICATE_NODE);
        Map<Attribute, Node> obj = createAttValue(ATTR_O, createLiteral("abc"));
        LinkedHashMap<Attribute, Node> map = createAttValueMap(subj, pred, obj);
        SingleConstraint expectedConstraint = new SingleConstraint(map);
        checkExpression(expectedConstraint, queryText);
        queryText = "ASK WHERE { ?s ?p ?o FILTER (str(?o) = \"abc\") }";
        checkExpression(expectedConstraint, queryText);
    }

    public void testSimpleEqualFilter1() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1) }";
        Conjunction conj = new Conjunction(SPO_CONSTRAINT, S1PO_CONSTRAINT);
        checkExpression(conj, queryText);
    }

    public void testSimpleEqualFilter2() throws Exception {
        String queryText = "ASK WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1) }";
        Conjunction conj = new Conjunction(SPO1_CONSTRAINT, S1PO1_CONSTRAINT);
        checkExpression(conj, queryText);
    }

    public void testComplexFilter() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) = \"abc\") }";
        Map<Attribute, Node> subj = createAttValue(ATTR_S, ANY_SUBJECT_NODE);
        Map<Attribute, Node> pred = createAttValue(ATTR_P, ANY_PREDICATE_NODE);
        Literal literal = createLiteral("abc");
        Map<Attribute, Node> obj = createAttValue(ATTR_O, literal);
        final LinkedHashMap<Attribute, Node> avp1 = createAttValueMap(subj, pred, obj);
        SingleConstraint constraint1 = new SingleConstraint(avp1);
        Attribute attrS1 = new AttributeImpl(ATTRIBUTE_S1, SUBJECT_NODE_TYPE);
        Map<Attribute, Node> subj1 = createAttValue(attrS1, ANY_SUBJECT_NODE);
        final LinkedHashMap<Attribute, Node> avp2 = createAttValueMap(subj1, pred, obj);
        SingleConstraint constraint2 = new SingleConstraint(avp2);
        Conjunction conj = new Conjunction(constraint1, constraint2);
        checkExpression(conj, queryText);
        queryText = "ASK WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) = \"abc\") }";
        checkExpression(conj, queryText);
    }

    public void testComplexFilter1() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) != \"abc\") }";
        Literal literal = createLiteral("abc");
        Map<Attribute, Node> filterLhs = createAttValue(ATTR_O, ANY_NODE);
        Expression strOp = new StrOperator(filterLhs);
        Map<Attribute, Node> filterRhs = createAttValue(ATTR_O, literal);
        Expression singleValue = new SingleValue(filterRhs);
        NEqualsExpression neq = new NEqualsExpression(strOp, singleValue);
        Conjunction conj = new Conjunction(SPO_CONSTRAINT, S1PO_CONSTRAINT);
        Filter filter = new Filter(conj, neq);
        checkExpression(filter, queryText);
    }

    public void testComplexFilter2() throws Exception {
        String queryText = "ASK WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) != \"abc\") }";
        Literal literal = createLiteral("abc");
        Map<Attribute, Node> filterLhs = createAttValue(ATTR_O1, ANY_NODE);
        Expression strOp = new StrOperator(filterLhs);
        Map<Attribute, Node> filterRhs = createAttValue(ATTR_O1, literal);
        Expression singleValue = new SingleValue(filterRhs);
        NEqualsExpression neq = new NEqualsExpression(strOp, singleValue);
        Conjunction conj = new Conjunction(SPO1_CONSTRAINT, S1PO1_CONSTRAINT);
        Filter filter = new Filter(conj, neq);
        checkExpression(filter, queryText);
    }

    public void testRightUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . { { ?s1 ?p ?o1 } UNION { ?s2 ?p ?o2 } } }";
        Conjunction conj1 = new Conjunction(SPO_CONSTRAINT, S1PO1_CONSTRAINT);
        Conjunction conj2 = new Conjunction(SPO_CONSTRAINT, S2PO2_CONSTRAINT);
        Union union = new Union(conj1, conj2);
        checkExpression(union, queryText);
    }

    public void testLeftUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { { { ?s1 ?p ?o1 } UNION { ?s2 ?p ?o2 } } . ?s ?p ?o }";
        Conjunction conj1 = new Conjunction(SPO_CONSTRAINT, S1PO1_CONSTRAINT);
        Conjunction conj2 = new Conjunction(SPO_CONSTRAINT, S2PO2_CONSTRAINT);
        Union union = new Union(conj1, conj2);
        checkExpression(union, queryText);
    }

    public void testTwoUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { { { ?s ?p ?o } UNION { ?s1 ?p ?o1 } } . " +
            "{ { ?s2 ?p ?o2 } UNION { ?s3 ?p ?o3 } } }";
        Conjunction conj1 = new Conjunction(SPO_CONSTRAINT, S2PO2_CONSTRAINT);
        Conjunction conj2 = new Conjunction(SPO_CONSTRAINT, S3PO3_CONSTRAINT);
        Conjunction conj3 = new Conjunction(S1PO1_CONSTRAINT, S2PO2_CONSTRAINT);
        Conjunction conj4 = new Conjunction(S1PO1_CONSTRAINT, S3PO3_CONSTRAINT);
        Union union1 = new Union(conj1, conj2);
        Union union2 = new Union(conj3, conj4);
        Union union = new Union(union1, union2);
        checkExpression(union, queryText);
    }

    public void testBooleanEquals() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o FILTER (bound(?o) = true) }";
        Map<Attribute, Node> obj = createAttValue(ATTR_O, ANY_NODE);
        LogicExpression boundExp = new BoundOperator(obj);
        Filter filter = new Filter(SPO_CONSTRAINT, boundExp);
        checkExpression(filter, queryText);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER (false = true) }";
        filter = new Filter(SPO_CONSTRAINT, FALSE_EXPRESSION);
        checkExpression(filter, queryText);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER (true = false) }";
        filter = new Filter(SPO_CONSTRAINT, FALSE_EXPRESSION);
        checkExpression(filter, queryText);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER (bound(?o) = false) }";
        LogicExpression notExp = new LogicNotExpression(boundExp);
        filter = new Filter(SPO_CONSTRAINT, notExp);
        checkExpression(filter, queryText);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER (false = bound(?o)) }";
        filter = new Filter(SPO_CONSTRAINT, notExp);
        checkExpression(filter, queryText);
        queryText = "SELECT * WHERE { ?s ?p ?o FILTER ((bound(?o) && bound(?s)) = true) }";
        Map<Attribute, Node> subj = createAttValue(ATTR_S, ANY_NODE);
        LogicExpression boundExp1 = new BoundOperator(subj);
        LogicExpression andExp = new LogicAndExpression(boundExp, boundExp1);
        filter = new Filter(SPO_CONSTRAINT, andExp);
        checkExpression(filter, queryText);
    }

    private void checkExpression(Expression expected, String queryText) throws Exception {
        Query query = queryBuilder.buildQuery(GRAPH, queryText);
        Expression expression = getQueryExpression(query);
        if (expression instanceof Projection) {
            expression = getExpressionField(expression, Projection.class, "nextExpression");
        } else if (expression instanceof Ask) {
            expression = getExpressionField(expression, Ask.class, "nextExpression");
        } else {
            expression = null;
        }
        assertEquals(expected, expression);
    }

    private Expression getQueryExpression(Query query) {
        Expression expression = query.getNext();
        expression.accept(simplifier);
        expression = simplifier.getExpression();
        if (simplifier.parseAgain()) {
            expression.accept(simplifier);
            expression = simplifier.getExpression();
        }
        return expression;
    }

    private Expression getExpressionField(Object obj, Class<?> cls, String fieldName)
        throws IllegalAccessException {
        Field field = ReflectTestUtil.getField(cls, fieldName);
        field.setAccessible(true);
        return (Expression) field.get(obj);
    }
}
