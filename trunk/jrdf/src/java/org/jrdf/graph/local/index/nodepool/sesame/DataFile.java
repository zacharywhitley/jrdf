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
import java.util.NoSuchElementException;

/**
 * Class supplying access to a data file. A data file stores data sequentially.
 * Each entry starts with the entry's length (4 bytes), followed by the data
 * itself. File offsets are used to identify entries.
 *
 * @author Arjohn Kampman
 */
public class DataFile {

    /*-----------*
      * Constants *
      *-----------*/

    /**
     * Magic number "Native Data File" to detect whether the file is actually a
     * data file. The first three bytes of the file should be equal to this magic
     * number.
     */
    private static final byte[] MAGIC_NUMBER = new byte[]{'n', 'd', 'f'};

    /**
     * File format version, stored as the fourth byte in data files.
     */
    private static final byte FILE_FORMAT_VERSION = 1;

    private static final long HEADER_LENGTH = MAGIC_NUMBER.length + 1;

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

    public DataFile(File file)
        throws IOException {
        this(file, false);
    }

    public DataFile(File file, boolean forceSync)
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
            buf.rewind();

            fileChannel.write(buf, 0L);

            sync();
        } else {
            // Verify file header
            ByteBuffer buf = ByteBuffer.allocate((int) HEADER_LENGTH);
            fileChannel.read(buf, 0L);
            buf.rewind();

            if (buf.remaining() < HEADER_LENGTH) {
                throw new IOException("File too short to be a compatible data file");
            }

            byte[] magicNumber = new byte[MAGIC_NUMBER.length];
            buf.get(magicNumber);
            byte version = buf.get();

            if (!Arrays.equals(MAGIC_NUMBER, magicNumber)) {
                throw new IOException("File doesn't contain compatible data records");
            }

            if (version > FILE_FORMAT_VERSION) {
                throw new IOException("Unable to read data file; it uses a newer file format");
            } else if (version != FILE_FORMAT_VERSION) {
                throw new IOException("Unable to read data file; invalid file format version: " + version);
            }
        }
    }

    /*---------*
      * Methods *
      *---------*/

    public File getFile() {
        return file;
    }

    /**
     * Stores the specified data and returns the byte-offset at which it has been
     * stored.
     *
     * @param data
     *        The data to store, must not be <tt>null</tt>.
     * @return The byte-offset in the file at which the data was stored.
     */
    public long storeData(byte[] data)
        throws IOException {
        assert data != null : "data must not be null";

        long offset = fileChannel.size();

        ByteBuffer buf = ByteBuffer.allocate(data.length + 4);
        buf.putInt(data.length);
        buf.put(data);
        buf.rewind();

        fileChannel.write(buf, offset);

        return offset;
    }

    /**
     * Gets the data that is stored at the specified offset.
     *
     * @param offset
     *        An offset in the data file, must be larger than 0.
     * @return The data that was found on the specified offset.
     * @exception IOException
     *            If an I/O error occurred.
     */
    public byte[] getData(long offset)
        throws IOException {
        assert offset > 0 : "offset must be larger than 0, is: " + offset;

        // TODO: maybe get more data in one go is more efficient?
        ByteBuffer buf = ByteBuffer.allocate(4);
        fileChannel.read(buf, offset);
        int dataLength = buf.getInt(0);

        byte[] data = new byte[dataLength];
        buf = ByteBuffer.wrap(data);
        fileChannel.read(buf, offset + 4L);

        return data;
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
     * Closes the data file, releasing any file locks that it might have.
     *
     * @throws IOException
     */
    public void close()
        throws IOException {
        raf.close();
    }

    /**
     * Gets an iterator that can be used to iterate over all stored data.
     *
     * @return a DataIterator.
     */
    public DataIterator iterator() {
        return new DataIterator();
    }

    /**
     * An iterator that iterates over the data that is stored in a data file.
     */
    public class DataIterator {

        private long position = HEADER_LENGTH;

        public boolean hasNext()
            throws IOException {
            return position < fileChannel.size();
        }

        public byte[] next()
            throws IOException {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            byte[] data = getData(position);
            position += (4 + data.length);
            return data;
        }
    }
}
