/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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
import org.jrdf.graph.Node;
import org.jrdf.graph.local.BlankNodeImpl;
import org.jrdf.vocabulary.XSD;

public final class SemanticLiteralComparatorImpl implements SemanticLiteralComparator {
    private static final long serialVersionUID = -2619929957116442073L;
    private LexicalComparator comparator;

    private SemanticLiteralComparatorImpl() {
    }

    public SemanticLiteralComparatorImpl(final LexicalComparator comparator) {
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
        return o1.getDatatypeValue().equivCompareTo(o2.getDatatypeValue());
    }

    public int compare(Literal literal1, Node node) {
        if (BlankNodeImpl.isBlankNode(node)) {
            return 0;
        } else {
            return compare(literal1, (Literal) node);
        }
    }
}
