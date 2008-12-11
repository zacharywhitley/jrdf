package org.jrdf.query.expression;

import org.jrdf.util.EqualsUtil;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.type.PositionalNodeType;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.urql.analysis.VariableCollector;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class Ask<V extends ExpressionVisitor> implements Expression<V>, Serializable {
    private static final long serialVersionUID = 7831085111074741271L;

    private static final int DUMMY_HASHCODE = 47;
    private Expression<ExpressionVisitor> nextExpression;
    private Map<AttributeName, PositionalNodeType> allVariables;

    private Ask() {
    }

    public Ask(Expression<ExpressionVisitor> nextExpression, VariableCollector variableCollector) {
        this.nextExpression = nextExpression;
        allVariables = variableCollector.getAttributes();
    }

    public Map<Attribute, ValueOperation> getAVO() {
        return nextExpression.getAVO();
    }

    public void accept(V v) {
        v.visitAsk(this);
    }

    public int size() {
        return nextExpression.size();
    }

    public Expression<ExpressionVisitor> getNextExpression() {
        return nextExpression;
    }

    public void setNextExpression(Expression<ExpressionVisitor> expression) {
        nextExpression = expression;
    }

    public Map<AttributeName, PositionalNodeType> getAllVariables() {
        return allVariables;
    }

    @Override
    public String toString() {
        return "ASK" + "\n" + nextExpression.toString();
    }

    @Override
    public int hashCode() {
        return DUMMY_HASHCODE + nextExpression.hashCode();
    }

    @Override
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
        return determineEqualityFromFields(this, (Ask) obj);
    }

    private boolean determineEqualityFromFields(Ask o1, Ask o2) {
        return o1.getNextExpression().equals(o2.getNextExpression());
    }
}
