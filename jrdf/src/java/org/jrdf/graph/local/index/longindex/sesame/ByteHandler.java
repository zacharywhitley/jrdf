package org.jrdf.graph.local.index.longindex.sesame;

import static org.jrdf.graph.local.index.longindex.sesame.ByteArrayUtil.*;

public class ByteHandler {
    public byte[] toBytes(Long... longs) {
        byte[] b = new byte[8 * longs.length];
        for (int i = 0; i < longs.length; i++) {
            putLong(longs[i], b, 8 * i);
        }
        return b;
    }

    public Long[] fromBytes(byte[] b, int size) {
        Long[] results = new Long[size];
        for (int i  = 0; i < size; i++) {
            results[i] = getLong(b, 8 * i); 
        }
        return results;
    }
}
