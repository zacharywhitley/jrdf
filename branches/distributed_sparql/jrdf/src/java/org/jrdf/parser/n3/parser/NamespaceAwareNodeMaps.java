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

import org.jrdf.graph.Node;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.LiteralParser;
import org.jrdf.parser.ntriples.parser.NodeMaps;
import org.jrdf.parser.ntriples.parser.RegexNodeParser;
import static org.jrdf.util.param.ParameterUtil.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.*;

public class NamespaceAwareNodeMaps implements NodeMaps {
    /**
     * Pattern for Subject in N3 triple pattern.
     */
    public static final Pattern SUBJECT_REGEX = compile(
        "(<([\\x20-\\x7E]+?)>||((\\p{Alpha}[\\x20-\\x7E]*?):(\\p{Alpha}[\\x20-\\x7E]*?))" +
            "|_:(\\p{Alpha}[\\x20-\\x7E]*?))");

    /**
     * Pattern for Predicate in N3 triple pattern.
     */
    public static final Pattern PREDICATE_REGEX = compile(
        "(<([\\x20-\\x7E]+?)>||((\\p{Alpha}[\\x20-\\x7E]*?):([\\x20-\\x7E]*?))" +
            "|_:(\\p{Alpha}[\\x20-\\x7E]*?)|([\\x20-\\x7E]+?))");

    /**
     * Pattern for Object in N3 triple pattern.
     */
    public static final Pattern OBJECT_REGEX = compile(
        "(<([\\x20-\\x7E]+?)>||((\\p{Alpha}[\\x20-\\x7E]*?):(\\p{Alpha}[\\x20-\\x7E]*?))" +
            "|_:(\\p{Alpha}[\\x20-\\x7E]*?)|([\\x20-\\x7E]+?))");
    private static final int URI_GROUP = 2;
    private static final int NS_LOCAL_NAME_GROUP = 3;
    private static final int BLANK_NODE_GROUP = 6;
    private static final int LITERAL_GROUP = 7;
    private final NamespaceAwareURIReferenceParser uriReferenceParser;
    private final BlankNodeParser blankNodeParser;
    private final LiteralParser literalNodeParser;
    private final Map<Integer, RegexNodeParser> groupMatches = new HashMap<Integer, RegexNodeParser>();
    private Map<Integer, RegexNodeParser> subjectGroupMatches;
    private Map<Integer, RegexNodeParser> predicateGroupMatches;
    private Map<Integer, RegexNodeParser> objectGroupMatches;

    public NamespaceAwareNodeMaps(final NamespaceAwareURIReferenceParser newURIReferenceParser,
        final BlankNodeParser newBlankNodeParser, final LiteralParser newLiteralNodeParser) {
        checkNotNull(newURIReferenceParser, newBlankNodeParser, newLiteralNodeParser);
        uriReferenceParser = newURIReferenceParser;
        blankNodeParser = newBlankNodeParser;
        literalNodeParser = newLiteralNodeParser;
        setUpMatches();
    }

    public Map<Integer, RegexNodeParser> getSubjectMap() {
        return subjectGroupMatches;
    }

    public Map<Integer, RegexNodeParser> getPredicateMap() {
        return predicateGroupMatches;
    }

    public Map<Integer, RegexNodeParser> getObjectMap() {
        return objectGroupMatches;
    }

    private void setUpMatches() {
        standardMatches();
        subjectGroupMatches = new HashMap<Integer, RegexNodeParser>(groupMatches);
        predicateGroupMatches = new HashMap<Integer, RegexNodeParser>(groupMatches);
        objectGroupMatches = new HashMap<Integer, RegexNodeParser>(groupMatches);
        subjectGroupMatches.remove(LITERAL_GROUP);
        predicateGroupMatches.remove(BLANK_NODE_GROUP);
        predicateGroupMatches.remove(LITERAL_GROUP);
    }

    private void standardMatches() {
        groupMatches.put(URI_GROUP, new RegexNodeParser() {
            public Node parse(final String line) throws ParseException {
                return uriReferenceParser.parseURIReference(line);
            }
        });
        groupMatches.put(NS_LOCAL_NAME_GROUP, new RegexNodeParser() {
            public Node parse(final String line) throws ParseException {
                return uriReferenceParser.parseURIReferenceWithNamespace(line);
            }
        });
        groupMatches.put(BLANK_NODE_GROUP, new RegexNodeParser() {
            public Node parse(final String line) throws ParseException {
                return blankNodeParser.parseBlankNode(line);
            }
        });
        groupMatches.put(LITERAL_GROUP, new RegexNodeParser() {
            public Node parse(final String line) throws ParseException {
                return literalNodeParser.parseLiteral(line);
            }
        });
    }
}