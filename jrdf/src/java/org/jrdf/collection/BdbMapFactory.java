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
import com.sleepycat.je.Environment;
import org.jrdf.util.bdb.BdbEnvironmentHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jrdf.util.param.ParameterUtil.checkNotNull;

public final class BdbMapFactory implements MapFactory {
    private final BdbEnvironmentHandler handler;
    private final String databaseName;
    private Environment env;
    private List<Database> databases = new ArrayList<Database>();
    private long mapNumber;

    public BdbMapFactory(BdbEnvironmentHandler newHandler, String newDatabaseName) {
        checkNotNull(newHandler, newDatabaseName);
        this.handler = newHandler;
        this.databaseName = newDatabaseName;
    }

    public <A, T, U extends A> Map<T, U> createMap(Class<T> clazz1, Class<A> clazz2) {
        mapNumber++;
        return createMap(clazz1, clazz2, Long.toString(mapNumber));
    }

    public <A, T, U extends A> Map<T, U> createTemporaryMap(Class<T> clazz1, Class<A> clazz2) {
        mapNumber++;
        return reallyCreateMap(clazz1, clazz2, Long.toString(mapNumber), false, true);
    }

    public <A, T, U extends A> Map<T, U> createMap(Class<T> clazz1, Class<A> clazz2, String name) {
        return reallyCreateMap(clazz1, clazz2, name, true, true);
    }

    public <A, T, U extends A> Map<T, U> openExistingMap(Class<T> clazz1, Class<A> clazz2, String name) {
        return reallyCreateMap(clazz1, clazz2, name, true, false);
    }

    public void close() {
        try {
            mapNumber = 0;
            closeDatabase();
        } finally {
            closeEnvironment();
        }
    }

    private <A, T, U extends A> Map<T, U> reallyCreateMap(Class<T> clazz1, Class<A> clazz2, String name,
        boolean keepMap, boolean removeExisting) {
        env = handler.setUpEnvironment();
        if (env.getDatabaseNames().contains(name) && removeExisting) {
            env.removeDatabase(null, name);
        }
        DatabaseConfig dbConfig = handler.setUpDatabaseConfig(keepMap);
        Database database = handler.setupDatabase(env, databaseName + name, dbConfig);
        databases.add(database);
        return handler.createMap(database, clazz1, clazz2);
    }

    private void closeDatabase() {
        if (!databases.isEmpty()) {
            for (Database database : databases) {
                database.close();
            }
        }
        databases.clear();
    }

    private void closeEnvironment() {
        if (env != null) {
            env.sync();
            env.close();
            env = null;
        }
    }
}
