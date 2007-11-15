package org.jrdf.graph.local.index.longindex.sesame;

public class ByteHandler {
    public byte[] toBytes(Long... longs) {
        byte[] b = new byte[8 * longs.length];
        for (int i = 0; i < longs.length; i++) {
            b[i * 8] = (byte) (0xff & (longs[i] >> 8 * 7));
            b[i * 8 + 1] = (byte) (0xff & (longs[i] >> 8 * 6));
            b[i * 8 + 2] = (byte) (0xff & (longs[i] >> 8 * 5));
            b[i * 8 + 3] = (byte) (0xff & (longs[i] >> 8 * 4));
            b[i * 8 + 4] = (byte) (0xff & (longs[i] >> 8 * 3));
            b[i * 8 + 5] = (byte) (0xff & (longs[i] >> 8 * 2));
            b[i * 8 + 6] = (byte) (0xff & (longs[i] >> 8 * 1));
            b[i * 8 + 7] = (byte) (0xff & (longs[i] >> 8 * 0));
        }
        return b;
    }

    public Long[] fromBytes(byte[] b, int size) {
        Long[] results = new Long[size];
        for (int i  = 0; i < size; i++) {
            results[i] = (((long) (b[i * 8] & 0xff) << 56) |
            ((long) (b[i * 8 + 1] & 0xff) << 48) |
            ((long) (b[i * 8 + 2] & 0xff) << 40) |
            ((long) (b[i * 8 + 3] & 0xff) << 32) |
            ((long) (b[i * 8 + 4] & 0xff) << 24) |
            ((long) (b[i * 8 + 5] & 0xff) << 16) |
            ((long) (b[i * 8 + 6] & 0xff) << 8) |
            ((long) (b[i * 8 + 7] & 0xff)));
        }
        return results;
    }
}
