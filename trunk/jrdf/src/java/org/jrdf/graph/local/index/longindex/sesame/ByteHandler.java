package org.jrdf.graph.local.index.longindex.sesame;

import static org.jrdf.graph.local.index.longindex.sesame.ByteArrayUtil.*;

public final class ByteHandler {
    private static final int BITS = 8;

    private ByteHandler() {
    }

    public static byte[] toBytes(Long... longs) {
        byte[] b = new byte[BITS * longs.length];
        for (int i = 0; i < longs.length; i++) {
            putLong(longs[i], b, BITS * i);
        }
        return b;
    }

    public static Long[] fromBytes(byte[] b, int size) {
        Long[] results = new Long[size];
        for (int i  = 0; i < size; i++) {
            results[i] = getLong(b, BITS * i);
        }
        return results;
    }
}
