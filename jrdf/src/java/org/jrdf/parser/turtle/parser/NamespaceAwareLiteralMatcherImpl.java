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

package org.jrdf.parser.turtle.parser;

import org.jrdf.parser.NamespaceListener;
import org.jrdf.parser.ntriples.parser.LiteralMatcher;
import org.jrdf.parser.ntriples.parser.NTripleUtil;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotEmptyString;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.io.Serializable;
import java.util.regex.Pattern;

public final class NamespaceAwareLiteralMatcherImpl implements LiteralMatcher, Serializable {
    private static final long serialVersionUID = -1198268919417815649L;
    private static final int LITERAL_VALUES_LENGTH = 3;
    private Pattern pattern = Pattern.compile("\\\"([\\x20-\\x7E]*)\\\"" +
        "(" +
        "\\@(\\p{Lower}[\\-\\p{Alnum}]*)?|" +
        "\\^\\^\\<([\\x20-\\x7E]+)\\>|" +
        "\\^\\^(\\p{Alpha}[\\x20-\\x7E]*?):((\\p{Alpha}[\\x20-\\x7E]*)?)" +
        ")*\\p{Blank}*");
    private static final int LITERAL_INDEX = 1;
    private static final int LANGUAGE_INDEX = 3;
    private static final int DATATYPE_URI_INDEX = 4;
    private static final int DATATYPE_PREFIX_INDEX = 5;
    private static final int DATATYPE_LOCAL_NAME_INDEX = 6;
    private RegexMatcherFactory regexFactory;
    private NTripleUtil nTripleUtil;
    private NamespaceListener listener;

    private NamespaceAwareLiteralMatcherImpl() {
    }

    public NamespaceAwareLiteralMatcherImpl(RegexMatcherFactory newRegexFactory, NTripleUtil newNTripleUtil,
        NamespaceListener newListener) {
        checkNotNull(newRegexFactory, newNTripleUtil);
        regexFactory = newRegexFactory;
        nTripleUtil = newNTripleUtil;
        listener = newListener;
    }

    public void setPattern(String newPattern) {
        pattern = Pattern.compile(newPattern);
    }

    public boolean matches(String s) {
        checkNotEmptyString("s", s);
        return regexFactory.createMatcher(pattern, s).matches();
    }

    public String[] parse(String s) {
        checkNotEmptyString("s", s);
        RegexMatcher matcher = regexFactory.createMatcher(pattern, s);
        String[] values = new String[LITERAL_VALUES_LENGTH];
        if (matcher.matches()) {
            String ntriplesLiteral = matcher.group(LITERAL_INDEX);
            values[0] = nTripleUtil.unescapeLiteral(ntriplesLiteral);
            values[1] = matcher.group(LANGUAGE_INDEX);
            values[2] = getDatatypeString(matcher);
        }
        return values;
    }

    private String getDatatypeString(RegexMatcher matcher) {
        String result = null;
        final String prefix = matcher.group(DATATYPE_PREFIX_INDEX);
        final String fullURI = matcher.group(DATATYPE_URI_INDEX);
        if (fullURI != null) {
            result = fullURI;
        } else if (prefix != null) {
            result = listener.getFullURI(prefix) + matcher.group(DATATYPE_LOCAL_NAME_INDEX);
        }
        return result;
    }
}