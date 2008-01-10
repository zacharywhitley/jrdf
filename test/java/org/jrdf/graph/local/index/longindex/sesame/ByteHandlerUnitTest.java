package org.jrdf.graph.local.index.longindex.sesame;

import junit.framework.TestCase;

public class ByteHandlerUnitTest extends TestCase {

    public void testEncodeDecode() {
        byte[] bytes = ByteHandler.toBytes(1234L, 3456L, 123456838L);
        Long[] newLong = ByteHandler.fromBytes(bytes, 3);
        assertEquals(1234L, (long) newLong[0]);
        assertEquals(3456L, (long) newLong[1]);
        assertEquals(123456838L, (long) newLong[2]);
    }
}
