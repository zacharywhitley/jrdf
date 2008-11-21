package org.jrdf.query;

import org.jrdf.graph.Graph;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.AskAnswerImpl;
import org.jrdf.query.execute.ExpressionSimplifier;
import org.jrdf.query.execute.ExpressionSimplifierImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.GraphRelation;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class AskQueryImpl implements Query {
    private Expression<ExpressionVisitor> expression;
    private final GraphRelationFactory graphRelationFactory;

    public AskQueryImpl(Expression<ExpressionVisitor> expression, GraphRelationFactory graphRelationFactory) {
        checkNotNull(expression, graphRelationFactory);
        this.expression = expression;
        this.graphRelationFactory = graphRelationFactory;
    }

    public Answer executeQuery(Graph graph, QueryEngine queryEngine) {
        checkNotNull(graph, queryEngine);
        long timeStarted = System.currentTimeMillis();
        boolean result = getResult(graph, queryEngine);
        return new AskAnswerImpl(System.currentTimeMillis() - timeStarted, result);
    }

    private boolean getResult(Graph graph, QueryEngine queryEngine) {
        GraphRelation entireGraph = graphRelationFactory.createRelation(graph);
        queryEngine.initialiseBaseRelation(entireGraph);
        ExpressionSimplifier simplifier = new ExpressionSimplifierImpl();
        expression.accept(simplifier);
        expression = simplifier.getExpression();
        expression.accept(queryEngine);
        return !queryEngine.getResult().getTuples().isEmpty();
    }
}
