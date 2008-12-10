package org.jrdf.query.expression.logic;

import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.BiOperandExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.util.EqualsUtil;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 *
 * @author Yuan-Fang Li
 * @version $Id$
 */
public class LessThanExpression<V extends ExpressionVisitor> implements LogicExpression<V>, BiOperandExpression<V> {
    private static final long serialVersionUID = -8314509866292119440L;
    private static final int DUMMY_HASHCODE = 47;

    private Expression<V> lhs;
    private Expression<V> rhs;

    private LessThanExpression() {
    }

    public LessThanExpression(Expression<V> lhs, Expression<V> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Map<Attribute, ValueOperation> getAVO() {
        Map<Attribute, ValueOperation> map = new LinkedHashMap<Attribute, ValueOperation>();
        map.putAll(lhs.getAVO());
        map.putAll(rhs.getAVO());
        return map;
    }

    public Expression<V> getLhs() {
        return lhs;
    }

    public Expression<V> getRhs() {
        return rhs;
    }

    public void accept(V v) {
        v.visitLessThanExpression(this);
    }

    public int size() {
        return (lhs.size() + rhs.size()) / 2 + 1;
    }

    public int hashCode() {
        int hash = DUMMY_HASHCODE + lhs.hashCode();
        return hash * DUMMY_HASHCODE + rhs.hashCode();
    }

    public String toString() {
        return lhs + " < " + rhs;
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
        return determineEqualityFromFields(this, (LessThanExpression) obj);
    }

    private boolean determineEqualityFromFields(LessThanExpression o1, LessThanExpression o2) {
        return lhsEqual(o1, o2) && rhsEqual(o1, o2);
    }

    private boolean rhsEqual(LessThanExpression o1, LessThanExpression o2) {
        return o1.rhs.equals(o2.rhs);
    }

    private boolean lhsEqual(LessThanExpression o1, LessThanExpression o2) {
        return o1.lhs.equals(o2.lhs);
    }
}
