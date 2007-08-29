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

package org.jrdf.graph.molecule;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactoryImpl;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.Triple;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.mem.NodeComparatorImpl;
import org.jrdf.graph.mem.TripleComparatorImpl;
import org.jrdf.graph.mem.LocalizedNodeComparator;
import org.jrdf.graph.mem.LocalizedNodeComparatorImpl;
import org.jrdf.graph.mem.BlankNodeComparator;
import org.jrdf.graph.mem.LocalizedBlankNodeComparatorImpl;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.net.URI;

public class MoleculeImplUnitTest extends TestCase {
    private JRDFFactory factory1 = SortedMemoryJRDFFactoryImpl.getFactory();
    private JRDFFactory factory2 = SortedMemoryJRDFFactoryImpl.getFactory();
    private Graph newGraph1 = factory1.getNewGraph();
    private Graph newGraph2 = factory2.getNewGraph();
    private GraphElementFactory elementFactory1 = newGraph1.getElementFactory();
    private GraphElementFactory elementFactory2 = newGraph2.getElementFactory();
    private TripleFactory tripleFactory1 = newGraph1.getTripleFactory();
    private TripleFactory tripleFactory2 = newGraph2.getTripleFactory();
    private LocalizedNodeComparator localizedNodeComparator = new LocalizedNodeComparatorImpl();
    private BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localizedNodeComparator);
    private NodeComparator nodeComparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
    private TripleComparator comparator = new TripleComparatorImpl(nodeComparator);
    private URIReference ref1;
    private URIReference ref2;

    public void testMoleculeOrderURIReference() throws Exception {
        Molecule molecule = new MoleculeImpl(comparator);
        ref1 = elementFactory1.createURIReference(URI.create("urn:foo"));
        ref2 = elementFactory1.createURIReference(URI.create("urn:bar"));
        molecule.addTriple(tripleFactory1.createTriple(ref1, ref1, ref1));
        Triple triple = tripleFactory1.createTriple(ref1, ref1, ref2);
        molecule.addTriple(triple);
        assertEquals(triple, molecule.getHeadTriple());
    }

//    public void testMoleculeOrderBlankNodes() throws Exception {
//        Molecule molecule = new MoleculeImpl(comparator);
//        ref1 = elementFactory1.createURIReference(URI.create("urn:foo"));
//        ref2 = elementFactory1.createURIReference(URI.create("urn:bar"));
//        molecule.addTriple(tripleFactory1.createTriple(elementFactory1.createBlankNode(), ref1, ref1));
//        Triple triple = tripleFactory1.createTriple(elementFactory1.createBlankNode(), ref1, ref2);
//        molecule.addTriple(triple);
//        molecule.addTriple(tripleFactory1.createTriple(elementFactory1.createBlankNode(), ref1, elementFactory1.createBlankNode()));
//        assertEquals(triple, molecule.getHeadTriple());
//    }
}
