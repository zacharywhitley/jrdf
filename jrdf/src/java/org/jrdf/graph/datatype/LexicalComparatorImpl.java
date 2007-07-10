package org.jrdf.graph.datatype;

import org.jrdf.graph.Literal;

public final class LexicalComparatorImpl implements LexicalComparator {
    private static final long serialVersionUID = 4545758980986518259L;

    public int compare(final Literal o1, final Literal o2) {
        if (o1.getLanguage() != null && o2.getLanguage() != null) {
            return compareLanguage(o1, o2, false);
        } else if (o1.getDatatypeURI() != null && o2.getDatatypeURI() != null) {
            return compareDatatypes(o1, o2);
        } else {
            return compareLexicalForm(o1, o2);
        }
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

    private int compareDatatypes(final Literal o1, final Literal o2) {
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
