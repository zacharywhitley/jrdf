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

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.LiteralParser;
import org.jrdf.parser.ntriples.parser.ObjectParser;
import org.jrdf.parser.ntriples.parser.PredicateParser;
import org.jrdf.parser.ntriples.parser.SubjectParser;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.util.boundary.RegexMatcher;

public class TripleParserImpl implements TripleParser {
    private final SubjectParser subjectParser;
    private final PredicateParser predicateParser;
    private final ObjectParser objectParser;
    private final TripleFactory tripleFactory;
    private final BlankNodeParser blankNodeParser;

    public TripleParserImpl(NamespaceAwareURIReferenceParser newURIReferenceParser, BlankNodeParser newBlankNodeParser,
        LiteralParser newLiteralNodeParser, TripleFactory newTripleFactory) {
        this.subjectParser = new SubjectParserImpl(newURIReferenceParser, newBlankNodeParser);
        this.predicateParser = new PredicateParserImpl(newURIReferenceParser);
        this.objectParser = new ObjectParserImpl(newURIReferenceParser, newBlankNodeParser, newLiteralNodeParser);
        this.blankNodeParser = newBlankNodeParser;
        this.tripleFactory = newTripleFactory;
    }

    public Triple parseTriple(RegexMatcher tripleRegexMatcher) {
        try {
            SubjectNode subject = subjectParser.parseSubject(tripleRegexMatcher);
            PredicateNode predicate = predicateParser.parsePredicate(tripleRegexMatcher);
            ObjectNode object = objectParser.parseObject(tripleRegexMatcher);
            if (subject != null && predicate != null && object != null) {
                return tripleFactory.createTriple(subject, predicate, object);
            } else {
                // This is an error - will become a NoSuchElementException.
                return null;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        blankNodeParser.clear();
    }
}