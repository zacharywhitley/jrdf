package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.graph.local.index.longindex.AbstractLongIndexIntegrationTest;
import org.jrdf.map.TempDirectoryHandler;

public class LongIndexSesameIntegrationTest extends AbstractLongIndexIntegrationTest {

    public void setUp() {
        longIndex = new LongIndexSesame(new TempDirectoryHandler(), "sesTestDb");
        longIndex.clear();
    }
}
