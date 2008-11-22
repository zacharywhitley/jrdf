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

package org.jrdf.parser.n3;

import org.jrdf.graph.Triple;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

// TODO N3 Changes - Copy of NTriplesEventReader.
public class N3EventReader implements RDFEventReader {
    /**
     * A regular expression for NTriples.
     */
    public static final Pattern TRIPLE_REGEX = Pattern.compile("\\p{Blank}*" +
                    "(\\<([\\x20-\\x7E]+?)\\>|((\\p{Alpha}\\p{Alnum}*?):(\\p{Alpha}\\p{Alnum}*?))|" +
                    "_:(\\p{Alpha}\\p{Alnum}*?))\\p{Blank}+" +
                    "(\\<([\\x20-\\x7E]+?)\\>|((\\p{Alpha}\\p{Alnum}*?):(\\p{Alpha}\\p{Alnum}*?)))\\p{Blank}+" +
                    "(\\<([\\x20-\\x7E]+?)\\>||((\\p{Alpha}\\p{Alnum}*?):(\\p{Alpha}\\p{Alnum}*?))|" +
                    "_:(\\p{Alpha}\\p{Alnum}*?)|(([\\x20-\\x7E]+?)))\\p{Blank}*" +
                    "\\.\\p{Blank}*");
    private static final Pattern COMMENT_REGEX = Pattern.compile("\\p{Blank}*#([\\x20-\\x7E[^\\n\\r]])*");

    private final LineNumberReader bufferedReader;
    private final URI baseURI;
    private final RegexMatcherFactory regexMatcherFactory;
    private final TripleParser tripleParser;
    private Triple nextTriple;

    public N3EventReader(final InputStream in, final URI newBaseURI, final RegexMatcherFactory newRegexFactory,
        final TripleParser newTripleParser) {
        this(new InputStreamReader(in), newBaseURI, newRegexFactory, newTripleParser);
    }

    public N3EventReader(final Reader reader, final URI newBaseURI, final RegexMatcherFactory newRegexFactory,
        final TripleParser newTripleParser) {
        this.bufferedReader = new LineNumberReader(reader);
        this.baseURI = newBaseURI;
        this.regexMatcherFactory = newRegexFactory;
        this.tripleParser = newTripleParser;
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
            triple = parseLine(line);
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

    private Triple parseLine(String line) {
        RegexMatcher tripleRegexMatcher;
        Triple triple = null;
        if (!COMMENT_REGEX.matcher(line).matches()) {
            tripleRegexMatcher = regexMatcherFactory.createMatcher(TRIPLE_REGEX, line);
            if (tripleRegexMatcher.matches()) {
                triple = tripleParser.parseTriple(tripleRegexMatcher);
            }
        }
        return triple;
    }
}