package org.jrdf.graph.datatype;

import org.jrdf.graph.Literal;

public final class SemanticComparatorImpl implements SemanticComparator {
    private static final long serialVersionUID = -2619929957116442073L;
    private LexicalComparator comparator;

    private SemanticComparatorImpl() {
    }

    public SemanticComparatorImpl(final LexicalComparator comparator) {
        this.comparator = comparator;
    }

    public int compare(final Literal o1, final Literal o2) {
        if (o1.getLanguage() != null && o2.getLanguage() != null) {
            return comparator.compareLanguage(o1, o2, true);
        } else if (o1.getDatatypeURI() != null && o2.getDatatypeURI() != null) {
            return compareDatatypes(o1, o2);
        } else {
            return comparator.compareLexicalForm(o1, o2);
        }
    }

    private int compareDatatypes(final Literal o1, final Literal o2) {
        return o1.getValue().equivCompareTo(o2.getValue());
    }
}
