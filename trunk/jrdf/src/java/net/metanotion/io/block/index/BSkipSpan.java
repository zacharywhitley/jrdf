/*
Copyright (c) 2006, Matthew Estes
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
* Neither the name of Metanotion Software nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.metanotion.io.block.index;

import net.metanotion.io.Serializer;
import net.metanotion.io.block.BlockFile;
import net.metanotion.util.skiplist.BaseSkipList;
import net.metanotion.util.skiplist.SkipSpan;

import java.io.IOException;

public class BSkipSpan extends SkipSpan {

    protected BlockFile bf;
    protected int page;
    protected int overflowPage;

    protected int prevPage;
    protected int nextPage;
    protected Serializer keySer;
    protected Serializer valSer;

    public static void init(BlockFile bf, int page, int spanSize) throws IOException {
        BlockFile.pageSeek(bf.getFile(), page);
        bf.getFile().writeInt(0);
        bf.getFile().writeInt(0);
        bf.getFile().writeInt(0);
        bf.getFile().writeShort((short) spanSize);
        bf.getFile().writeShort(0);
    }

    public SkipSpan newInstance(BaseSkipList sl) {
        try {
            int newPage = bf.allocPage();
            init(bf, newPage, bf.getSpanSize());
            return new BSkipSpan(bf, (BSkipList) sl, newPage, keySer, valSer);
        } catch (IOException ioe) {
            throw new Error();
        }
    }

    public void killInstance() {
        try {
            int curPage = overflowPage;
            int next;
            while (curPage != 0) {
                BlockFile.pageSeek(bf.getFile(), curPage);
                next = bf.getFile().readInt();
                bf.freePage(curPage);
                curPage = next;
            }
            bf.freePage(page);
        } catch (IOException ioe) {
            throw new Error();
        }
    }

    public void flush() {
        try {
            BlockFile.pageSeek(bf.getFile(), page);
            bf.getFile().writeInt(overflowPage);
            bf.getFile().writeInt((prev != null) ? ((BSkipSpan) prev).page : 0);
            bf.getFile().writeInt((next != null) ? ((BSkipSpan) next).page : 0);
            bf.getFile().writeShort((short) keys.length);
            bf.getFile().writeShort((short) nKeys);

            int ksz, vsz;
            int curPage = this.page;
            int[] curNextPage = new int[1];
            curNextPage[0] = this.overflowPage;
            int[] pageCounter = new int[1];
            pageCounter[0] = 16;
            byte[] keyData;
            byte[] valData;

            for (int i = 0; i < nKeys; i++) {
                if ((pageCounter[0] + 4) > BlockFile.PAGESIZE) {
                    if (curNextPage[0] == 0) {
                        curNextPage[0] = bf.allocPage();
                        BlockFile.pageSeek(bf.getFile(), curNextPage[0]);
                        bf.getFile().writeInt(0);
                        BlockFile.pageSeek(bf.getFile(), curPage);
                        bf.getFile().writeInt(curNextPage[0]);
                    }
                    BlockFile.pageSeek(bf.getFile(), curNextPage[0]);
                    curPage = curNextPage[0];
                    curNextPage[0] = bf.getFile().readInt();
                    pageCounter[0] = 4;
                }
                keyData = this.keySer.getBytes(keys[i]);
                valData = this.valSer.getBytes(vals[i]);
                pageCounter[0] += 4;
                bf.getFile().writeShort(keyData.length);
                bf.getFile().writeShort(valData.length);
                curPage = bf.writeMultiPageData(keyData, curPage, pageCounter, curNextPage);
                curPage = bf.writeMultiPageData(valData, curPage, pageCounter, curNextPage);
            }
            BlockFile.pageSeek(bf.getFile(), this.page);
            this.overflowPage = bf.getFile().readInt();
        } catch (IOException ioe) {
            throw new Error();
        }
    }

    private static void load(BSkipSpan bss, BlockFile bf, BSkipList bsl, int spanPage, Serializer key, Serializer val)
        throws IOException {
        bss.bf = bf;
        bss.page = spanPage;
        bss.keySer = key;
        bss.valSer = val;

        bsl.spanHash.put(new Integer(spanPage), bss);

        BlockFile.pageSeek(bf.getFile(), spanPage);

        bss.overflowPage = bf.getFile().readInt();
        bss.prevPage = bf.getFile().readInt();
        bss.nextPage = bf.getFile().readInt();
        int sz = bf.getFile().readShort();
        bss.nKeys = bf.getFile().readShort();

        bss.keys = new Comparable[sz];
        bss.vals = new Object[sz];

        int ksz, vsz;
        int curPage = spanPage;
        int[] curNextPage = new int[1];
        curNextPage[0] = bss.overflowPage;
        int[] pageCounter = new int[1];
        pageCounter[0] = 16;
//System.out.println("Span Load " + sz + " nKeys " + nKeys + " page " + curPage);
        for (int i = 0; i < bss.nKeys; i++) {
            if ((pageCounter[0] + 4) > BlockFile.PAGESIZE) {
                BlockFile.pageSeek(bf.getFile(), curNextPage[0]);
                curPage = curNextPage[0];
                curNextPage[0] = bf.getFile().readInt();
                pageCounter[0] = 4;
            }
            ksz = bf.getFile().readShort();
            vsz = bf.getFile().readShort();
            pageCounter[0] += 4;
            byte[] k = new byte[ksz];
            byte[] v = new byte[vsz];
            curPage = bf.readMultiPageData(k, curPage, pageCounter, curNextPage);
            curPage = bf.readMultiPageData(v, curPage, pageCounter, curNextPage);
//System.out.println("i=" + i + ", Page " + curPage + ", offset " + pageCounter[0] + " ksz " + ksz + " vsz " + vsz);
            bss.keys[i] = (Comparable) bss.keySer.construct(k);
            bss.vals[i] = bss.valSer.construct(v);
        }

    }

    protected BSkipSpan() {
    }

    public BSkipSpan(BlockFile bf, BSkipList bsl, int spanPage, Serializer key, Serializer val) throws IOException {
        BSkipSpan.load(this, bf, bsl, spanPage, key, val);
        this.next = null;
        this.prev = null;

        BSkipSpan bss = this;
        BSkipSpan temp;
        int np = nextPage;
        while (np != 0) {
            temp = (BSkipSpan) bsl.spanHash.get(new Integer(np));
            if (temp != null) {
                bss.next = temp;
                break;
            }
            bss.next = new BSkipSpan();
            bss.next.next = null;
            bss.next.prev = bss;
            bss = (BSkipSpan) bss.next;

            BSkipSpan.load(bss, bf, bsl, np, key, val);
            np = bss.nextPage;
        }

        bss = this;
        np = prevPage;
        while (np != 0) {
            temp = (BSkipSpan) bsl.spanHash.get(new Integer(np));
            if (temp != null) {
                bss.next = temp;
                break;
            }
            bss.prev = new BSkipSpan();
            bss.prev.next = bss;
            bss.prev.prev = null;
            bss = (BSkipSpan) bss.prev;

            BSkipSpan.load(bss, bf, bsl, np, key, val);
            np = bss.prevPage;
        }
    }
}
