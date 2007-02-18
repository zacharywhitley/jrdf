/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.parser.ntriples;

import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.TripleFactoryException;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.ntriples.parser.ObjectParser;
import org.jrdf.parser.ntriples.parser.PredicateParser;
import org.jrdf.parser.ntriples.parser.SubjectParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.NoSuchElementException;

public class NTriplesEventReader implements RDFEventReader {
    private static final Pattern COMMENT_REGEX = Pattern.compile("\\p{Blank}*#([\\x20-\\x7E[^\\n\\r]])*");
    private static final Pattern TRIPLE_REGEX = Pattern.compile("\\p{Blank}*" +
        "(\\<([\\x20-\\x7E]+?)\\>|_:((\\p{Alpha}\\p{Alnum}*?)))\\p{Blank}+" +
        "(\\<([\\x20-\\x7E]+?)\\>)\\p{Blank}+" +
        "(\\<([\\x20-\\x7E]+?)\\>|_:((\\p{Alpha}\\p{Alnum}*?))|((([\\x20-\\x7E]+?))))\\p{Blank}*" +
        "\\.\\p{Blank}*");

    private final LineNumberReader bufferedReader;
    private final URI baseURI;
    private final SubjectParser subjectParser;
    private final PredicateParser predicateParser;
    private final ObjectParser objectParser;
    private final TripleFactory factory;
    private Triple nextTriple;

    public NTriplesEventReader(final InputStream in, final URI baseURI, final TripleFactory factory,
            final SubjectParser subjectParser, final PredicateParser predicateParser,
            final ObjectParser objectParser) {
        this(new InputStreamReader(in), baseURI, factory, subjectParser, predicateParser, objectParser);
    }

    public NTriplesEventReader(Reader reader, URI baseURI, TripleFactory factory, SubjectParser subjectParser,
            PredicateParser predicateParser, ObjectParser objectParser) {
        this.bufferedReader = new LineNumberReader(reader);
        this.baseURI = baseURI;
        this.factory = factory;
        this.subjectParser = subjectParser;
        this.predicateParser = predicateParser;
        this.objectParser = objectParser;
        parseNext();
    }

    public boolean hasNext() {
        return nextTriple != null;
    }

    public Triple next() {
        Triple currentTriple = nextTriple;
        parseNext();
        if (currentTriple != null) {
            return currentTriple;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove triples, this is read only.");
    }

    private void parseNext() {
        String line = getLine();
        Triple triple = null;
        while (line != null && triple == null) {
            triple = parseLine(line, triple);
            if (triple == null) {
                line = getLine();
            }
        }
        nextTriple = triple;
    }

    public boolean close() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private String getLine() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Triple parseLine(String line, Triple triple) {
        Matcher tripleRegexMatcher;
        if (!COMMENT_REGEX.matcher(line).matches()) {
            tripleRegexMatcher = TRIPLE_REGEX.matcher(line);
            if (tripleRegexMatcher.matches()) {
                triple = parseTriple(tripleRegexMatcher);
            }
        }
        return triple;
    }

    private Triple parseTriple(Matcher tripleRegexMatcher) {
        try {
            SubjectNode subject = subjectParser.parseSubject(tripleRegexMatcher);
            PredicateNode predicate = predicateParser.parsePredicate(tripleRegexMatcher);
            ObjectNode object = objectParser.parseObject(tripleRegexMatcher);
            if (subject != null && predicate != null && object != null) {
                return factory.createTriple(subject, predicate, object);
            } else {
                // This is an error.
                return null;
            }
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (TripleFactoryException e) {
            throw new RuntimeException(e);
        }
    }
}
