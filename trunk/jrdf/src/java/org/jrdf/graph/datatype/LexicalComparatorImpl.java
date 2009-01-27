/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

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
