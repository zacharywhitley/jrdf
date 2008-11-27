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

package org.jrdf.parser.ntriples;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleImpl;
import org.jrdf.parser.RDFEventReader;
import org.jrdf.parser.StatementHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class RegexEventReader implements RDFEventReader, StatementHandler {
    private static final Pattern COMMENT_REGEX = Pattern.compile("\\p{Blank}*#([\\x20-\\x7E[^\\n\\r]])*");
    private final LineNumberReader bufferedReader;
    private final URI baseURI;
    private final TriplesParser parser;
    private Triple currentTriple;
    private Triple nextTriple;

    public RegexEventReader(final InputStream in, final URI newBaseURI, final TriplesParser newParser) {
        this(new InputStreamReader(in), newBaseURI, newParser);
    }

    public RegexEventReader(final Reader reader, final URI newBaseURI, final TriplesParser newParser) {
        bufferedReader = new LineNumberReader(reader);
        baseURI = newBaseURI;
        parser = newParser;
        parser.setStatementHandler(this);
        parseNext();
    }

    public boolean hasNext() {
        return nextTriple != null;
    }

    public Triple next() {
        final Triple currentTriple = nextTriple;
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
        currentTriple = null;
        while (line != null && currentTriple == null) {
            parseLine(line);
            if (currentTriple == null) {
                line = getLine();
            }
        }
        nextTriple = currentTriple;
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

    public void handleStatement(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        currentTriple = new TripleImpl(subject, predicate, object);
    }

    private void parseLine(CharSequence line) {
        if (!COMMENT_REGEX.matcher(line).matches()) {
            parser.handleTriple(line);
        }
    }
}