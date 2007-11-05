package org.jrdf.graph.datatype;

import org.jrdf.graph.Literal;

public final class LexicalComparatorImpl implements LexicalComparator {
    private static final long serialVersionUID = 4545758980986518259L;

    public int compare(final Literal o1, final Literal o2) {
        final int literalTypeComparison = compareLiteralTypes(o1, o2);
        if (literalTypeComparison == 0) {
            return compareSameLiterals(o1, o2);
        } else {
            return literalTypeComparison;
        }
    }

    public int compareLiteralTypes(final Literal o1, final Literal o2) {
        // check if they are the same type first.
        if (areLiteralsSameType(o1, o2)) {
            return 0;
        }
        return compareFirstLiteral(o1, o2);
    }

    public int compareLanguage(final Literal o1, final Literal o2, final boolean ignoreCase) {
        final int languageComparison = compareIgnoringCase(o1, o2, ignoreCase);
        if (languageComparison == 0) {
            return compareLexicalForm(o1, o2);
        } else {
            return languageComparison;
        }
    }

    public int compareLexicalForm(final Literal o1, final Literal o2) {
        return o1.getLexicalForm().compareTo(o2.getLexicalForm());
    }

    private int compareSameLiterals(Literal o1, Literal o2) {
        if (o1.isLanguageLiteral()) {
            return compareLanguage(o1, o2, false);
        } else if (o1.isDatatypedLiteral()) {
            return compareLexicalFormDatatypes(o1, o2);
        } else {
            return compareLexicalForm(o1, o2);
        }
    }

    private int compareFirstLiteral(Literal o1, Literal o2) {
        if (o1.isPlainLiteral()) {
            // if o1 is a plain literal then it is always smaller than o2.
            return -1;
        } else if (o1.isLanguageLiteral()) {
            return compareLanguageLiteral(o2);
        } else {
            // o1 must be datatype literal and is always greater.
            return 1;
        }
    }

    private int compareLanguageLiteral(Literal o2) {
        if (o2.isDatatypedLiteral()) {
            // if o1 is a language type and o2 is a datatype.
            return -1;
        } else {
            // o1 is a language type and o2 is a plain liteal.
            return 1;
        }
    }

    private boolean areLiteralsSameType(final Literal o1, final Literal o2) {
        return bothLiterals(o1, o2) || bothDatatyped(o1, o2) || bothPlain(o1, o2);
    }

    private boolean bothPlain(Literal o1, Literal o2) {
        return o1.isPlainLiteral() && o2.isPlainLiteral();
    }

    private boolean bothDatatyped(Literal o1, Literal o2) {
        return o1.isDatatypedLiteral() && o2.isDatatypedLiteral();
    }

    private boolean bothLiterals(Literal o1, Literal o2) {
        return o1.isLanguageLiteral() && o2.isLanguageLiteral();
    }

    private int compareLexicalFormDatatypes(final Literal o1, final Literal o2) {
        final int datatypeComparison = o1.getDatatypeURI().compareTo(o2.getDatatypeURI());
        if (datatypeComparison == 0) {
            return compareLexicalForm(o1, o2);
        } else {
            return datatypeComparison;
        }
    }

    private int compareIgnoringCase(final Literal o1, final Literal o2, final boolean ignoreCase) {
        final String language1;
        final String language2;
        if (ignoreCase) {
            language1 = o1.getLanguage().toLowerCase();
            language2 = o2.getLanguage().toLowerCase();
        } else {
            language1 = o1.getLanguage();
            language2 = o1.getLanguage();
        }
        return language1.compareTo(language2);
    }
}
