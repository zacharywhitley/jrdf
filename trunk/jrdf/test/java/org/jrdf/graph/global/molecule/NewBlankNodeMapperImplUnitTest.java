package org.jrdf.graph.global.molecule;

import junit.framework.TestCase;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMultiLevelMolecule;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.*;
import org.jrdf.graph.global.molecule.mem.NewMolecule;
import org.jrdf.graph.global.molecule.mem.NewMoleculeComparator;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactory;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.NewMoleculeHeadTripleComparatorImpl;
import static org.jrdf.util.test.SetUtil.asSet;

import java.util.Collections;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: 6/05/2008
 * Time: 15:17:17
 * To change this template use File | Settings | File Templates.
 */
public class NewBlankNodeMapperImplUnitTest extends TestCase {
    private final TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private final NewMoleculeComparator moleculeComparator = new NewMoleculeHeadTripleComparatorImpl(comparator);
    private final NewMoleculeFactory moleculeFactory = new NewMoleculeFactoryImpl(moleculeComparator);
    private BlankNodeMapper mapper;
    private BlankNode BNODE4;
    private BlankNode BNODE5;
    private BlankNode BNODE6;
    private BlankNode BNODE7;
    private BlankNode BNODE8;
    private GraphElementFactory fac;
    private TripleFactory tFac;

    public void setUp() throws Exception {
        mapper = new NewBlankNodeMapperImpl();
        fac = GRAPH.getElementFactory();
        tFac = GRAPH.getTripleFactory();
        BNODE4 = fac.createBlankNode();
        BNODE5 = fac.createBlankNode();
        BNODE6 = fac.createBlankNode();
        BNODE7 = fac.createBlankNode();
        BNODE8 = fac.createBlankNode();
    }

    public void testIncompatibleMolecules() {
        NewMolecule m1 = moleculeFactory.createMolecule(B1R1R1);
        NewMolecule m2 = moleculeFactory.createMolecule(B2R2B3);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertTrue(blankNodeMap.isEmpty());
    }

    public void testLevelOneMapping1() {
        NewMolecule m1 = moleculeFactory.createMolecule(B1R1R1);
        NewMolecule m2 = moleculeFactory.createMolecule(B2R1R1);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE1, blankNodeMap.get(BNODE2));
    }

    public void testLevelOneMapping2() {
        NewMolecule m1 = moleculeFactory.createMolecule(R1R2B1);
        NewMolecule m2 = moleculeFactory.createMolecule(R1R2B2);
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE1, blankNodeMap.get(BNODE2));
    }

    public void testConflictingNestedNodes() {
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R2R2), Collections.<Triple>emptySet());
        NewMolecule m2 = createMultiLevelMolecule(asSet(B1R1B3), asSet(B3R2R3), Collections.<Triple>emptySet());
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertTrue(blankNodeMap.isEmpty());
    }

    public void testNestedNodes() {
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R2R2), Collections.<Triple>emptySet());
        NewMolecule m2 = createMultiLevelMolecule(asSet(B1R1B3), asSet(B3R2R2), Collections.<Triple>emptySet());
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(BNODE2, blankNodeMap.get(BNODE3));
    }

    public void test3LvlNodes() throws GraphException {
        Triple B2R1B3 = tFac.createTriple(BNODE2, REF1, BNODE3);
        Triple B3R1B4 = tFac.createTriple(BNODE3, REF1, BNODE4);
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R1B3), asSet(B3R1B4));
        Triple B5R1B6 = tFac.createTriple(BNODE5, REF1, BNODE6);
        Triple B6R1B7 = tFac.createTriple(BNODE6, REF1, BNODE7);
        Triple B7R1B8 = tFac.createTriple(BNODE7, REF1, BNODE8);
        NewMolecule m2 = createMultiLevelMolecule(asSet(B5R1B6), asSet(B6R1B7), asSet(B7R1B8));
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals("size of map", 4, blankNodeMap.size());
        assertEquals(BNODE1, blankNodeMap.get(BNODE5));
        assertEquals(BNODE2, blankNodeMap.get(BNODE6));
        assertEquals(BNODE3, blankNodeMap.get(BNODE7));
        assertEquals(BNODE4, blankNodeMap.get(BNODE8));
    }

    public void testConflicting3Lvl() {
        Triple B2R1B3 = tFac.createTriple(BNODE2, REF1, BNODE3);
        Triple B3R1B4 = tFac.createTriple(BNODE3, REF1, BNODE4);
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R1B3), asSet(B3R1B4));
        Triple B5R1B6 = tFac.createTriple(BNODE5, REF1, BNODE6);
        Triple B6R1B7 = tFac.createTriple(BNODE6, REF1, BNODE7);
        Triple B7R2B8 = tFac.createTriple(BNODE7, REF2, BNODE8);
        NewMolecule m2 = createMultiLevelMolecule(asSet(B5R1B6), asSet(B6R1B7), asSet(B7R2B8));
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m1, m2);
        assertTrue(blankNodeMap.isEmpty());
    }

    public void testSubsumingDirections() {
        Triple B2R1B3 = tFac.createTriple(BNODE2, REF1, BNODE3);
        Triple B3R1B4 = tFac.createTriple(BNODE3, REF1, BNODE4);
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R1B3), asSet(B3R1B4));
        Triple B5R1B6 = tFac.createTriple(BNODE5, REF1, BNODE6);
        Triple B6R1B7 = tFac.createTriple(BNODE6, REF1, BNODE7);
        NewMolecule m2 = createMultiLevelMolecule(asSet(B5R1B6), asSet(B6R1B7), Collections.<Triple>emptySet());
        Map<BlankNode, BlankNode> blankNodeMap = mapper.createMap(m2, m1);
        assertTrue(blankNodeMap.isEmpty());
        blankNodeMap = mapper.createMap(m1, m2);
        assertFalse(blankNodeMap.isEmpty());
        assertEquals(3, blankNodeMap.size());
        assertEquals(BNODE1, blankNodeMap.get(BNODE5));
        assertEquals(BNODE2, blankNodeMap.get(BNODE6));
        assertEquals(BNODE3, blankNodeMap.get(BNODE7));
    }
}
