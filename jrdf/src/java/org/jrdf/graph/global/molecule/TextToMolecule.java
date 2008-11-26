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

package org.jrdf.graph.global.molecule;

import org.jrdf.graph.Triple;
import static org.jrdf.parser.ntriples.NTriplesEventReader.TRIPLE_REGEX;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Parses a string represetation of a molecule that follows basic NTriples escaping and turns it into a Molecule object.
 * This is to be used primarily for serialization or similar purposes.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class TextToMolecule {
    private static final Pattern START_MOLECULE = Pattern.compile("\\p{Blank}*\\[\\p{Blank}*");
    private static final Pattern END_MOLECULE = Pattern.compile("\\p{Blank}*\\]\\p{Blank}*");
    private final RegexMatcherFactory regexMatcherFactory;
    private final TripleParser tripleParser;
    private final MoleculeFactory moleculeFactory;
    private LineNumberReader bufferedReader;
    private Molecule currentMolecule;
    private Triple currentTriple;
    private Stack<Molecule> parentMolecules;
    private Stack<Triple> parentTriples;

    public TextToMolecule(final RegexMatcherFactory newRegexFactory, final TripleParser newTripleParser,
        final MoleculeFactory moleculeFactory) {
        this.regexMatcherFactory = newRegexFactory;
        this.tripleParser = newTripleParser;
        this.moleculeFactory = moleculeFactory;
    }

    public Molecule parse(InputStream in) {
        return parse(new InputStreamReader(in));
    }

    public Molecule parse(Reader reader) {
        reset();
        this.bufferedReader = new LineNumberReader(reader);
        parseNext();
        return currentMolecule;
    }

    private void reset() {
        this.parentMolecules = new Stack<Molecule>();
        this.parentTriples = new Stack<Triple>();
        currentMolecule = null;
        currentTriple = null;
        tripleParser.clear();
    }

    private void parseNext() {
        String line = getLine();
        while (line != null) {
            parseLine(line);
            line = getLine();
        }
    }

    private String getLine() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseLine(String line) {
        final RegexMatcher startMolecule = regexMatcherFactory.createMatcher(START_MOLECULE, line);
        if (startMolecule.matches()) {
            handleStartMolecule();
        } else {
            final RegexMatcher tripleMatcher = regexMatcherFactory.createMatcher(TRIPLE_REGEX, line);
            if (tripleMatcher.matches()) {
                handleTriple(tripleMatcher, line);
            } else {
                final RegexMatcher endMolecule = regexMatcherFactory.createMatcher(END_MOLECULE, line);
                if (endMolecule.matches()) {
                    handleEndMolecule();
                }
            }
        }
    }

    private void handleStartMolecule() {
        if (currentMolecule != null) {
            parentMolecules.push(currentMolecule);
        }
        if (currentTriple != null) {
            parentTriples.push(currentTriple);
        }
        currentMolecule = moleculeFactory.createMolecule();
    }

    private void handleTriple(RegexMatcher tripleMatcher, CharSequence line) {
        currentTriple = tripleParser.parseTriple(tripleMatcher, line);
        currentMolecule.add(currentTriple);
    }

    private void handleEndMolecule() {
        if (!parentMolecules.isEmpty()) {
            Molecule subMolecule = currentMolecule;
            Triple subMoleculeTriple = parentTriples.pop();
            currentMolecule = parentMolecules.pop();
            currentMolecule.add(subMoleculeTriple, subMolecule);
        }
    }
}