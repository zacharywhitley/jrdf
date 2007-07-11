package org.jrdf.graph.datatype;

import org.jrdf.graph.Literal;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Sorts by the URI of the literal and then value.  Will return 0 only if both the URI are the same and the literal
 * value are the same.  Use SemanticComparator for semantic comparison.
 */
public interface LexicalComparator extends Comparator<Literal>, Serializable {
    /**
     * Returns order based on literal type, always goes plain, language and then data type.  i.e. if o1 is a plain
     * literal always -1, if o1 is a language literal and o2 is a plain 1, else -1 and if o1 is a
     * datatype literal then always 1.  0 if literals are equal.
     *
     * @param o1 the first literal to compare.
     * @param o2 the second literal to compare.
     * @return -1, 0 or 1 if literals based on type are smaller, equal or larger.
     */
    int compareLiteralTypes(Literal o1, Literal o2);

    int compareLanguage(Literal o1, Literal o2, final boolean ignoreCase);

    int compareLexicalForm(Literal o1, Literal o2);
}
