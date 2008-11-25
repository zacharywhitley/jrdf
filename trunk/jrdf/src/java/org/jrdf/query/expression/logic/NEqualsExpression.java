package org.jrdf.query.expression.logic;

import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.util.EqualsUtil;

import java.util.Map;

/**
 *
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class NEqualsExpression<V extends ExpressionVisitor> implements LogicExpression<V> {
    private static final long serialVersionUID = -5583172009536428369L;
    private static final int DUMMY_HASHCODE = 47;

    private Map<Attribute, ValueOperation> lhs;
    private Map<Attribute, ValueOperation> rhs;

    private NEqualsExpression() {
    }

    public NEqualsExpression(Map<Attribute, ValueOperation> lhs, Map<Attribute, ValueOperation> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Map<Attribute, ValueOperation> getLhs() {
        return lhs;
    }

    public Map<Attribute, ValueOperation> getRhs() {
        return rhs;
    }

    public void accept(V v) {
        v.visitNEqualsExpression(this);
    }

    public int size() {
        return (lhs.size() + rhs.size()) / 2 + 1;
    }

    public int hashCode() {
        // FIXME TJA: Test drive out values of triple.hashCode()
        int hash = DUMMY_HASHCODE + lhs.hashCode();
        return hash * DUMMY_HASHCODE + rhs.hashCode();
    }

    public String toString() {
        return lhs + " != " + rhs;
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
        return determineEqualityFromFields(this, (NEqualsExpression) obj);
    }

    private boolean determineEqualityFromFields(NEqualsExpression o1, NEqualsExpression o2) {
        return lhsEqual(o1, o2) && rhsEqual(o1, o2);
    }

    private boolean rhsEqual(NEqualsExpression o1, NEqualsExpression o2) {
        return o1.rhs.equals(o2.rhs);
    }

    private boolean lhsEqual(NEqualsExpression o1, NEqualsExpression o2) {
        return o1.lhs.equals(o2.lhs);
    }
}
