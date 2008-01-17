package org.jrdf.graph.global.index;

import org.jrdf.graph.Node;
import org.jrdf.graph.Triple;

public class POSMoleculeIndexMemUnitTest extends AbstractMoleculeIndexMemUnitTest {
    protected MoleculeIndex getIndex() {
        return new POSMoleculeIndexMem();
    }

    protected Node[] getNodes(Triple triple) {
        return new Node[]{triple.getPredicate(), triple.getObject(), triple.getSubject()};
    }
}
