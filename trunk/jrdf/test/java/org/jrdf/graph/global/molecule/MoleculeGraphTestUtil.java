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

import org.jrdf.GlobalJRDFFactory;
import org.jrdf.SortedDiskGlobalJRDFFactory;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;

import java.net.URI;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class MoleculeGraphTestUtil {
    public static GlobalJRDFFactory FACTORY;
    public static TripleComparator COMPARATOR, GLOBAL_COMPARATOR;
    public static MoleculeComparator MOLECULE_COMPARATOR, GLOBAL_MOLECULE_COMPARATOR;
    public static MoleculeFactory MOLECULE_FACTORY;

    public static MoleculeGraph GRAPH;
    public static GraphElementFactory ELEMENT_FACTORY;
    public static TripleFactory TRIPLE_FACTORY;
    public static BlankNode BNODE1;
    public static BlankNode BNODE2;
    public static BlankNode BNODE3;


    public static Triple B3R2R2;
    public static Triple B3R2R3;
    public static Triple B2R2B3;
    public static Triple B2R2R1;
    public static Triple R1R2B2;
    public static Triple B1R1B2;
    public static Triple B1R2R2;
    public static Triple B1R1R1;

    public static URIReference REF1;
    public static URIReference REF2;
    public static URIReference REF3;

    public static void setUp() throws GraphElementFactoryException {
        COMPARATOR = new TripleComparatorFactoryImpl().newComparator();
        GLOBAL_COMPARATOR = new GroundedTripleComparatorFactoryImpl().newComparator();
        GLOBAL_MOLECULE_COMPARATOR = new MoleculeHeadTripleComparatorImpl(GLOBAL_COMPARATOR);

        MOLECULE_COMPARATOR = new MoleculeHeadTripleComparatorImpl(COMPARATOR);
        MOLECULE_FACTORY = new MoleculeFactoryImpl(MOLECULE_COMPARATOR);

        new TempDirectoryHandler().removeDir();
        FACTORY = SortedDiskGlobalJRDFFactory.getFactory();
        GRAPH = FACTORY.getGraph();
        GRAPH.clear();
        ELEMENT_FACTORY = GRAPH.getElementFactory();
        TRIPLE_FACTORY = GRAPH.getTripleFactory();

        REF1 = ELEMENT_FACTORY.createURIReference(URI.create("urn:ref1"));
        REF2 = ELEMENT_FACTORY.createURIReference(URI.create("urn:ref2"));
        REF3 = ELEMENT_FACTORY.createURIReference(URI.create("urn:ref3"));

        BNODE1 = ELEMENT_FACTORY.createBlankNode();
        BNODE2 = ELEMENT_FACTORY.createBlankNode();
        BNODE3 = ELEMENT_FACTORY.createBlankNode();

        B1R1R1 = TRIPLE_FACTORY.createTriple(BNODE1, REF1, REF1);
        B1R2R2 = TRIPLE_FACTORY.createTriple(BNODE1, REF2, REF2);
        B1R1B2 = TRIPLE_FACTORY.createTriple(BNODE1, REF1, BNODE2);
        R1R2B2 = TRIPLE_FACTORY.createTriple(REF1, REF2, BNODE2);
        B2R2R1 = TRIPLE_FACTORY.createTriple(BNODE2, REF2, REF1);
        B2R2B3 = TRIPLE_FACTORY.createTriple(BNODE2, REF2, BNODE3);
        B3R2R3 = TRIPLE_FACTORY.createTriple(BNODE3, REF2, REF3);
        B3R2R2 = TRIPLE_FACTORY.createTriple(BNODE3, REF2, REF2);
    }

    public static void close() {
        GRAPH.clear();
        GRAPH.close();
        FACTORY.close();
    }
}
