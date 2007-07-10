package org.jrdf.graph.datatype;

import org.jrdf.graph.Literal;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Sorts by the URI of the literal and then value.  Will return 0 only if both the URI are the same and the literal
 * value are the same.  Use SemanticComparator for semantic comparison.
 */
public interface LexicalComparator extends Comparator<Literal>, Serializable {
    int compareLanguage(Literal o1, Literal o2, final boolean ignoreCase);

    int compareLexicalForm(Literal o1, Literal o2);
}
