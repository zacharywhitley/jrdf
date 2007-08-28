package org.jrdf.graph.molecule;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactoryImpl;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.Triple;

import java.net.URI;

public class MoleculeComparatorImplUnitTest extends TestCase {
    private JRDFFactory factory1 = SortedMemoryJRDFFactoryImpl.getFactory();
    private JRDFFactory factory2 = SortedMemoryJRDFFactoryImpl.getFactory();
    private Graph newGraph1 = factory1.getNewGraph();
    private Graph newGraph2 = factory2.getNewGraph();
    private GraphElementFactory elementFactory1 = newGraph1.getElementFactory();
    private GraphElementFactory elementFactory2 = newGraph2.getElementFactory();
    private TripleFactory tripleFactory1 = newGraph1.getTripleFactory();
    private TripleFactory tripleFactory2 = newGraph2.getTripleFactory();
    private URI uri1 = URI.create("urn:foo");

    public void testSame() throws Exception {
        URIReference ref1 = elementFactory1.createURIReference(uri1);
        URIReference ref2 = elementFactory2.createURIReference(uri1);
        Triple triple1 = tripleFactory1.createTriple(ref1, ref1, elementFactory1.createBlankNode());
        Triple triple2 = tripleFactory2.createTriple(ref2, ref2, elementFactory2.createBlankNode());
        newGraph1.add(triple1);
        newGraph2.add(triple2);
        Molecule molecule1 = new MoleculeImpl(newGraph1, triple1);
        Molecule molecule2 = new MoleculeImpl(newGraph2, triple2);
        MoleculeComparator moleculeComparator = new MoleculeComparatorImpl();
        assertTrue(moleculeComparator.compare(molecule1, molecule2));
    }
}
