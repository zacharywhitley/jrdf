/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.example;

import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.collection.MemMapFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.parser.Parser;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.util.ClosableIterable;
import static org.jrdf.util.EscapeURL.toEscapedString;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * A simple example of parsing in a RDF/XML file into an in memory JRDF graph.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class RdfXmlParserExample {
    private static final JRDFFactory JRDF_FACTORY = SortedMemoryJRDFFactory.getFactory();
    private static final String DEFAULT_RDF_URL = "http://rss.slashdot.org/Slashdot/slashdot";

    // file:///temp/java/
//    LongIndex[] indexes = new LongIndex[]{new LongIndexMem(), new LongIndexMem(), new LongIndexMem()};
//    NodePool nodePool = new JeNodePoolFactory().createNewNodePool();
//    NodeComparator comparator = new NodeComparatorImpl(new NodeTypeComparatorImpl());
//    GraphFactory factory = new SortedResultsGraphFactory(indexes, nodePool, comparator);
//    Parser parser = new RdfXmlParser(factory.getGraph().getElementFactory());
//    parser.parse(in, EscapeURL.toEscapedString(url));

    public static void main(String[] args) throws Exception {
        URL url = getDocumentURL(args);
        InputStream in = getInputStream(url);
        try {
            final Graph jrdfMem = JRDF_FACTORY.getGraph();
            Parser parser = new GraphRdfXmlParser(jrdfMem, new MemMapFactory());
            parser.parse(in, toEscapedString(url));
            ClosableIterable<Triple> triples = jrdfMem.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            try {
                for (Triple triple : triples) {
                    System.out.println("Graph: " + triple);
                }
                System.out.println("Total number of statements: " + jrdfMem.getNumberOfTriples());
            } finally {
                triples.iterator().close();
            }
        } finally {
            in.close();
        }
    }

    private static InputStream getInputStream(URL url) throws Exception {
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        urlConnection.connect();
        String encoding = urlConnection.getContentEncoding();
        InputStream in;
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            in = new GZIPInputStream(urlConnection.getInputStream());
        } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
            in = new InflaterInputStream(urlConnection.getInputStream(), new Inflater(true));
        } else {
            in = urlConnection.getInputStream();
        }
        return in;
    }

    private static URL getDocumentURL(String[] args) throws MalformedURLException {
        String baseURL;
        if (args.length == 0 || args[0].length() == 0) {
            System.out.println("First argument empty so using: " + DEFAULT_RDF_URL);
            baseURL = DEFAULT_RDF_URL;
        } else {
            baseURL = args[0];
        }
        return new URL(baseURL);
    }
}
