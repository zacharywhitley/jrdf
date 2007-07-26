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

package org.jrdf.graph.index.nodepool.map;

import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import org.jrdf.graph.Node;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.index.nodepool.NodePoolFactory;
import org.jrdf.AbstractJeBDBHandler;

import java.io.File;

public class JeNodePoolFactory extends AbstractJeBDBHandler implements NodePoolFactory {
    private static final String CLASS_CATALOG_NODEPOOL = "java_class_catalog_nodepool";
    private static final String CLASS_CATALOG_STRINGPOOL = "java_class_catalog_stringpool";
    private static final String USERNAME = System.getProperty("user.name");
    private static final File SYSTEM_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private StoredMap nodePool;
    private StoredMap stringPool;
    private StoredClassCatalog nodePoolCatalog;
    private StoredClassCatalog stringPoolCatalog;

    public NodePool createNodePool() {
        try {
            File dir = new File(SYSTEM_TEMP_DIR, "jrdf_" + USERNAME);
            dir.mkdirs();
            EnvironmentConfig envConfig = setUpEnvironment();
            Environment env = new Environment(dir, envConfig);
            DatabaseConfig dbConfig = setUpDatabase(true);
//            setupCatalogs(env, dbConfig);
            nodePoolCatalog = setupCatalog(env, dbConfig, CLASS_CATALOG_NODEPOOL);
            stringPoolCatalog = setupCatalog(env, dbConfig, CLASS_CATALOG_STRINGPOOL);
            nodePool = createMap(env, "nodePool", nodePoolCatalog, Long.class, Node.class);
            stringPool = createMap(env, "stringPool", stringPoolCatalog, String.class, Long.class);
            return new NodePoolImpl(nodePool, stringPool);
        } catch (DatabaseException dbe) {
            throw new RuntimeException("Could not create database", dbe);
        }
    }

//    private EnvironmentConfig setUpEnvironment() {
//        EnvironmentConfig env = new EnvironmentConfig();
//        env.setTransactional(true);
//        env.setAllowCreate(true);
//        return env;
//    }
//
//    private DatabaseConfig setUpDatabase(boolean transactional) {
//        DatabaseConfig dbConfig = new DatabaseConfig();
//        dbConfig.setTransactional(transactional);
//        dbConfig.setAllowCreate(true);
//        return dbConfig;
//    }
//
//    private void setupCatalogs(Environment env, DatabaseConfig dbConfig) throws DatabaseException {
//        Database nodePoolCatalogDb = env.openDatabase(null, CLASS_CATALOG_NODEPOOL, dbConfig);
//        nodePoolCatalog = new StoredClassCatalog(nodePoolCatalogDb);
//        Database stringPoolCatalogDb = env.openDatabase(null, CLASS_CATALOG_STRINGPOOL, dbConfig);
//        stringPoolCatalog = new StoredClassCatalog(stringPoolCatalogDb);
//    }

    private StoredMap createMap(Environment env, String dbName, StoredClassCatalog catalog, Class<?> key, Class<?> data
    ) throws DatabaseException {
        DatabaseConfig dbConfig = setUpDatabase(false);
        Database database = env.openDatabase(null, dbName, dbConfig);
        SerialBinding keyBinding = new SerialBinding(catalog, key);
        SerialBinding dataBinding = new SerialBinding(nodePoolCatalog, data);
        return new StoredMap(database, keyBinding, dataBinding, true);
    }
}
