package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.graph.local.index.longindex.AbstractLongIndexIntegrationTest;
import org.jrdf.util.TempDirectoryHandler;

public class LongIndexSesameIntegrationTest extends AbstractLongIndexIntegrationTest {

    public void setUp() {
        TripleBTree tree = new BTreeFactoryImpl().createBTree(new TempDirectoryHandler(), "sesTestDb");
        longIndex = new LongIndexSesame(tree);
        longIndex.clear();
    }
}
