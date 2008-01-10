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
import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * Class that provides indexed storage and retrieval of arbitrary length data.
 *
 * @author Arjohn Kampman
 */
public class IterativeDataStore {

    /*-----------*
      * Variables *
      *-----------*/

    private DataFile dataFile;

    private IDFile idFile;

    private HashFile hashFile;

    /**
     * The checksum to use for calculating data hashes.
     */
    private CRC32 crc32 = new CRC32();

    /*--------------*
      * Constructors *
      *--------------*/

    public IterativeDataStore(File dataDir, String filePrefix)
        throws IOException {
        this(dataDir, filePrefix, false);
    }

    public IterativeDataStore(File dataDir, String filePrefix, boolean forceSync)
        throws IOException {
        File file = new File(dataDir, filePrefix + ".dat");
        dataFile = new DataFile(file, forceSync);
        idFile = new IDFile(new File(dataDir, filePrefix + ".id"), forceSync);
        hashFile = new HashFile(new File(dataDir, filePrefix + ".hash"), forceSync);
    }

    /*---------*
      * Methods *
      *---------*/

    /**
     * Gets the value for the specified ID.
     *
     * @param id
     *        A value ID, should be larger than 0.
     * @return The value for the ID, or <tt>null</tt> if no such value could be
     *         found.
     * @exception IOException
     *            If an I/O error occurred.
     */
    public byte[] getData(int id)
        throws IOException {
        assert id > 0 : "id must be larger than 0, is: " + id;

        // Data not in cache or cache not used, fetch from file
        long offset = idFile.getOffset(id);

        if (offset != 0L) {
            return dataFile.getData(offset);
        }

        return null;
    }

    /**
     * Gets the ID for the specified value.
     *
     * @param queryData
     *        The value to get the ID for, must not be <tt>null</tt>.
     * @return The ID for the specified value, or <tt>-1</tt> if no such ID
     *         could be found.
     * @exception IOException
     *            If an I/O error occurred.
     */
    public int getID(byte[] queryData)
        throws IOException {
        assert queryData != null : "queryData must not be null";

        int id = -1;

        // Value not in cache or cache not used, fetch from file
        int hash = getDataHash(queryData);
        HashFile.IDIterator iter = hashFile.getIDIterator(hash);

        while ((id = iter.next()) >= 0) {
            long offset = idFile.getOffset(id);
            byte[] data = dataFile.getData(offset);

            if (Arrays.equals(queryData, data)) {
                // Matching data found
                break;
            }
        }

        return id;
    }

    /**
     * Returns the maximum value-ID that is in use.
     *
     * @return The largest ID, or <tt>0</tt> if the store does not contain any
     *         values.
     * @throws IOException
     *         If an I/O error occurs.
     */
    public int getMaxID()
        throws IOException {
        return idFile.getMaxID();
    }

    /**
     * Stores the supplied value and returns the ID that has been assigned to it.
     * In case the data to store is already present, the ID of this existing data
     * is returned.
     *
     * @param data
     *        The data to store, must not be <tt>null</tt>.
     * @return The ID that has been assigned to the value.
     * @exception IOException
     *            If an I/O error occurred.
     */
    public int storeData(byte[] data)
        throws IOException {
        assert data != null : "data must not be null";

        int id = getID(data);

        if (id == -1) {
            // Data not stored yet, store it under a new ID.
            long offset = dataFile.storeData(data);
            id = idFile.storeOffset(offset);
            hashFile.storeID(getDataHash(data), id);
        }

        return id;
    }

    /**
     * Synchronizes any recent changes to the data to disk.
     *
     * @exception IOException
     *            If an I/O error occurred.
     */
    public void sync()
        throws IOException {
        hashFile.sync();
        idFile.sync();
        dataFile.sync();
    }

    /**
     * Removes all values from the DataStore.
     *
     * @exception IOException
     *            If an I/O error occurred.
     */
    public void clear()
        throws IOException {
        hashFile.clear();
        idFile.clear();
        dataFile.clear();
    }

    /**
     * Closes the DataStore, releasing any file references, etc. In case a
     * transaction is currently open, it will be rolled back. Once closed, the
     * DataStore can no longer be used.
     *
     * @exception IOException
     *            If an I/O error occurred.
     */
    public void close()
        throws IOException {
        hashFile.close();
        idFile.close();
        dataFile.close();
    }

    public DataFile.DataIterator iterator() {
        return dataFile.iterator();
    }

    public HashFile.IDIterator hashIterator(int id) throws IOException {
        return hashFile.getIDIterator(id);
    }

    /**
     * Gets a hash code for the supplied data.
     *
     * @param data
     *        The data to calculate the hash code for.
     * @return A hash code for the supplied data.
     */
    private int getDataHash(byte[] data) {
        synchronized (crc32) {
            crc32.update(data);
            int crc = (int) crc32.getValue();
            crc32.reset();
            return crc;
        }
    }
}
