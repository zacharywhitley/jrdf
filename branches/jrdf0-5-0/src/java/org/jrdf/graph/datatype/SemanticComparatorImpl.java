package org.jrdf.graph.datatype;

import org.jrdf.graph.Literal;
import org.jrdf.vocabulary.XSD;

public final class SemanticComparatorImpl implements SemanticComparator {
    private static final long serialVersionUID = -2619929957116442073L;
    private LexicalComparator comparator;

    private SemanticComparatorImpl() {
    }

    public SemanticComparatorImpl(final LexicalComparator comparator) {
        this.comparator = comparator;
    }

    public int compare(final Literal o1, final Literal o2) {
        final int literalTypeComparison = comparator.compareLiteralTypes(o1, o2);
        if (literalTypeComparison == 0) {
            return compareSameLiterals(o1, o2);
        } else {
            return compareDifferentLiterals(o1, o2, literalTypeComparison);
        }
    }

    private int compareSameLiterals(final Literal o1, final Literal o2) {
        if (o1.isLanguageLiteral()) {
            return comparator.compareLanguage(o1, o2, true);
        } else if (o1.isDatatypedLiteral()) {
            return compareDatatypes(o1, o2);
        } else {
            return comparator.compareLexicalForm(o1, o2);
        }
    }

    private int compareDifferentLiterals(final Literal o1, final Literal o2, final int literalTypeComparison) {
        if (isXsdWithPlainLiteral(o1, o2) || isXsdWithPlainLiteral(o2, o1)) {
            return compareDatatypes(o1, o2);
        } else {
            return literalTypeComparison;
        }
    }

    private boolean isXsdWithPlainLiteral(final Literal o1, final Literal o2) {
        return o1.isDatatypedLiteral() && o1.getDatatypeURI().equals(XSD.STRING) && o2.isPlainLiteral();
    }

    private int compareDatatypes(final Literal o1, final Literal o2) {
        return o1.getValue().equivCompareTo(o2.getValue());
    }
}
