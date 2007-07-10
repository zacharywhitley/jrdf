package org.jrdf.graph.datatype;

import org.jrdf.graph.Literal;

import java.util.Comparator;
import java.io.Serializable;

/**
 * Sorts by the URI of the literal and then value.  Will return 0 if the data type URIs are different but are
 * comparable types (such as decimal and integer) and the values are the same.
 */
public interface SemanticComparator extends Comparator<Literal>, Serializable {
}
