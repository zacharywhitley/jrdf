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

package org.jrdf.persistence;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Resource;
import org.jrdf.persistence.repository.Connection;
import org.jrdf.persistence.repository.RQULException;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Peter Bednar
 * @author Jozef Wagner, http://wagjo.com/
 */
public class EntityManagerImpl implements EntityManager {
    protected boolean open;
    protected Connection connection;
    protected Map managedEntities;

    public EntityManagerImpl(Connection connection) {
        open = true;
        this.connection = connection;
        managedEntities = new HashMap();
    }

    public URI persist(Object entity) throws PersistenceException {
        if (!isOpen()) {
            throw new IllegalStateException();
        }

        ClassMapping cm = RDFIntrospector.getMappings(entity);
        URI uri = cm.getURI(entity);

        Object managed = null;
        boolean insert = false;
        if (uri != null) {
            managed = managedEntities.get(entity);
            if (managed != null && managed != entity) {
                throw new PersistenceException("Not managed entity, Probably you are trying to persiste DAO which " +
                    "was obtained from other EntityManager Instance");
            }
        } else {
            uri = createURI(cm);
            cm.setURI(entity, uri);
            insert = true;
        }
        if (managed == null) {
            managedEntities.put(entity, entity);
        }

        for (String rul : Statements.createInsertOrUpdateStatements(entity, this, insert)) {
            executeUpdate(rul);
        }
        return uri;
    }

    public <T> T find(Class<T> entityClass, URI uri) throws PersistenceException {
        if (!isOpen()) {
            throw new IllegalStateException();
        }

        ClassMapping cm = RDFIntrospector.getMappings(entityClass);
        Object entity = cm.newInstance(uri);

        Object managed = managedEntities.get(entity);
        if (managed != null) {
            return (T) managed;
        }

        Resource res = getResource(uri);
        if (res != null) {
            managedEntities.put(entity, entity);
            cm.copyValues(entity, res, this);
            return (T) entity;
        } else {
            return null;
        }
    }

    public void merge(Object entity) throws PersistenceException {
        // TODO copy properties to managed entity
        managedEntities.put(entity, entity);
    }

    public void remove(Object entity) throws PersistenceException {
        remove(RDFIntrospector.getMappings(entity).getURI(entity));
    }

    public void remove(URI uri) throws PersistenceException {
        if (!isOpen()) {
            throw new IllegalStateException();
        }
        if (uri == null) {
            return;
        }

        Iterator managed = managedEntities.values().iterator();
        while (managed.hasNext()) {
            Object entity = managed.next();
            if (uri.equals(RDFIntrospector.getMappings(entity).getURI(entity))) {
                managed.remove();
            }
        }

        executeUpdate(Statements.createDeleteStatement(uri));
    }

    public Query createNativeQuery(String rulString) {
        if (!isOpen()) {
            throw new IllegalStateException();
        }

        return new QueryImpl(rulString, this);
    }

    public Query createNativeQuery(String rqlString, Class resultClass) {
        if (!isOpen()) {
            throw new IllegalStateException();
        }

        return new QueryImpl(rqlString, resultClass, this);
    }

    public boolean isOpen() {
        return open;
    }

    public void close() {
        open = false;
    }

    public void clear() {
        managedEntities.clear();
    }

    protected Resource getResource(URI uri) throws PersistenceException {
        try {
            return ResultParser.getResource(executeQuery(Statements.createSelectStatement(uri)), uri);
        } catch (GraphElementFactoryException e) {
            throw new PersistenceException(e);
        }
    }

    public List<URI> getResultSet(String rql) throws PersistenceException {
        return ResultParser.getResultSet(executeQuery(rql));
    }

    private static URI createURI(ClassMapping mapping) {
        // TODO call URI generator
        String uri = "";
        if (mapping.typeURIs.length != 0) {
            uri = mapping.typeURIs[0].toString();
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd-HHmm");

            Calendar calendar = new GregorianCalendar();
            return new URI(uri + ":_" + dateFormat.format(calendar.getTime()) + "-" + UUID.randomUUID());

        } catch (java.net.URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void executeUpdate(String rulString) throws PersistenceException {
        try {
            connection.executeUpdate(rulString);
        } catch (RQULException rqule) {
            throw new PersistenceException(rqule);
        }
    }

    public Graph executeQuery(String rqlString) throws PersistenceException {
        try {
            return connection.executeQuery(rqlString);
        } catch (RQULException rqule) {
            throw new PersistenceException(rqule);
        }
    }
}
