package org.jrdf.graph.global.index;

import org.jrdf.graph.Node;
import org.jrdf.graph.Triple;

public class OSPMoleculeIndexMemUnitTest extends AbstractMoleculeIndexMemUnitTest {
    protected MoleculeIndex getIndex() {
        return new OSPMoleculeIndexMem();
    }

    protected Node[] getNodes(Triple triple) {
        return new Node[]{triple.getObject(), triple.getSubject(), triple.getPredicate()};
    }
}
