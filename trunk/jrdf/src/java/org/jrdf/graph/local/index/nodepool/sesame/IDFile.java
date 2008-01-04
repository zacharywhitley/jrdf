/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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
package org.jrdf.graph.local.index.nodepool.sesame;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Class supplying access to an ID file. An ID file maps IDs (integers &gt;= 1)
 * to file pointers (long integers). There is a direct correlation between IDs
 * and the position at which the file pointers are stored; the file pointer for
 * ID X is stored at position 8*X.
 *
 * @author Arjohn Kampman
 */
public class IDFile {

    /*-----------*
      * Constants *
      *-----------*/

    /**
     * Magic number "Native ID File" to detect whether the file is actually an ID
     * file. The first three bytes of the file should be equal to this magic
     * number.
     */
    private static final byte[] MAGIC_NUMBER = new byte[]{'n', 'i', 'f'};

    /**
     * File format version, stored as the fourth byte in ID files.
     */
    private static final byte FILE_FORMAT_VERSION = 1;

    /**
     * The size of the file header in bytes. The file header contains the
     * following data: magic number (3 bytes) file format version (1 byte) and 4
     * dummy bytes to align data at 8-byte offsets.
     */
    private static final long HEADER_LENGTH = 8;

    private static final long ITEM_SIZE = 8L;

    /*-----------*
      * Variables *
      *-----------*/

    private File file;

    private RandomAccessFile raf;

    private FileChannel fileChannel;

    private boolean forceSync;

    /*--------------*
      * Constructors *
      *--------------*/

    public IDFile(File file)
        throws IOException {
        this(file, false);
    }

    public IDFile(File file, boolean forceSync)
        throws IOException {
        this.file = file;
        this.forceSync = forceSync;

        if (!file.exists()) {
            boolean created = file.createNewFile();
            if (!created) {
                throw new IOException("Failed to create file: " + file);
            }
        }

        // Open a read/write channel to the file
        raf = new RandomAccessFile(file, "rw");
        fileChannel = raf.getChannel();

        if (fileChannel.size() == 0L) {
            // Empty file, write header
            ByteBuffer buf = ByteBuffer.allocate((int) HEADER_LENGTH);
            buf.put(MAGIC_NUMBER);
            buf.put(FILE_FORMAT_VERSION);
            buf.put(new byte[]{0, 0, 0, 0});
            buf.rewind();

            fileChannel.write(buf, 0L);

            sync();
        } else {
            // Verify file header
            ByteBuffer buf = ByteBuffer.allocate((int) HEADER_LENGTH);
            fileChannel.read(buf, 0L);
            buf.rewind();

            if (buf.remaining() < HEADER_LENGTH) {
                throw new IOException("File too short to be a compatible ID file");
            }

            byte[] magicNumber = new byte[MAGIC_NUMBER.length];
            buf.get(magicNumber);
            byte version = buf.get();

            if (!Arrays.equals(MAGIC_NUMBER, magicNumber)) {
                throw new IOException("File doesn't contain compatible ID records");
            }

            if (version > FILE_FORMAT_VERSION) {
                throw new IOException("Unable to read ID file; it uses a newer file format");
            } else if (version != FILE_FORMAT_VERSION) {
                throw new IOException("Unable to read ID file; invalid file format version: " + version);
            }
        }

    }

    /*---------*
      * Methods *
      *---------*/

    public final File getFile() {
        return file;
    }

    /**
     * Gets the largest ID that is stored in this ID file.
     *
     * @return The largest ID, or <tt>0</tt> if the file does not contain any
     *         data.
     * @throws IOException
     *         If an I/O error occurs.
     */
    public int getMaxID()
        throws IOException {
        return (int) (fileChannel.size() / ITEM_SIZE) - 1;
    }

    /**
     * Stores the offset of a new data entry, returning the ID under which is
     * stored.
     */
    public int storeOffset(long offset)
        throws IOException {
        int id = (int) (fileChannel.size() / ITEM_SIZE);
        setOffset(id, offset);
        return id;
    }

    /**
     * Sets or updates the stored offset for the specified ID.
     *
     * @param id
     *        The ID to set the offset for, must be larger than 0.
     * @param offset
     *        The (new) offset for the specified ID.
     */
    public void setOffset(int id, long offset)
        throws IOException {
        assert id > 0 : "id must be larger than 0, is: " + id;

        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(0, offset);
        fileChannel.write(buf, ITEM_SIZE * id);
    }

    /**
     * Gets the offset of the data entry with the specified ID.
     *
     * @param id
     *        The ID to get the offset for, must be larger than 0.
     * @return The offset for the ID.
     */
    public long getOffset(int id)
        throws IOException {
        assert id > 0 : "id must be larger than 0, is: " + id;

        ByteBuffer buf = ByteBuffer.allocate(8);
        fileChannel.read(buf, ITEM_SIZE * id);
        return buf.getLong(0);
    }

    /**
     * Discards all stored data.
     *
     * @throws IOException
     *         If an I/O error occurred.
     */
    public void clear()
        throws IOException {
        fileChannel.truncate(HEADER_LENGTH);
    }

    /**
     * Syncs any unstored data to the hash file.
     */
    public void sync()
        throws IOException {
        if (forceSync) {
            fileChannel.force(false);
        }
    }

    /**
     * Closes the ID file, releasing any file locks that it might have.
     *
     * @throws IOException
     */
    public void close()
        throws IOException {
        raf.close();
    }
}
