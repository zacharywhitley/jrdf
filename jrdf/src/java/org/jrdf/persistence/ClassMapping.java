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

import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.persistence.lazy.LazyCollection;
import org.jrdf.persistence.lazy.LazyList;
import org.jrdf.persistence.lazy.LazySet;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Bednar
 * @author Jozef Wagner, http://wagjo.com/
 */
class ClassMapping {
    protected Class entityClass;
    protected String namespace;
    protected URI[] typeURIs;
    protected PropertyMapping urid;
    protected Set<PropertyMapping> properties;

    public ClassMapping(Class entityClass, String namespace, URI[] typeURIs, PropertyMapping URIreference,
        Set<PropertyMapping> properties) {
        this.entityClass = entityClass;
        this.namespace = namespace;
        this.typeURIs = typeURIs;
        this.urid = URIreference;
        this.properties = properties;
    }

    public Object newInstance(URI uri) throws PersistenceException {
        try {
            Object instance = entityClass.newInstance();
            setURI(instance, uri);
            return instance;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public URI getURI(Object obj) {
        return (URI) urid.getValue(obj);
    }

    public void setURI(Object obj, URI uri) {
        urid.setValue(obj, uri);
    }

    public List<Triple> toTriples(Object entity, EntityManager context) throws PersistenceException {
        List<Triple> triples = new LinkedList<Triple>();
        URI uri = getURI(entity);

        for (PropertyMapping property : properties) {
            Object value = property.getValue(entity);

            if (value instanceof Collection) {
                Collection values = (Collection) value;
                if (values.isEmpty()) {
                    triples.add(createTriple(uri, property, null, context));
                } else {
                    if (values instanceof LazyCollection) {
                        values = ((LazyCollection) values).elements();
                    }
                    for (Object elm : values) {
                        triples.add(createTriple(uri, property, elm, context));
                    }
                }
            } else {
                triples.add(createTriple(uri, property, value, context));
            }
        }

        return triples;
    }

    public void copyValues(Object entity, Resource resource, EntityManager context) throws PersistenceException {

        for (PropertyMapping property : properties) {
            Collection values = property.isInverse() ?
                getInversePropertyValues(resource, property, context) :
                getPropertyValues(resource, property, context);

            Object value = property.isCollection() ? values :
                values.isEmpty() ? null : values.iterator().next();

            property.setValue(entity, value);
        }
    }

    private static Triple createTriple(URI subject, PropertyMapping property, Object object, EntityManager context)
        throws PersistenceException {
        URI predicate = property.getURI();

        if (object == null) {
            return !property.isInverse() ?
                new TripleImpl(subject, predicate, null) :
                new TripleImpl(null, predicate, subject);
        }

        if (RDFIntrospector.isRDFResource(object.getClass())) {
            ClassMapping cm = RDFIntrospector.getMappings(object);
            URI uri = cm.getURI(object);

            if (uri == null) {
                context.persist(object);
                uri = cm.getURI(object);
            }

            return !property.isInverse() ?
                new TripleImpl(subject, predicate, uri) :
                new TripleImpl(uri, predicate, subject);
        } else {
            return new TripleImpl(subject, predicate, object);
        }
    }

    // TODO AN Fix this?
    private static class TripleImpl implements Triple {
        private static final long serialVersionUID = 99L;
        URIReference subject;
        URIReference predicate;
        ObjectNode object;

        private TripleImpl() {
        }

        public TripleImpl(URI subject, URI predicate, Object object) {
//            if (subject != null) {
//                this.subject = new URIReferenceImpl(subject);
//            }
//            this.predicate = new URIReferenceImpl(predicate);
//            if (object != null) {
//                this.object = object instanceof URI ? new URIReferenceImpl((URI)object) :
//                    LiteralFactory.createLiteral(object);
//            }
        }

        public SubjectNode getSubject() {
            return subject;
        }

        public PredicateNode getPredicate() {
            return predicate;
        }

        public ObjectNode getObject() {
            return object;
        }

        public boolean isGrounded() {
            return true;
        }

        public String toString() {
            return getSubject() + " " + getPredicate() + " " + getObject();
        }
    }

    private static Collection getPropertyValues(Resource resource, PropertyMapping property, EntityManager context)
        throws PersistenceException {
        Collection values;
        if (property.getPropertyType() == Set.class) {
            values = new HashSet();
        } else {
            values = new ArrayList();
        }
        Class targetClass = property.getTargetEntityType();
        URI predicate = property.getURI();
        if (RDFIntrospector.isRDFResource(targetClass) || targetClass == URI.class) {
            //ClosableIterator<ObjectNode> iter = resource.getObjects(new URIReferenceImpl(predicate));
            ClosableIterator<ObjectNode> iter = null;
            while (iter.hasNext()) {
                ObjectNode node = iter.next();
                values.add(((Resource) node).getURI());
            }
            if (targetClass != URI.class) {
                if (property.getPropertyType() == Set.class) {
                    values = new LazySet(values, targetClass, context);
                } else {
                    values = new LazyList(values, targetClass, context);
                }
            }
        } else {
            //ClosableIterator<ObjectNode> iter = resource.getObjects(predicate);
            ClosableIterator<ObjectNode> iter = null;
            while (iter.hasNext()) {
                ObjectNode node = iter.next();
                // If program throws exception here, you don't have
                // targetEntity=XXX.class annotation property
                // in List field. It should be, for example:
                //     @Property(name="#subTask", targetEntity=Task.class)
                //     protected List<Task> subTasks;
                values.add(((Literal) node).getValue());
            }
        }

        return values;
    }

    private static Collection getInversePropertyValues(Resource resource, PropertyMapping property,
        EntityManager context) throws PersistenceException {
        Class targetClass = property.getTargetEntityType();

        Query q = context.createNativeQuery(
            Statements.createInversePropertyStatement(resource.getURI(),
                property.getURI()),
            targetClass);

        Collection values;
        if (property.getPropertyType() == Set.class) {
            values = q.getResultSet();
        } else {
            values = q.getResultList();
        }
        if (targetClass == java.net.URI.class) {
            if (property.getPropertyType() == Set.class) {
                values = new HashSet<URI>(values);
            } else {
                values = new ArrayList<URI>(values);
            }
        }

        return values;
    }

}
