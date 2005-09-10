package org.jrdf.query.relation.operation;


/**
 * Combines two relations attributes if they have common tuple values.  The
 * same as AND in Algebra A.
 * <p/>
 * The general algorithm is:
 * 1. Find all matching attributes on two relations.
 * 2. Union the attributes and tuples together.
 * 3. Remove any attributes that are not common between the two.
 * <p/>
 * For example:
 * Relation 1 has the following statements: <1, a, foo>, <1, b, bar>,
 * <1, c, bar>, <1, c, baz>
 * Relation 2 has the following statements: <2, b, foo>, <2, c, bar>,
 * <2, f, bar>, <2, g, bar>, <2, c, baz>, <2, f, baz>
 * <p/>
 * After join:
 * <2, b, bar>, <2, c, bar>, <1, c, bar>, <1, f, bar>, <1, f, bar>, <2, c, baz>,
 * <1, c, baz>, <1, f, baz>
 * Removed:
 * <2, a, foo>, <1, a, foo>
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface Join extends Operation {
}
