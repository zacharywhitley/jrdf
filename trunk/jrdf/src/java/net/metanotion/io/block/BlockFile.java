/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */
package net.metanotion.io.block;

import net.metanotion.io.RAIFile;
import net.metanotion.io.RandomAccessInterface;
import net.metanotion.io.Serializer;
import net.metanotion.io.block.index.BSkipList;
import net.metanotion.io.data.IntBytes;
import net.metanotion.io.data.NullBytes;
import net.metanotion.io.data.StringBytes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class BlockFile {
    private static final long MAGIC_BYTES = 0x3141deadbeef0100L;

    /**
     * Page size.
     */
    public static final long PAGESIZE = 1024;

    /**
     * Offset.
     */
    public static final long OFFSET_MOUNTED = 20;

    /**
     * Span size.
     */
    public static final short INITIAL_SPAN_SIZE = 127;
    private static final int INITIAL_PAGE_COUNT = 4;
    private RandomAccessInterface file;
    private long fileLen = PAGESIZE * 2;
    private int freeListStart;
    private short mounted;
    private BSkipList metaIndex;
    private HashMap openIndices = new HashMap();
    private long headerBytes;
    private short spanSize = INITIAL_SPAN_SIZE;

    public BlockFile(RandomAccessInterface rai) throws IOException {
        this(rai, false);
    }

    public BlockFile(RandomAccessFile raf) throws IOException {
        this(new RAIFile(raf), false);
    }

    public BlockFile(RandomAccessFile raf, boolean init) throws IOException {
        this(new RAIFile(raf), init);
    }

    public BlockFile(File f, boolean init) throws IOException {
        this(new RAIFile(f, true, true), init);
    }

    public BlockFile(RandomAccessInterface rai, boolean init) throws IOException {
        if (rai == null) {
            throw new NullPointerException();
        }

        file = rai;

        if (init) {
            file.setLength(fileLen);
            writeSuperBlock();
            BSkipList.init(this, 2, spanSize);
        }

        readSuperBlock();
        if (headerBytes != MAGIC_BYTES) {
            if ((headerBytes & MAGIC_BYTES) == MAGIC_BYTES) {
                throw new BadVersionException();
            } else {
                throw new BadFileFormatException();
            }
        }
//if(mounted != 0) { throw new CorruptFileException(); }
        if (fileLen != file.length()) {
            throw new CorruptFileException();
        }
        mount();

        metaIndex = new BSkipList(spanSize, this, 2, new StringBytes(), new IntBytes());
    }

    private void mount() throws IOException {
        file.seek(BlockFile.OFFSET_MOUNTED);
        mounted = 1;
        file.writeShort(mounted);
    }

    public RandomAccessInterface getFile() {
        return file;
    }

    private void writeSuperBlock() throws IOException {
        file.seek(0);
        file.writeLong(MAGIC_BYTES);
        file.writeLong(fileLen);
        file.writeInt(freeListStart);
        file.writeShort(mounted);
        file.writeShort(spanSize);
    }

    private void readSuperBlock() throws IOException {
        file.seek(0);
        headerBytes = file.readLong();
        fileLen = file.readLong();
        freeListStart = file.readInt();
        mounted = file.readShort();
        spanSize = file.readShort();
    }

    public int writeMultiPageData(byte[] data, int page, int[] curPageOff, int[] nextPage) throws IOException {
        int pageCounter = curPageOff[0];
        int curNextPage = nextPage[0];
        int curPage = page;
        int dct = 0;
        while (dct < data.length) {
            int len = ((int) BlockFile.PAGESIZE) - pageCounter;
            if (len <= 0) {
                if (curNextPage == 0) {
                    curNextPage = this.allocPage();
                    BlockFile.pageSeek(this.file, curNextPage);
                    this.getFile().writeInt(0);
                    BlockFile.pageSeek(this.file, curPage);
                    this.getFile().writeInt(curNextPage);
                }
                BlockFile.pageSeek(this.file, curNextPage);
                curPage = curNextPage;
                curNextPage = this.getFile().readInt();
                pageCounter = INITIAL_PAGE_COUNT;
                len = ((int) BlockFile.PAGESIZE) - pageCounter;
            }
            this.getFile().write(data, dct, Math.min(len, data.length - dct));
            pageCounter += Math.min(len, data.length - dct);
            dct += Math.min(len, data.length - dct);
        }
        nextPage[0] = curNextPage;
        curPageOff[0] = pageCounter;
        return curPage;
    }

    public int readMultiPageData(byte[] arr, int page, int[] curPageOff, int[] nextPage) throws IOException {
        int pageCounter = curPageOff[0];
        int curNextPage = nextPage[0];
        int curPage = page;
        int dct = 0;
        int res;
        while (dct < arr.length) {
            int len = ((int) BlockFile.PAGESIZE) - pageCounter;
            if (len <= 0) {
                BlockFile.pageSeek(this.file, curNextPage);
                curPage = curNextPage;
                curNextPage = this.getFile().readInt();
                pageCounter = INITIAL_PAGE_COUNT;
                len = ((int) BlockFile.PAGESIZE) - pageCounter;
            }
            res = this.getFile().read(arr, dct, Math.min(len, arr.length - dct));
            if (res == -1) {
                throw new IOException();
            }
            pageCounter += Math.min(len, arr.length - dct);
            dct += res;
        }
        nextPage[0] = curNextPage;
        curPageOff[0] = pageCounter;
        return curPage;
    }

    public static void pageSeek(RandomAccessInterface file, int page) throws IOException {
        file.seek((((long) page) - 1L) * BlockFile.PAGESIZE);
    }

    public int allocPage() throws IOException {
        if (freeListStart != 0) {
            FreeListBlock flb = new FreeListBlock(file, freeListStart);
            if (flb.len > 0) {
                flb.len = flb.len - 1;
                int page = flb.branches[flb.len];
                flb.writeBlock();
                return page;
            } else {
                freeListStart = flb.nextPage;
                writeSuperBlock();
                return flb.page;
            }
        }
        long offset = file.length();
        fileLen = offset + BlockFile.PAGESIZE;
        file.setLength(fileLen);
        writeSuperBlock();
        return ((int) ((long) (offset / BlockFile.PAGESIZE))) + 1;
    }

    public void freePage(int page) throws IOException {
        System.out.println("Free Page " + page);
        if (freeListStart == 0) {
            freeListStart = page;
            FreeListBlock.initPage(file, page);
            writeSuperBlock();
            return;
        }
        FreeListBlock flb = new FreeListBlock(file, freeListStart);
        if (flb.isFull()) {
            FreeListBlock.initPage(file, page);
            if (flb.nextPage == 0) {
                flb.nextPage = page;
                flb.writeBlock();
                return;
            } else {
                flb = new FreeListBlock(file, page);
                flb.nextPage = freeListStart;
                flb.writeBlock();
                freeListStart = page;
                writeSuperBlock();
                return;
            }
        }
        flb.addPage(page);
        flb.writeBlock();
    }

    public BSkipList getIndex(String name, Serializer key, Serializer val) throws IOException {
        Integer page = (Integer) metaIndex.get(name);
        if (page == null) {
            return null;
        }
        BSkipList bsl = new BSkipList(spanSize, this, page.intValue(), key, val);
        openIndices.put(name, bsl);
        return bsl;
    }

    public BSkipList makeIndex(String name, Serializer key, Serializer val) throws IOException {
        if (metaIndex.get(name) != null) {
            throw new IOException("Index already exists");
        }
        int page = allocPage();
        metaIndex.put(name, new Integer(page));
        BSkipList.init(this, page, spanSize);
        BSkipList bsl = new BSkipList(spanSize, this, page, key, val);
        openIndices.put(name, bsl);
        return bsl;
    }

    public void delIndex(String name) throws IOException {
        Integer page = (Integer) metaIndex.remove(name);
        if (page == null) {
            return;
        }
        NullBytes nb = new NullBytes();
        BSkipList bsl = new BSkipList(spanSize, this, page.intValue(), nb, nb);
        bsl.delete();
    }

    public void close() throws IOException {
        metaIndex.close();
        metaIndex = null;

        Set oi = openIndices.keySet();
        Iterator i = oi.iterator();
        Object k;
        while (i.hasNext()) {
            k = i.next();
            BSkipList bsl = (BSkipList) openIndices.get(k);
            bsl.close();
        }

        // Unmount.
        file.seek(BlockFile.OFFSET_MOUNTED);
        file.writeShort(0);
    }

    public short getSpanSize() {
        return spanSize;
    }

    public static void main(String args[]) {
        try {
            RAIFile raif = new RAIFile(new File(args[0]), true, true);
            BlockFile bf = new BlockFile(raif, true);

            //bf.metaIndex.delete();
            bf.makeIndex("foo", new NullBytes(), new NullBytes());


            BSkipList b = bf.getIndex("foo", new NullBytes(), new NullBytes());
            System.out.println(bf.allocPage());

            bf.close();
            raif.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
