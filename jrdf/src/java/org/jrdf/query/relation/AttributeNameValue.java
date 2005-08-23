package org.jrdf.query.relation;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;

/**
 * Something in here
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface AttributeNameValue {
    PredicateNode getName();

    ObjectNode getValue();
}
