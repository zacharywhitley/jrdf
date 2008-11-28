/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.query.Query;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.AttributeImpl;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import static org.jrdf.query.relation.mem.StrAVPOperation.STR;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.urql.builder.QueryBuilder;
import static org.jrdf.util.test.NodeTestUtil.createLiteral;
import org.jrdf.util.test.ReflectTestUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class ExpressionSimplifierImplUnitTest extends TestCase {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private QueryBuilder queryBuilder;
    private static final Graph GRAPH = FACTORY.getGraph();

    private static final SubjectNodeType SUBJECT_NODE_TYPE = new SubjectNodeType();
    private static final PredicateNodeType PREDICATE_NODE_TYPE = new PredicateNodeType();
    private static final ObjectNodeType OBJECT_NODE_TYPE = new ObjectNodeType();
    private static final VariableName ATTRIBUTE_S = new VariableName("s");
    private static final VariableName ATTRIBUTE_S1 = new VariableName("s1");
    private static final VariableName ATTRIBUTE_S2 = new VariableName("s2");
    private static final VariableName ATTRIBUTE_S3 = new VariableName("s3");
    private static final VariableName ATTRIBUTE_P = new VariableName("p");
    private static final VariableName ATTRIBUTE_O = new VariableName("o");
    private static final VariableName ATTRIBUTE_O1 = new VariableName("o1");
    private static final VariableName ATTRIBUTE_O2 = new VariableName("o2");
    private static final VariableName ATTRIBUTE_O3 = new VariableName("o3");

    private ExpressionSimplifier simplifier;
    private static final Attribute ATTR_S = new AttributeImpl(ATTRIBUTE_S, SUBJECT_NODE_TYPE);
    private static final Attribute ATTR_S1 = new AttributeImpl(ATTRIBUTE_S1, SUBJECT_NODE_TYPE);
    private static final Attribute ATTR_S2 = new AttributeImpl(ATTRIBUTE_S2, SUBJECT_NODE_TYPE);
    private static final Attribute ATTR_S3 = new AttributeImpl(ATTRIBUTE_S3, SUBJECT_NODE_TYPE);
    private static final Attribute ATTR_P = new AttributeImpl(ATTRIBUTE_P, PREDICATE_NODE_TYPE);
    private static final Attribute ATTR_O = new AttributeImpl(ATTRIBUTE_O, OBJECT_NODE_TYPE);
    private static final Attribute ATTR_O1 = new AttributeImpl(ATTRIBUTE_O1, OBJECT_NODE_TYPE);
    private static final Attribute ATTR_O2 = new AttributeImpl(ATTRIBUTE_O2, OBJECT_NODE_TYPE);
    private static final Attribute ATTR_O3 = new AttributeImpl(ATTRIBUTE_O3, OBJECT_NODE_TYPE);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FACTORY.refresh();
        queryBuilder = FACTORY.getNewQueryBuilder();
        simplifier = new ExpressionSimplifierImpl();
    }

    public void testSimpleQuery() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p 'hello'@en }";
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        Literal literal = createLiteral("hello", "en");
        Attribute object1 = new AttributeImpl(new PositionName("OBJECT1"), OBJECT_NODE_TYPE);
        Map<Attribute, ValueOperation> obj = createSingleAVP(object1, literal, EQUALS);
        LinkedHashMap<Attribute, ValueOperation> map = createSingleAVP(subj, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint = new SingleConstraint<ExpressionVisitor>(map);
        getExpression(queryText, constraint);
        queryText = "ASK WHERE { ?s ?p 'hello'@en }";
        object1 = new AttributeImpl(new PositionName("OBJECT2"), OBJECT_NODE_TYPE);
        obj = createSingleAVP(object1, literal, EQUALS);
        map = createSingleAVP(subj, pred, obj);
        constraint = new SingleConstraint<ExpressionVisitor>(map);
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

    public void testSimpleFilter1() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1) }";
        Attribute attrS1 = new AttributeImpl(ATTRIBUTE_S1, SUBJECT_NODE_TYPE);
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj = createSingleAVP(ATTR_O, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp1 = createSingleAVP(subj, pred, obj);
        Map<Attribute, ValueOperation> subj1 = createSingleAVP(attrS1, ANY_SUBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp2 = createSingleAVP(subj1, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint1 = new SingleConstraint<ExpressionVisitor>(avp1);
        SingleConstraint<ExpressionVisitor> constraint2 = new SingleConstraint<ExpressionVisitor>(avp2);
        Conjunction<ExpressionVisitor> conj = new Conjunction<ExpressionVisitor>(constraint1, constraint2);
        getExpression(queryText, conj);
        queryText = "ASK WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1) }";
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
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        Literal literal = createLiteral("abc");
        Map<Attribute, ValueOperation> obj = createSingleAVP(ATTR_O, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp1 = createSingleAVP(subj, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint1 = new SingleConstraint<ExpressionVisitor>(avp1);
        Map<Attribute, ValueOperation> subj1 = createSingleAVP(ATTR_S1, ANY_SUBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp2 = createSingleAVP(subj1, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint2 = new SingleConstraint<ExpressionVisitor>(avp2);
        Map<Attribute, ValueOperation> filterLhs = createSingleAVP(ATTR_O, ANY_NODE, STR);
        Map<Attribute, ValueOperation> filterRhs = createSingleAVP(ATTR_O, literal, EQUALS);
        NEqualsExpression<ExpressionVisitor> neq = new NEqualsExpression<ExpressionVisitor>(filterLhs, filterRhs);
        Conjunction<ExpressionVisitor> conj = new Conjunction<ExpressionVisitor>(constraint1, constraint2);
        Filter<ExpressionVisitor> filter = new Filter<ExpressionVisitor>(conj, neq);
        getExpression(queryText, filter);
        queryText = "ASK WHERE { ?s ?p ?o . ?s1 ?p ?o1 FILTER (?o = ?o1 && str(?o1) != \"abc\") }";
        getExpression(queryText, filter);
    }

    public void testRightUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { ?s ?p ?o . { { ?s1 ?p ?o1 } UNION { ?s2 ?p ?o2 } } }";
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj = createSingleAVP(ATTR_O, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp1 = createSingleAVP(subj, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint1 = new SingleConstraint<ExpressionVisitor>(avp1);
        Map<Attribute, ValueOperation> subj1 = createSingleAVP(ATTR_S1, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj1 = createSingleAVP(ATTR_O1, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp2 = createSingleAVP(subj1, pred, obj1);
        SingleConstraint<ExpressionVisitor> constraint2 = new SingleConstraint<ExpressionVisitor>(avp2);
        Map<Attribute, ValueOperation> subj2 = createSingleAVP(ATTR_S2, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj2 = createSingleAVP(ATTR_O2, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp3 = createSingleAVP(subj2, pred, obj2);
        SingleConstraint<ExpressionVisitor> constraint3 = new SingleConstraint<ExpressionVisitor>(avp3);
        Conjunction<ExpressionVisitor> conj1 = new Conjunction<ExpressionVisitor>(constraint1, constraint2);
        Conjunction<ExpressionVisitor> conj2 = new Conjunction<ExpressionVisitor>(constraint1, constraint3);
        Union<ExpressionVisitor> union = new Union<ExpressionVisitor>(conj1, conj2);
        getExpression(queryText, union);
    }

    public void testLeftUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { { { ?s1 ?p ?o1 } UNION { ?s2 ?p ?o2 } } . ?s ?p ?o }";
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj = createSingleAVP(ATTR_O, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp1 = createSingleAVP(subj, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint1 = new SingleConstraint<ExpressionVisitor>(avp1);
        Map<Attribute, ValueOperation> subj1 = createSingleAVP(ATTR_S1, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj1 = createSingleAVP(ATTR_O1, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp2 = createSingleAVP(subj1, pred, obj1);
        SingleConstraint<ExpressionVisitor> constraint2 = new SingleConstraint<ExpressionVisitor>(avp2);
        Map<Attribute, ValueOperation> subj2 = createSingleAVP(ATTR_S2, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj2 = createSingleAVP(ATTR_O2, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp3 = createSingleAVP(subj2, pred, obj2);
        SingleConstraint<ExpressionVisitor> constraint3 = new SingleConstraint<ExpressionVisitor>(avp3);
        Conjunction<ExpressionVisitor> conj1 = new Conjunction<ExpressionVisitor>(constraint1, constraint2);
        Conjunction<ExpressionVisitor> conj2 = new Conjunction<ExpressionVisitor>(constraint1, constraint3);
        Union<ExpressionVisitor> union = new Union<ExpressionVisitor>(conj1, conj2);
        getExpression(queryText, union);
    }

    public void testTwoUnionInConjunction() throws Exception {
        String queryText = "SELECT * WHERE { { { ?s ?p ?o } UNION { ?s1 ?p ?o1 } } . " +
            "{ { ?s2 ?p ?o2 } UNION { ?s3 ?p ?o3 } } }";
        Map<Attribute, ValueOperation> subj = createSingleAVP(ATTR_S, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> pred = createSingleAVP(ATTR_P, ANY_PREDICATE_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj = createSingleAVP(ATTR_O, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp1 = createSingleAVP(subj, pred, obj);
        SingleConstraint<ExpressionVisitor> constraint1 = new SingleConstraint<ExpressionVisitor>(avp1);
        Map<Attribute, ValueOperation> subj1 = createSingleAVP(ATTR_S1, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj1 = createSingleAVP(ATTR_O1, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp2 = createSingleAVP(subj1, pred, obj1);
        SingleConstraint<ExpressionVisitor> constraint2 = new SingleConstraint<ExpressionVisitor>(avp2);
        Map<Attribute, ValueOperation> subj2 = createSingleAVP(ATTR_S2, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj2 = createSingleAVP(ATTR_O2, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp3 = createSingleAVP(subj2, pred, obj2);
        SingleConstraint<ExpressionVisitor> constraint3 = new SingleConstraint<ExpressionVisitor>(avp3);
        Map<Attribute, ValueOperation> subj3 = createSingleAVP(ATTR_S3, ANY_SUBJECT_NODE, EQUALS);
        Map<Attribute, ValueOperation> obj3 = createSingleAVP(ATTR_O3, ANY_OBJECT_NODE, EQUALS);
        final LinkedHashMap<Attribute, ValueOperation> avp4 = createSingleAVP(subj3, pred, obj3);
        SingleConstraint<ExpressionVisitor> constraint4 = new SingleConstraint<ExpressionVisitor>(avp4);
        Conjunction<ExpressionVisitor> conj1 = new Conjunction<ExpressionVisitor>(constraint1, constraint3);
        Conjunction<ExpressionVisitor> conj2 = new Conjunction<ExpressionVisitor>(constraint1, constraint4);
        Conjunction<ExpressionVisitor> conj3 = new Conjunction<ExpressionVisitor>(constraint2, constraint3);
        Conjunction<ExpressionVisitor> conj4 = new Conjunction<ExpressionVisitor>(constraint2, constraint4);
        Union<ExpressionVisitor> union1 = new Union<ExpressionVisitor>(conj1, conj2);
        Union<ExpressionVisitor> union2 = new Union<ExpressionVisitor>(conj3, conj4);
        Union<ExpressionVisitor> union = new Union<ExpressionVisitor>(union1, union2);
        getExpression(queryText, union);
    }

    private LinkedHashMap<Attribute, ValueOperation> createSingleAVP(Map<Attribute, ValueOperation>... avps) {
        LinkedHashMap<Attribute, ValueOperation> map = new LinkedHashMap<Attribute, ValueOperation>();
        for (Map<Attribute, ValueOperation> avp : avps) {
            map.putAll(avp);
        }
        return map;
    }

    private Map<Attribute, ValueOperation> createSingleAVP(Attribute attrO, Node node, AVPOperation operation) {
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
