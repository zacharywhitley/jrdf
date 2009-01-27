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

package org.jrdf.graph.global.index.longindex;

import junit.framework.TestCase;
import org.jrdf.GlobalJRDFFactory;
import org.jrdf.SortedDiskGlobalJRDFFactory;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;
import org.jrdf.vocabulary.RDF;
import org.jrdf.vocabulary.XSD;

import static java.net.URI.create;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class AddMoleculeToIndexIntegrationTest extends TestCase {
    private static final TripleComparator TRIPLE_COMPARATOR = new TripleComparatorFactoryImpl().newComparator();
    private static final MoleculeComparator MOLECULE_COMPARATOR =
        new MoleculeHeadTripleComparatorImpl(TRIPLE_COMPARATOR);
    private GlobalJRDFFactory factory = SortedDiskGlobalJRDFFactory.getFactory();
    private MoleculeFactory molFactory = new MoleculeFactoryImpl(MOLECULE_COMPARATOR);
    private MoleculeGraph graph;
    private GraphElementFactory elementFactory;

    public void setUp() {
        graph = factory.getNewGraph();
        elementFactory = graph.getElementFactory();
    }

    public void tearDown() {
        graph.clear();
    }

    public void testInteractionMolecule() throws GraphException {
        Molecule molecule = molFactory.createMolecule();

        Resource observation = elementFactory.createResource();
        URIReference p1 = elementFactory.createURIReference(create("foo:experimentMethod"));
        Resource method = elementFactory.createResource();
        Triple t1 = observation.asTriple(p1, method);

        URIReference p2 = elementFactory.createURIReference(create("foo:hasLocalID"));
        Literal localId = elementFactory.createLiteral("18004", XSD.STRING);
        Triple t2 = method.asTriple(p2, localId);

        URIReference db = elementFactory.createURIReference(create("foo:db"));
        Literal psimidb = elementFactory.createLiteral("psi-mi", XSD.STRING);
        Triple t3 = method.asTriple(db, psimidb);

        URIReference id = elementFactory.createURIReference(create("foo:id"));
        Literal miid = elementFactory.createLiteral("MI:0019", XSD.STRING);
        Triple t4 = method.asTriple(id, miid);

        URIReference type = elementFactory.createURIReference(RDF.TYPE);
        URIReference emType = elementFactory.createURIReference(create("foo:experimentMethod"));
        Triple t5 = method.asTriple(type, emType);
        Molecule sub1 = molFactory.createMolecule(t2, t3, t4, t5);
        molecule.add(t1, sub1);

        URIReference p5 = elementFactory.createURIReference(create("foo:observedInteraction"));
        Resource interaction = elementFactory.createResource();
        Triple t6 = observation.asTriple(p5, interaction);

        URIReference participant = elementFactory.createURIReference(create("foo:participant"));
        Resource participant1 = elementFactory.createResource();
        Triple t7 = interaction.asTriple(participant, participant1);

        Literal lid1 = elementFactory.createLiteral("18006", XSD.STRING);
        Triple t8 = participant1.asTriple(p2, lid1);

        URIReference physicalEntity = elementFactory.createURIReference(create("foo:physicalEntity"));
        Triple t9 = participant1.asTriple(type, physicalEntity);

        Resource participant2 = elementFactory.createResource();
        Literal lid2 = elementFactory.createLiteral("18008", XSD.STRING);
        Triple t10 = participant2.asTriple(p2, lid2);

        Triple t11 = participant2.asTriple(type, physicalEntity);

        Molecule subSub1 = molFactory.createMolecule(t8, t9, t10, t11);
        //System.err.println(subSub1.toString());
        Molecule sub2 = molFactory.createMolecule();
        sub2.add(t7, subSub1);
        molecule.add(t6, sub2);

        URIReference physicalInteraction = elementFactory.createURIReference(create("foo:physicalInteraction"));
        Triple t12 = interaction.asTriple(type, physicalInteraction);
        sub2.add(t12);

        URIReference referenceForObservation = elementFactory.createURIReference(create("foo:referenceForObservation"));
        Resource rfo = elementFactory.createResource();
        Triple t13 = observation.asTriple(referenceForObservation, rfo);
        molecule.add(t13);

        URIReference aboutLID = elementFactory.createURIReference(create("foo:aboutLocalID"));
        Literal aboutid1 = elementFactory.createLiteral("18004", XSD.INTEGER);
        Triple t14 = rfo.asTriple(aboutLID, aboutid1);

        URIReference link = elementFactory.createURIReference(create("foo:link"));
        Literal linkStr = elementFactory.createLiteral("http");
        Triple t15 = rfo.asTriple(link, linkStr);

        Literal pubmed = elementFactory.createLiteral("pubmed", XSD.STRING);
        Triple t16 = rfo.asTriple(db, pubmed);

        Literal pubmed1 = elementFactory.createLiteral("9412461", XSD.STRING);
        Triple t17 = rfo.asTriple(id, pubmed1);

        URIReference publicationXref = elementFactory.createURIReference(create("foo:publicationXref"));
        Triple t18 = rfo.asTriple(type, publicationXref);

        Molecule sub3 = molFactory.createMolecule(t14, t15, t16, t17, t18);
        molecule.add(t13, sub3);

        URIReference source = elementFactory.createURIReference(create("foo:sourceOfData"));
        Resource source1 = elementFactory.createResource();
        Triple t19 = observation.asTriple(source, source1);

        Literal sDB = elementFactory.createLiteral("mips", XSD.STRING);
        Triple t20 = source1.asTriple(db, sDB);

        Literal sID = elementFactory.createLiteral("41", XSD.STRING);
        Triple t21 = source1.asTriple(id, sID);

        URIReference uXref = elementFactory.createURIReference(create("foo:unificationXref"));
        Triple t22 = source1.asTriple(type, uXref);

        Molecule sub4 = molFactory.createMolecule(t20, t21, t22);
        molecule.add(t19, sub4);

        Literal obDB = elementFactory.createLiteral("http://ebi.ac.uk/", XSD.STRING);
        Triple t23 = observation.asTriple(db, obDB);

        URIReference obType = elementFactory.createURIReference(create("foo:ExperimentalObservation"));
        Triple t24 = observation.asTriple(type, obType);

        molecule.add(t23);
        molecule.add(t24);

        //System.err.println(molecule.toString());
        graph.add(molecule);
    }
}
