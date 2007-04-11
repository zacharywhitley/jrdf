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

package org.jrdf.parser.hadoop;

import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.parser.ParserBlankNodeFactory;

import java.io.File;
import java.io.IOException;

public class ParserBlankNodeFactoryImplIntegrationTest extends TestCase {
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String TEST_DIR = TMP_DIR + File.separator +  "set";
    private Graph newGraph;

    public void setUp() throws IOException {
        File dir = new File(TEST_DIR);
        if (dir.exists()) {
            FileUtil.fullyDelete(dir);
        }
    }

    public void testCreateBlankNode() throws IOException, GraphElementFactoryException {
        newGraph = TestJRDFFactory.getFactory().getNewGraph();
        GraphElementFactory graphElementFactory = newGraph.getElementFactory();
        ParserBlankNodeFactory blankNodeFactory = new ParserBlankNodeFactoryImpl(graphElementFactory,
            new Configuration(), TEST_DIR);
        BlankNode blankNode1 = blankNodeFactory.createBlankNode("hello");
        BlankNode blankNode2 = blankNodeFactory.createBlankNode("hello");
        assertEquals(blankNode1, blankNode2);
        for (int i = 0; i < 1000; i++) {
            blankNodeFactory.createBlankNode("hello"+i);
        }
        //blankNodeFactory.close();
        //blankNodeFactory.clear();
    }
}
