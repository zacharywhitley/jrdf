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
import net.metanotion.util.skiplist.SkipLevels;
import net.metanotion.util.skiplist.SkipSpan;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class BSkipList extends BaseSkipList {
    public int firstSpanPage = 0;
    public int firstLevelPage = 0;
    public int skipPage = 0;
    public BlockFile bf;

    public HashMap spanHash = new HashMap();
    public HashMap levelHash = new HashMap();

    protected BSkipList() {
    }

    public BSkipList(int spanSize, BlockFile bf, int skipPage, Serializer key, Serializer val) throws IOException {
        if (spanSize < 1) {
            throw new Error("Span size too small");
        }

        this.skipPage = skipPage;
        this.bf = bf;

        BlockFile.pageSeek(bf.getFile(), skipPage);
        firstSpanPage = bf.getFile().readInt();
        firstLevelPage = bf.getFile().readInt();
        size = bf.getFile().readInt();
        spans = bf.getFile().readInt();
        System.out.println(size + " " + spans);

        first = new BSkipSpan(bf, this, firstSpanPage, key, val);
        stack = new BSkipLevels(bf, firstLevelPage, this);
        rng = new Random(System.currentTimeMillis());
    }

    public void close() {
        System.out.println("Closing index " + size + " and " + spans);
        flush();
        first = null;
        stack = null;
    }

    public void flush() {
        try {
            BlockFile.pageSeek(bf.getFile(), skipPage);
            bf.getFile().writeInt(firstSpanPage);
            bf.getFile().writeInt(firstLevelPage);
            bf.getFile().writeInt(size);
            bf.getFile().writeInt(spans);

        } catch (IOException ioe) {
            throw new Error();
        }
    }

    public void delete() throws IOException {
        SkipLevels curLevel = stack, nextLevel;
        while (curLevel != null) {
            nextLevel = curLevel.levels[0];
            curLevel.killInstance();
            curLevel = nextLevel;
        }

        SkipSpan curSpan = first, nextSpan;
        while (curSpan != null) {
            nextSpan = curSpan.next;
            curSpan.killInstance();
            curSpan = nextSpan;
        }

        bf.freePage(skipPage);
    }

    public static void init(BlockFile bf, int page, int spanSize) throws IOException {
        int firstSpan = bf.allocPage();
        int firstLevel = bf.allocPage();
        BlockFile.pageSeek(bf.getFile(), page);
        bf.getFile().writeInt(firstSpan);
        bf.getFile().writeInt(firstLevel);
        bf.getFile().writeInt(0);
        bf.getFile().writeInt(1);
        BSkipSpan.init(bf, firstSpan, spanSize);
        BSkipLevels.init(bf, firstLevel, firstSpan, 4);
    }

    public int maxLevels() {
        int max = super.maxLevels();
        int cells = (int) ((BlockFile.PAGESIZE - 8) / 4);
        return (max > cells) ? cells : max;
    }

}
