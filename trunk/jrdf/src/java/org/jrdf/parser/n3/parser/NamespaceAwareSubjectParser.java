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

package org.jrdf.parser.n3.parser;

import org.jrdf.graph.SubjectNode;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.SubjectParser;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

public final class NamespaceAwareSubjectParser implements SubjectParser {
    private static final Pattern REGEX = compile(
        "(<([\\x20-\\x7E]+?)>||((\\p{Alpha}[\\x20-\\x7E]*?):(\\p{Alpha}[\\x20-\\x7E]*?))" +
        "|_:(\\p{Alpha}[\\x20-\\x7E]*?))");
    private static final int LINE_GROUP = 0;
    private static final int URI_GROUP = 2;
    private static final int NS_LOCAL_NAME_GROUP = 3;
    private static final int NS_GROUP = 4;
    private static final int LOCAL_NAME_GROUP = 5;
    private static final int BLANK_NODE_GROUP = 6;
    private final RegexMatcherFactory factory;
    private final NamespaceAwareURIReferenceParser uriReferenceParser;
    private final BlankNodeParser blankNodeParser;

    public NamespaceAwareSubjectParser(final RegexMatcherFactory newFactory,
        final NamespaceAwareURIReferenceParser newURIReferenceParser, final BlankNodeParser newBlankNodeParser) {
        checkNotNull(newFactory, newURIReferenceParser, newBlankNodeParser);
        factory = newFactory;
        uriReferenceParser = newURIReferenceParser;
        blankNodeParser = newBlankNodeParser;
    }

    public SubjectNode parseNode(final CharSequence line) throws ParseException {
        checkNotNull(line);
        final RegexMatcher regexMatcher = factory.createMatcher(REGEX, line);
        if (regexMatcher.matches()) {
            return parseSubject(regexMatcher);
        } else {
            throw new IllegalArgumentException("Couldn't match line: " + line);
        }
    }

    private SubjectNode parseSubject(final RegexMatcher matcher) throws ParseException {
        checkNotNull(matcher);
        if (matcher.group(URI_GROUP) != null) {
            return uriReferenceParser.parseURIReference(matcher.group(URI_GROUP));
        } else if (matcher.group(NS_LOCAL_NAME_GROUP) != null) {
            return uriReferenceParser.parseURIReference(matcher.group(NS_GROUP), matcher.group(LOCAL_NAME_GROUP));
        } else if (matcher.group(BLANK_NODE_GROUP) != null) {
            return blankNodeParser.parseBlankNode(matcher.group(BLANK_NODE_GROUP));
        } else {
            throw new ParseException("Failed to parse line: " + matcher.group(LINE_GROUP), 1);
        }
    }
}