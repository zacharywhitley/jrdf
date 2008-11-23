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

package org.jrdf.parser.ntriples.parser;

import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotEmptyString;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.io.Serializable;
import java.util.regex.Pattern;

// TODO N3 Changes - this and StringNodeMapperImpl - sharing regex pattern.  This should be moved to N3 only.
public final class RegexLiteralMatcher implements LiteralMatcher, Serializable {
    private static final long serialVersionUID = 71365287225809670L;
    private static final int LITERAL_VALUES_LENGTH = 3;
    private static final String PATTERN = "\\\"([\\t\\r\\n\\x20-\\x7E]*)\\\"" +
        "(" +
        "((\\@(\\p{Lower}+(\\-a-z0-9]+)*))|(\\^\\^\\<([\\x20-\\x7E]+)\\>))?" +
        ").*";
    private Pattern pattern = Pattern.compile(PATTERN);


    private static final int LITERAL_INDEX = 1;
    private static final int LANGUAGE_INDEX = 5;
    private static final int DATATYPE_INDEX = 8;
    private RegexMatcherFactory regexFactory;
    private NTripleUtil nTripleUtil;

    private RegexLiteralMatcher() {
    }

    public RegexLiteralMatcher(RegexMatcherFactory newRegexFactory, NTripleUtil newNTripleUtil) {
        checkNotNull(newRegexFactory, newNTripleUtil);
        this.regexFactory = newRegexFactory;
        this.nTripleUtil = newNTripleUtil;
    }

    public void setPattern(String newPattern) {
        pattern = Pattern.compile(newPattern);
    }

    public boolean matches(String s) {
        checkNotEmptyString("s", s);
        RegexMatcher matcher = regexFactory.createMatcher(pattern, s);
        return matcher.matches();
    }

    public String[] parse(String s) {
        checkNotEmptyString("s", s);
        RegexMatcher matcher = regexFactory.createMatcher(pattern, s);
        String[] values = new String[LITERAL_VALUES_LENGTH];
        if (matcher.matches()) {
            String ntriplesLiteral = matcher.group(LITERAL_INDEX);
            values[0] = nTripleUtil.unescapeLiteral(ntriplesLiteral);
            values[1] = matcher.group(LANGUAGE_INDEX);
            values[2] = matcher.group(DATATYPE_INDEX);
        }
        return values;
    }
}
