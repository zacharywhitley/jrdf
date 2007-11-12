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

package org.jrdf.map;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import org.jrdf.graph.BlankNode;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class StoredMapHandlerImpl implements StoredMapHandler {
    private Map<Class<?>, TupleBinding> binding = new HashMap<Class<?>, TupleBinding>();
    private final DirectoryHandler handler;

    public StoredMapHandlerImpl(DirectoryHandler handler) {
        this.handler = handler;
        binding.put(String.class, new StringBinding());
        binding.put(Long.class, new LongBinding());
        binding.put(LinkedList.class, new LongListBinding());
        binding.put(BlankNode.class, new BlankNodeBinding());
    }

    public Environment setUpEnvironment() throws DatabaseException {
        File dir = handler.getDir();
        dir.mkdirs();
        EnvironmentConfig env = new EnvironmentConfig();
        env.setTransactional(true);
        env.setAllowCreate(true);
        return new Environment(dir, env);
    }

    public DatabaseConfig setUpDatabaseConfig(boolean transactional) {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(transactional);
        dbConfig.setAllowCreate(true);
        return dbConfig;
    }

    public StoredClassCatalog setupCatalog(Environment env, String classCatalogString,  DatabaseConfig dbConfig)
        throws DatabaseException {
        Database catalogDb = env.openDatabase(null, classCatalogString, dbConfig);
        return new StoredClassCatalog(catalogDb);
    }

    public Database setupDatabase(Environment env, String dbName, DatabaseConfig dbConfig) throws DatabaseException {
        return env.openDatabase(null, dbName, dbConfig);
    }

    @SuppressWarnings({ "unchecked" })
    public <T, A, U extends A> Map<T, U> createMap(Environment env, Database database, Class<T> clazz1,
        Class<A> clazz2) {
        EntryBinding keyBinding = getBinding(clazz1);
        EntryBinding dataBinding = getBinding(clazz2);
        return new StoredMap(database, keyBinding, dataBinding, true);
    }

    private EntryBinding getBinding(Class<?> clazz) {
        if (binding.keySet().contains(clazz)) {
            return binding.get(clazz);
        } else {
            throw new IllegalArgumentException("Cannot retrieve binding for class: " + clazz.getClass());
        }
    }
}
