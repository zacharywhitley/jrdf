package org.jrdf.query.relation.operation.mem.logic;

import org.jrdf.graph.AnyNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.operation.BooleanEvaluator;
import org.jrdf.vocabulary.XSD;

import java.util.Map;

public class SimpleBooleanEvaluator extends ExpressionVisitorAdapter implements BooleanEvaluator {
    private final NodeComparator nodeComparator;

    private boolean contradiction;
    private Tuple tuple;
    private Node value;

    public SimpleBooleanEvaluator(NodeComparator nodeComparator) {
        this.nodeComparator = nodeComparator;
    }

    public Node getValue() {
        return value;
    }

    @Override
    public <V extends ExpressionVisitor> void visitSingleValue(SingleValue<V> value) {
        Map<Attribute, ValueOperation> avo = value.getAVO();
        Map.Entry<Attribute, ValueOperation> entry = avo.entrySet().iterator().next();
        Attribute attribute = entry.getKey();
        Node node = entry.getValue().getValue();
        ValueOperation valueOperation = tuple.getValueOperation(attribute);
        if (AnyNode.ANY_NODE.equals(node)) {
            if (valueOperation != null) {
                this.value = valueOperation.getValue();
            } else {
                this.value = null;
            }
        } else {
            this.value = node;
        }
    }

    @Override
    public <V extends ExpressionVisitor> void visitStr(StrOperator<V> str) {
        final ValueOperation valueOperation = getValueOperation(str);
        if (valueOperation != null) {
            Literal literal = (Literal) valueOperation.getValue();
            value = new LiteralImpl(literal.getLexicalForm());
        }
    }

    @Override
    public <V extends ExpressionVisitor> void visitLang(LangOperator<V> lang) {
        final ValueOperation valueOperation = getValueOperation(lang);
        if (valueOperation != null) {
            Literal literal = (Literal) valueOperation.getValue();
            value = new LiteralImpl(literal.getLanguage());
        }
    }

    @Override
    public <V extends ExpressionVisitor> void visitBound(BoundOperator<V> bound) {
        final ValueOperation valueOperation = getValueOperation(bound);
        contradiction = (valueOperation == null);
        value = new LiteralImpl(Boolean.toString(contradiction), XSD.BOOLEAN);
    }

    @Override
    public <V extends ExpressionVisitor> void visitLogicalAnd(LogicalAndExpression<V> andExpression) {
        andExpression.getLhs().accept((V) this);
        boolean lhsBoolean = contradiction;
        andExpression.getRhs().accept((V) this);
        contradiction = lhsBoolean || contradiction;
    }

    @Override
    public <V extends ExpressionVisitor> void visitLogicalNot(LogicalNotExpression<V> notExpression) {
        notExpression.getExpression().accept((V) this);
        contradiction = !contradiction;
    }

    @Override
    public <V extends ExpressionVisitor> void visitEqualsExpression(EqualsExpression<V> equalsExpression) {
        Node lhsValue = getValue(tuple, equalsExpression.getLhs());
        Node rhsValue = getValue(tuple, equalsExpression.getRhs());
        contradiction = compareNodes(lhsValue, rhsValue) != 0;
    }

    @Override
    public <V extends ExpressionVisitor> void visitNEqualsExpression(NEqualsExpression<V> nEqualsExpression) {
        Node lhsValue = getValue(tuple, nEqualsExpression.getLhs());
        Node rhsValue = getValue(tuple, nEqualsExpression.getRhs());
        contradiction = compareNodes(lhsValue, rhsValue) == 0;
    }

    @Override
    public <V extends ExpressionVisitor> void visitLessThanExpression(LessThanExpression<V> lessThanExpression) {
        Node lhsValue = getValue(tuple, lessThanExpression.getLhs());
        Node rhsValue = getValue(tuple, lessThanExpression.getRhs());
        contradiction = compareNodes(lhsValue, rhsValue) >= 0;
    }

    private <V extends ExpressionVisitor> ValueOperation getValueOperation(Operator<V> str) {
        final Map<Attribute, ValueOperation> avp = str.getAVO();
        Attribute attribute = avp.keySet().iterator().next();
        return tuple.getValueOperation(attribute);
    }

    private int compareNodes(Node lNode, Node rNode) {
        int result;
        if (lNode == null && rNode == null) {
            result = 0;
        } else if (lNode == null) {
            result = -1;
        } else if (rNode == null) {
            result = 1;
        } else {
            result = nodeComparator.compare(lNode, rNode);
        }
        return result;
    }

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }

    public <V extends ExpressionVisitor> Node getValue(Tuple tuple, Expression<V> expression) {
        BooleanEvaluator evaluator = new SimpleBooleanEvaluator(nodeComparator);
        evaluator.setTuple(tuple);
        expression.accept((V) evaluator);
        return evaluator.getValue();
    }

    public <V extends ExpressionVisitor> boolean evaluate(Tuple tuple, LogicExpression<V> expression) {
        setTuple(tuple);
        expression.accept((V) this);
        return !contradiction;
    }
}