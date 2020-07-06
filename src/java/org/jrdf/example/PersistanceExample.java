/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

import org.jrdf.PersistentJRDFFactory;
import org.jrdf.PersistentJRDFFactoryImpl;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Resource;
import org.jrdf.graph.GraphException;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;

import java.net.URI;

public class PersistanceExample {
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final PersistentJRDFFactory JRDF_FACTORY = PersistentJRDFFactoryImpl.getFactory(HANDLER);

    public static void main(String[] args) throws Exception {
        if (!JRDF_FACTORY.hasGraph("foo") || !JRDF_FACTORY.hasGraph("bar")) {
            HANDLER.removeDir();
            HANDLER.makeDir();
            createNewTriples();
        }
        getExistingTriples();
    }

    private static void getExistingTriples() throws GraphException {
        printOutTriples("foo");
        printOutTriples("bar");
    }

    private static void printOutTriples(String name) throws GraphException {
        Graph existingGraph = JRDF_FACTORY.getExistingGraph(name);
        long noTriples = existingGraph.getNumberOfTriples();
        System.out.println("Existing graph recovered " + name + ", found " + noTriples);
        System.out.println("Got: " + existingGraph);
    }

    private static void createNewTriples() throws GraphException {
        System.out.println("Creating new graphs");
        Graph fooGraph = JRDF_FACTORY.getNewGraph("foo");
        Graph barGraph = JRDF_FACTORY.getNewGraph("bar");
        GraphElementFactory barElementFactory = barGraph.getElementFactory();
        URI uri1 = URI.create("urn:hello");
        URI uri2 = URI.create("urn:there");
        Resource resource = barElementFactory.createResource(uri1);
        resource.addValue(uri1, uri2);
        resource.addValue(uri2, uri2);
        GraphElementFactory fooElementFactory = fooGraph.getElementFactory();
        Resource blankResource = fooElementFactory.createResource();
        blankResource.addValue(uri1, uri2);
        blankResource.addValue(uri1, uri1);
        blankResource.addValue(uri2, uri2);
    }
}
