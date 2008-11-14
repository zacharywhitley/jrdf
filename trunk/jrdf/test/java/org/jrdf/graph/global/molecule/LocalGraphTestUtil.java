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

import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;

import java.net.URI;

public class LocalGraphTestUtil {
    private static final JRDFFactory FACTORY = SortedMemoryJRDFFactory.getFactory();
    public static final Graph GRAPH = FACTORY.getGraph();
    private static final GraphElementFactory ELEMENT_FACTORY = GRAPH.getElementFactory();
    private static final TripleFactory TRIPLE_FACTORY = GRAPH.getTripleFactory();
    private static final TripleComparator TRIPLE_COMPARATOR = new TripleComparatorFactoryImpl().newComparator();
    private static final MoleculeComparator MOLECULE_COMPARATOR =
            new MoleculeHeadTripleComparatorImpl(TRIPLE_COMPARATOR);
    public static final MoleculeFactory MOLECULE_FACTORY = new MoleculeFactoryImpl(MOLECULE_COMPARATOR);
    public static final URIReference REF1;
    public static final URIReference REF2;
    public static final URIReference REF3;
    public static final BlankNode BNODE1;
    public static final BlankNode BNODE2;
    public static final BlankNode BNODE3;
    public static final BlankNode BNODE4;
    public static final BlankNode BNODE5;
    public static final BlankNode BNODE6;
    public static final BlankNode BNODE7;
    public static final BlankNode BNODE8;
    public static final Triple R1R1R1;
    public static final Triple R1R1B1;
    public static final Triple R1R1B3;
    public static final Triple R1R2B1;
    public static final Triple R1R1B2;
    public static final Triple R1R2B2;
    public static final Triple R2R1R1;
    public static final Triple R2R1R2;
    public static final Triple R2R1B1;
    public static final Triple R2R1B2;
    public static final Triple R2R2B1;
    public static final Triple R2R2B2;
    public static final Triple B1R1B1;
    public static final Triple B1R1R1;
    public static final Triple B1R1R1_2;
    public static final Triple B1R2R2;
    public static final Triple B1R3R2;
    public static final Triple B1R1B2;
    public static final Triple B1R1B3;
    public static final Triple B2R1R1;
    public static final Triple B2R2R1;
    public static final Triple B2R1B1;
    public static final Triple B2R1B3;
    public static final Triple B1R2B2;
    public static final Triple B1R2B3;
    public static final Triple B2R2R2;
    public static final Triple B2R2B3;
    public static final Triple B3R1R1;
    public static final Triple B3R1B4;
    public static final Triple B3R2R2;
    public static final Triple B3R2R3;
    public static final Triple B3R3B1;
    public static final Triple B3R3B2;
    public static final Triple B3R3B4;
    public static final Triple B3R3R3;
    public static final Triple B4R1B1;
    public static final Triple B4R2B2;
    public static final Triple B4R2B3;
    public static final Triple B5R2B3;
    public static final Triple B5R1B4;
    public static final Triple B5R1B6;
    public static final Triple B6R1B7;
    public static final Triple B7R1B8;
    public static final Triple B7R2B8;


    static {
        try {
            REF1 = ELEMENT_FACTORY.createURIReference(URI.create("urn:foo"));
            REF2 = ELEMENT_FACTORY.createURIReference(URI.create("urn:bar"));
            REF3 = ELEMENT_FACTORY.createURIReference(URI.create("urn:baz"));
            BNODE1 = ELEMENT_FACTORY.createBlankNode();
            BNODE2 = ELEMENT_FACTORY.createBlankNode();
            BNODE3 = ELEMENT_FACTORY.createBlankNode();
            BNODE4 = ELEMENT_FACTORY.createBlankNode();
            BNODE5 = ELEMENT_FACTORY.createBlankNode();
            BNODE6 = ELEMENT_FACTORY.createBlankNode();
            BNODE7 = ELEMENT_FACTORY.createBlankNode();
            BNODE8 = ELEMENT_FACTORY.createBlankNode();
            R1R1R1 = TRIPLE_FACTORY.createTriple(REF1, REF1, REF1);
            R1R1B1 = TRIPLE_FACTORY.createTriple(REF1, REF1, BNODE1);
            R1R1B2 = TRIPLE_FACTORY.createTriple(REF1, REF1, BNODE2);
            R1R2B1 = TRIPLE_FACTORY.createTriple(REF1, REF2, BNODE1);
            R1R2B2 = TRIPLE_FACTORY.createTriple(REF1, REF2, BNODE2);
            R2R1R1 = TRIPLE_FACTORY.createTriple(REF2, REF1, REF1);
            R2R1R2 = TRIPLE_FACTORY.createTriple(REF2, REF1, REF2);
            R2R1B1 = TRIPLE_FACTORY.createTriple(REF2, REF1, BNODE1);
            R2R1B2 = TRIPLE_FACTORY.createTriple(REF2, REF1, BNODE2);
            R2R2B1 = TRIPLE_FACTORY.createTriple(REF2, REF2, BNODE1);
            R2R2B2 = TRIPLE_FACTORY.createTriple(REF2, REF2, BNODE2);
            B1R1B1 = TRIPLE_FACTORY.createTriple(BNODE1, REF1, BNODE1);
            R1R1B3 = TRIPLE_FACTORY.createTriple(REF1, REF1, BNODE3);
            B1R1R1 = TRIPLE_FACTORY.createTriple(BNODE1, REF1, REF1);
            B1R1R1_2 = TRIPLE_FACTORY.createTriple(BNODE1, REF1, REF1);
            B1R2R2 = TRIPLE_FACTORY.createTriple(BNODE1, REF2, REF2);
            B1R1B2 = TRIPLE_FACTORY.createTriple(BNODE1, REF1, BNODE2);
            B1R2B2 = TRIPLE_FACTORY.createTriple(BNODE1, REF2, BNODE2);
            B1R1B3 = TRIPLE_FACTORY.createTriple(BNODE1, REF1, BNODE3);
            B1R2B3 = TRIPLE_FACTORY.createTriple(BNODE1, REF2, BNODE3);
            B1R3R2 = TRIPLE_FACTORY.createTriple(BNODE1, REF3, REF2);
            B2R1R1 = TRIPLE_FACTORY.createTriple(BNODE2, REF1, REF1);
            B2R2R1 = TRIPLE_FACTORY.createTriple(BNODE2, REF2, REF1);
            B2R1B1 = TRIPLE_FACTORY.createTriple(BNODE2, REF1, BNODE1);
            B2R1B3 = TRIPLE_FACTORY.createTriple(BNODE2, REF1, BNODE3);
            B2R2R2 = TRIPLE_FACTORY.createTriple(BNODE2, REF2, REF2);
            B2R2B3 = TRIPLE_FACTORY.createTriple(BNODE2, REF2, BNODE3);
            B3R1R1 = TRIPLE_FACTORY.createTriple(BNODE3, REF1, REF1);
            B3R2R2 = TRIPLE_FACTORY.createTriple(BNODE3, REF2, REF2);
            B3R2R3 = TRIPLE_FACTORY.createTriple(BNODE3, REF2, REF3);
            B3R3B1 = TRIPLE_FACTORY.createTriple(BNODE3, REF3, BNODE1);
            B3R1B4 = TRIPLE_FACTORY.createTriple(BNODE3, REF1, BNODE4);
            B4R1B1 = TRIPLE_FACTORY.createTriple(BNODE4, REF1, BNODE1);
            B3R3B2 = TRIPLE_FACTORY.createTriple(BNODE3, REF3, BNODE2);
            B3R3B4 = TRIPLE_FACTORY.createTriple(BNODE3, REF3, BNODE4);
            B3R3R3 = TRIPLE_FACTORY.createTriple(BNODE3, REF3, REF3);
            B4R2B2 = TRIPLE_FACTORY.createTriple(BNODE4, REF2, BNODE2);
            B4R2B3 = TRIPLE_FACTORY.createTriple(BNODE4, REF2, BNODE3);
            B5R2B3 = TRIPLE_FACTORY.createTriple(BNODE5, REF2, BNODE3);
            B5R1B4 = TRIPLE_FACTORY.createTriple(BNODE5, REF1, BNODE4);
            B5R1B6 = TRIPLE_FACTORY.createTriple(BNODE5, REF1, BNODE6);
            B6R1B7 = TRIPLE_FACTORY.createTriple(BNODE6, REF1, BNODE7);
            B7R1B8 = TRIPLE_FACTORY.createTriple(BNODE7, REF1, BNODE8);
            B7R2B8 = TRIPLE_FACTORY.createTriple(BNODE7, REF2, BNODE8);
        } catch (GraphElementFactoryException e) {
            throw new ExceptionInInitializerError("Failed to create required resources");
        }
    }

    private LocalGraphTestUtil() {
    }
}