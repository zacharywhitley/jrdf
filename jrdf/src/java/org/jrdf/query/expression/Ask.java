package org.jrdf.query.expression;

import org.jrdf.util.EqualsUtil;

import java.io.Serializable;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class Ask<V extends ExpressionVisitor> implements Expression<V>, Serializable {
    private static final long serialVersionUID = 7831085111074741271L;

    private static final int DUMMY_HASHCODE = 47;
    private Expression<ExpressionVisitor> nextExpression;

    private Ask() {
    }

    public Ask(Expression<ExpressionVisitor> nextExpression) {
        this.nextExpression = nextExpression;
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
