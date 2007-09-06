package org.jrdf.graph.molecule;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactoryImpl;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.mem.BlankNodeComparator;
import org.jrdf.graph.mem.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.mem.LocalizedNodeComparator;
import org.jrdf.graph.mem.LocalizedNodeComparatorImpl;
import org.jrdf.graph.mem.NodeComparatorImpl;
import org.jrdf.graph.mem.TripleComparatorImpl;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.net.URI;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

public class MoleculeComparatorImplUnitTest extends TestCase {
    private static final URI uri1 = URI.create("urn:foo");
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

    @Override
    public void setUp() throws Exception {
        ref1 = elementFactory1.createURIReference(uri1);
        ref2 = elementFactory2.createURIReference(uri1);
    }

    public void testBlankSubject() throws Exception {
        Triple triple1 = tripleFactory1.createTriple(elementFactory1.createBlankNode(), ref1, ref1);
        Triple triple2 = tripleFactory2.createTriple(elementFactory2.createBlankNode(), ref2, ref2);
        newGraph1.add(triple1);
        newGraph2.add(triple2);
        checkEqualMolecules(triple1, triple2);
    }

    public void testBlankObject() throws Exception {
        Triple triple1 = tripleFactory1.createTriple(ref1, ref1, elementFactory1.createBlankNode());
        Triple triple2 = tripleFactory2.createTriple(ref2, ref2, elementFactory2.createBlankNode());
        newGraph1.add(triple1);
        newGraph2.add(triple2);
        checkEqualMolecules(triple1, triple2);
    }

    public void testBothBlank() throws Exception {
        Triple triple1 = tripleFactory1.createTriple(elementFactory1.createBlankNode(), ref1,
            elementFactory1.createBlankNode());
        Triple triple2 = tripleFactory2.createTriple(elementFactory2.createBlankNode(), ref2,
            elementFactory2.createBlankNode());
        newGraph1.add(triple1);
        newGraph2.add(triple2);
        checkEqualMolecules(triple1, triple2);
    }

    public void testDifferentSubjectObject() throws Exception {
        Triple triple1 = tripleFactory1.createTriple(ref1, ref1, elementFactory1.createBlankNode());
        Triple triple2 = tripleFactory2.createTriple(elementFactory2.createBlankNode(), ref2, ref2);
        newGraph1.add(triple1);
        newGraph2.add(triple2);
        checkUnequalMolecules(triple1, triple2);
    }

    private void checkEqualMolecules(Triple triple1, Triple triple2) throws GraphException {
        checkMoleculesAreEqual(createSet(triple1), createSet(triple2));
    }

    private void checkUnequalMolecules(Triple triple1, Triple triple2) throws GraphException {
        checkMoleculesAreUnequal(createSet(triple1), createSet(triple2));
    }

    private void checkMoleculesAreEqual(Set<Triple> triple1, Set<Triple> triple2) throws GraphException {
        Molecule molecule1 = new MoleculeImpl(comparator);
        molecule1.add(triple1);
        Molecule molecule2 = new MoleculeImpl(comparator);
        molecule2.add(triple2);
        MoleculeComparator moleculeComparator = new MoleculeComparatorImpl();
        assertTrue(moleculeComparator.compare(molecule1, molecule2));
    }

    private void checkMoleculesAreUnequal(Set<Triple> triple1, Set<Triple> triple2) throws GraphException {
        Molecule molecule1 = new MoleculeImpl(comparator);
        molecule1.add(triple1);
        Molecule molecule2 = new MoleculeImpl(comparator);
        molecule2.add(triple2);
        MoleculeComparator moleculeComparator = new MoleculeComparatorImpl();
        assertFalse(moleculeComparator.compare(molecule1, molecule2));
    }

    private HashSet<Triple> createSet(Triple... triples) {
        return new HashSet<Triple>(asList(triples));
    }
}
