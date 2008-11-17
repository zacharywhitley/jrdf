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

import junit.framework.TestCase;
import org.jrdf.GlobalJRDFFactory;
import org.jrdf.SortedDiskGlobalJRDFFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;
import org.jrdf.util.TempDirectoryHandler;

import java.net.URI;

public abstract class AbstractMoleculeGraphIntegrationTest extends TestCase {
    protected GlobalJRDFFactory factory;
    protected MoleculeFactory moleculeFactory;
    protected MoleculeGraph graph;
    protected MoleculeComparator moleculeComparator;
    protected MoleculeComparator globalMoleculeComparator;
    protected URIReference ref1;
    protected BlankNode bnode2;
    protected Triple r1r2b2;
    protected Triple b1r1r1;
    protected Triple b1r1b2;
    protected Triple b1r2r2;
    protected Triple b2r2r1;
    protected Triple b2r2b3;
    protected Triple b3r2r2;
    protected Triple b3r2r3;

    public void setUp() throws Exception {
        final TripleComparator comparator = new TripleComparatorFactoryImpl().newComparator();
        moleculeComparator = new MoleculeHeadTripleComparatorImpl(comparator);
        moleculeFactory = new MoleculeFactoryImpl(moleculeComparator);

        final TripleComparator globalComparator = new GroundedTripleComparatorFactoryImpl().newComparator();
        globalMoleculeComparator = new MoleculeHeadTripleComparatorImpl(globalComparator);

        new TempDirectoryHandler().removeDir();
        factory = SortedDiskGlobalJRDFFactory.getFactory();
        graph = factory.getGraph();
        graph.clear();
        final GraphElementFactory elementFactory = graph.getElementFactory();
        final TripleFactory tripleFactory = graph.getTripleFactory();

        ref1 = elementFactory.createURIReference(URI.create("urn:ref1"));
        final URIReference ref2 = elementFactory.createURIReference(URI.create("urn:ref2"));
        final URIReference ref3 = elementFactory.createURIReference(URI.create("urn:ref3"));

        final BlankNode bnode1 = elementFactory.createBlankNode();
        bnode2 = elementFactory.createBlankNode();
        final BlankNode bnode3 = elementFactory.createBlankNode();

        b1r1r1 = tripleFactory.createTriple(bnode1, ref1, ref1);
        b1r2r2 = tripleFactory.createTriple(bnode1, ref2, ref2);
        b1r1b2 = tripleFactory.createTriple(bnode1, ref1, bnode2);
        r1r2b2 = tripleFactory.createTriple(ref1, ref2, bnode2);
        b2r2r1 = tripleFactory.createTriple(bnode2, ref2, ref1);
        b2r2b3 = tripleFactory.createTriple(bnode2, ref2, bnode3);
        b3r2r3 = tripleFactory.createTriple(bnode3, ref2, ref3);
        b3r2r2 = tripleFactory.createTriple(bnode3, ref2, ref2);
    }

    public void tearDown() {
        graph.clear();
        graph.close();
        factory.close();
    }
}
