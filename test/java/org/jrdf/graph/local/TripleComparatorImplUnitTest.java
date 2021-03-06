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

package org.jrdf.graph.local;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.util.test.ClassPropertiesTestUtil;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.net.URI;

/**
 * TripleComparator unit test
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class TripleComparatorImplUnitTest extends TestCase {
    private JRDFFactory factory = SortedMemoryJRDFFactory.getFactory();
    private Graph newGraph = factory.getGraph();
    private GraphElementFactory elementFactory = newGraph.getElementFactory();
    private TripleFactory tripleFactory = newGraph.getTripleFactory();
    private TripleComparator comparator = new TripleComparatorFactoryImpl().newComparator();
    private URIReference ref1;
    private URIReference ref2;

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(TripleComparator.class,
            TripleComparatorImpl.class);
        ClassPropertiesTestUtil.checkImplementationOfInterface(Serializable.class, TripleComparator.class);
        ClassPropertiesTestUtil.checkConstructor(TripleComparatorImpl.class, Modifier.PUBLIC, NodeComparator.class);
    }

    public void testComparison() throws Exception {
        ref1 = elementFactory.createURIReference(URI.create("urn:foo"));
        ref2 = elementFactory.createURIReference(URI.create("urn:bar"));
        Triple triple1 = tripleFactory.createTriple(elementFactory.createBlankNode(), ref1, ref1);
        Triple triple2 = tripleFactory.createTriple(elementFactory.createBlankNode(), ref1,
            elementFactory.createBlankNode());
        Triple triple3 = tripleFactory.createTriple(elementFactory.createBlankNode(), ref1, ref2);
        Triple triple4 = tripleFactory.createTriple(elementFactory.createBlankNode(), ref1, ref1);
        assertEquals(-1, comparator.compare(triple1, triple4));
        assertEquals(-1, comparator.compare(triple2, triple4));
        assertEquals(1, comparator.compare(triple1, triple2));
        assertEquals(-1, comparator.compare(triple2, triple1));
        assertEquals(1, comparator.compare(triple3, triple1));
        assertEquals(-1, comparator.compare(triple1, triple3));
    }
}
