/*
 * $Header$
 * $Revision: 442 $
 * $Date: 2006-01-29 14:41:23 +1000 (Sun, 29 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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
package org.jrdf.writer.rdfxml;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.index.operation.mem.ComparisonImpl;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.rdfxml.RdfXmlParser;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

public class RdfXmlWriterIntegrationTest extends TestCase {

    private static final String GROUNDED = "org/jrdf/writer/rdfxml/data/rdf/grounded.rdf";
    private static final String UNGROUNDED = "org/jrdf/writer/rdfxml/data/rdf/ungrounded.rdf";

    public void testReadWriteGrounded() throws Exception {
        ComparisonImpl comparison = new ComparisonImpl();
        // input
        Graph graph = readGraph(GROUNDED);
        assertTrue("Test data is invalid. Input graph should be grounded.", comparison.isGrounded(graph));
        // output
        StringWriter out = new StringWriter();
        // do write
        try {
            RdfXmlWriter writer = new RdfXmlWriter();
            writer.write(graph, out);
        } finally {
            out.close();
        }
        // re-read
        Graph read = JRDFFactory.getNewGraph();
        StringReader reader = new StringReader(out.toString());
        RdfXmlParser parser = new RdfXmlParser(read.getElementFactory());
        try {
            parser.parse(reader, "http://www.example.org/");
        } catch (ParseException e) {
            e.printStackTrace();
            fail("Output could not be parsed [" + e.getLineNumber() + ":" + e.getColumnNumber() + "]: " + e);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Output could not be parsed: " + e);
        }

        // compare
        assertTrue("Output graph should be grounded", comparison .isGrounded(read));
//        assertTrue("Output graph is not equal to input graph.", comparison.areIsomorphic(graph, read));
    }

    public void testReadWriteUngrounded() throws Exception {
//        ComparisonImpl comparison = new ComparisonImpl();
        // input
        Graph graph = readGraph(UNGROUNDED);
//        assertFalse("Test data is invalid. Input graph should not be grounded.", comparison.isGrounded(graph));
        // output
        StringWriter out = new StringWriter();
        // do write
        try {
            RdfXmlWriter writer = new RdfXmlWriter();
            writer.write(graph, out);
        } finally {
            out.close();
        }
        // re-read
        Graph read = JRDFFactory.getNewGraph();
        StringReader reader = new StringReader(out.toString());
        RdfXmlParser parser = new RdfXmlParser(read.getElementFactory());
        try {
            parser.parse(reader, "http://www.example.org/");
        } catch (ParseException e) {
            e.printStackTrace();
            fail("Output could not be parsed [" + e.getLineNumber() + ":"
                    + e.getColumnNumber() + "]: " + e);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Output could not be parsed: " + e);
        }
        // compare
//        assertFalse("Output graph should not be grounded", comparison .isGrounded(read));
//        assertTrue("Output graph is not equal to input graph.", comparison.areIsomorphic(graph, read));
    }

    private Graph readGraph(String document) throws Exception {
        URL source = getClass().getClassLoader().getResource(document);
        if (source == null) {
            throw new Exception("Failed to find: " + document);
        }
        Graph read = JRDFFactory.getNewGraph();
        RdfXmlParser parser = new RdfXmlParser(read.getElementFactory());
        parser.parse(source.openStream(), source.toURI().toString());
        return read;
    }

//    // TEST CODE
//    private void write(Graph g) throws Exception {
//        RdfXmlWriter writer = new RdfXmlWriter();
//        writer.write(g, System.out);
//    }
//    // END TEST

}
