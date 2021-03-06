/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

package org.jrdf.collection;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import org.jrdf.util.bdb.BdbEnvironmentHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;

import static org.jrdf.util.param.ParameterUtil.checkNotNull;

/**
 * An on disk implementation that uses Java BDB edition.
 */
public class BdbCollectionFactory implements IteratorTrackingCollectionFactory {
    private final BdbEnvironmentHandler handler;
    private final String baseDatabaseName;
    private Environment env;
    private long collectionNumber;
    private Map<Iterator<?>, Database> mappedDatabases = new HashMap<Iterator<?>, Database>();
    private Stack<Database> unmappedDatabases = new Stack<Database>();

    public BdbCollectionFactory(BdbEnvironmentHandler newHandler, String newDatabaseName) {
        checkNotNull(newHandler, newDatabaseName);
        this.handler = newHandler;
        this.baseDatabaseName = newDatabaseName;
    }

    public <T> SortedSet<T> createSet(Class<T> clazz) {
        return createSet(clazz, null);
    }

    @SuppressWarnings({ "unchecked" })
    public <T> SortedSet<T> createSet(Class<T> clazz, Comparator<?> comparator) {
        try {
            final Database database = createDatabase((Comparator<byte[]>) comparator);
            return handler.createSet(database, clazz);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            collectionNumber = 0;
            closeMappedDatabases();
        } finally {
            try {
                closeUnmappedDatabases();
            } finally {
                closeEnvironment();
            }
        }
    }

    public void trackCurrentIteratorResource(Iterator<?> iterator) {
        mappedDatabases.put(iterator, unmappedDatabases.pop());
    }

    public void removeIteratorResources(Iterator<?> iterator) {
        final Database database = mappedDatabases.get(iterator);
        if (database != null) {
            database.close();
            mappedDatabases.remove(iterator);
        } else {
            throw new RuntimeException("Tried to remove unmapped iterator: " + iterator);
        }
    }

    private Database createDatabase(Comparator<byte[]> comparator) throws DatabaseException {
        collectionNumber++;
        env = handler.setUpEnvironment();
        DatabaseConfig dbConfig = handler.setUpDatabaseConfig(false);
        if (comparator != null) {
            dbConfig.setOverrideBtreeComparator(true);
            dbConfig.setBtreeComparator(comparator);
        }
        final String databaseName = baseDatabaseName + collectionNumber;
        final Database database = handler.setupDatabase(env, databaseName, dbConfig);
        unmappedDatabases.push(database);
        return database;
    }

    private void closeMappedDatabases() {
        for (Database database : mappedDatabases.values()) {
            database.close();
        }
        mappedDatabases.clear();
    }

    private void closeUnmappedDatabases() {
        for (Database database : unmappedDatabases) {
            database.close();
        }
    }

    private void closeEnvironment() {
        if (env != null) {
            env.close();
        }
    }
}