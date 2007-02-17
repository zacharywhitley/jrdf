/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.graph.Literal;
import org.jrdf.parser.GraphStatementHandler;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.mem.ParserBlankNodeFactoryImpl;
import org.jrdf.util.ClosableIterator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NTripleIntegrationTest extends TestCase {
    private static final String TEST_DATA = "org/jrdf/parser/ntriples/test.nt";

    public void testParseFile() throws Exception {
        InputStream in = getSampleDate();
        TestJRDFFactory testJRDFFactory = TestJRDFFactory.getFactory();
        Graph newGraph = testJRDFFactory.getNewGraph();
        ParserBlankNodeFactory blankNodeFactory = new ParserBlankNodeFactoryImpl(newGraph.getElementFactory());
        ParserFactory factory = new ParserFactoryImpl();
        NTripleParser nTripleParser = factory.createParser(newGraph.getElementFactory(), blankNodeFactory);
        GraphStatementHandler statementHandler = new GraphStatementHandler(newGraph);
        nTripleParser.setStatementHandler(statementHandler);
        nTripleParser.parse(in, "foo");
        ClosableIterator<Triple> closableIterator = newGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE,
            ANY_OBJECT_NODE);
        try {
            while (closableIterator.hasNext()) {
                Triple triple = closableIterator.next();
                if (triple.getObject() instanceof Literal) {
                    System.err.println("Real value: " + ((Literal) triple.getObject()).getLexicalForm());
                }
                System.err.println("Got: " + triple);
            }
        } finally {
            closableIterator.close();
        }
    }

    public InputStream getSampleDate() throws IOException {
        URL source = getClass().getClassLoader().getResource(TEST_DATA);
        return source.openStream();
    }
}
