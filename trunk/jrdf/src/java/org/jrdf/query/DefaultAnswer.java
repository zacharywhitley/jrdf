/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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
 */

package org.jrdf.query;

import org.jrdf.graph.Triple;
import org.jrdf.util.EqualsUtil;
import org.jrdf.util.param.ParameterUtil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation of {@link Answer}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultAnswer implements Answer, Serializable {

    private static final long serialVersionUID = -4724846731215773529L;
    private static final int DEFAULT_HASH_CODE = 7;
    private static final String DELIMITER_OPEN = "{";
    private static final String DELIMITER_CLOSE = "}";
    private static final String INDENT = "  ";
    private static final String NEW_LINE = "\n";
    private List<Triple> solutions;

    public DefaultAnswer(List<Triple> solutions) {
        ParameterUtil.checkNotNull("solutions", solutions);
        this.solutions = solutions;
    }

    public List<Triple> getSolutions() {
        return solutions;
    }

    /**
     * Two answers are equal if their {@linkplain #getSolutions() solutions} are equal.
     * <p>Two {@link Answer}s of differing implementation will not neccessarily be equal.</p>
     */
    public boolean equals(Object obj) {
        // FIXME TJA: Should different implementations of Answer be equal?
        if (EqualsUtil.isNull(obj)) {
            return false;
        }
        if (EqualsUtil.sameReference(this, obj)) {
            return true;
        }
        if (EqualsUtil.differentClasses(this, obj)) {
            return false;
        }
        return determineEqualityFromFields((Answer) obj);
    }

    public int hashCode() {
        return DEFAULT_HASH_CODE;
    }

    private boolean determineEqualityFromFields(Answer obj) {
        return ((Answer) obj).getSolutions().equals(solutions);
    }

    public String toString() {
        StringBuffer stringForm = new StringBuffer();
        stringForm.append(DELIMITER_OPEN);
        stringForm.append(getTriplesAsString(solutions));
        stringForm.append(DELIMITER_CLOSE);
        return stringForm.toString();
    }

    private String getTriplesAsString(List<Triple> triples) {
        StringBuffer stringForm = new StringBuffer();
        for (Iterator<Triple> iterator = triples.iterator(); iterator.hasNext();) {
            String tripleString = getTripleAsString(iterator.next(), !iterator.hasNext());
            stringForm.append(tripleString);
        }
        return stringForm.toString();
    }

    private String getTripleAsString(Triple triple, boolean lastTriple) {
        String stringForm = NEW_LINE + INDENT + triple.toString();
        if (lastTriple) {
            stringForm += NEW_LINE;
        }
        return stringForm;
    }
}
