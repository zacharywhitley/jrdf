/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph;

import org.jrdf.graph.local.ReadWriteGraph;
import org.jrdf.vocabulary.RDF;

import java.net.URI;
import static java.net.URI.create;
import java.util.Iterator;

/**
 * The base implementation of the Triple Factory which adds to a given graph
 * reified statements, containers and collections.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractTripleFactory implements TripleFactory {

    /**
     * The graph that this factory constructs nodes for.
     */
    protected ReadWriteGraph graph;

    /**
     * The graph element factory.
     */
    protected GraphElementFactory elementFactory;

    protected AbstractTripleFactory(ReadWriteGraph newGraph, GraphElementFactory newElementFactory) {
        this.graph = newGraph;
        this.elementFactory = newElementFactory;
    }

    public Triple createTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return new TripleImpl(subject, predicate, object);
    }

    public Triple addTriple(URI subject, URI predicate, URI object) throws GraphElementFactoryException {
        return addTriple(subject, predicate, elementFactory.createURIReference(object));
    }

    public Triple addTriple(URI subject, URI predicate, Resource object) throws GraphElementFactoryException {
        return addTriple(subject, predicate, (ObjectNode) object.getUnderlyingNode());
    }

    public Triple addTriple(URI subject, URI predicate, String object) throws GraphElementFactoryException {
        return addTriple(subject, predicate, elementFactory.createLiteral(object));
    }

    public Triple addTriple(URI subject, URI predicate, Object object) throws GraphElementFactoryException {
        return addTriple(subject, predicate, elementFactory.createLiteral(object));
    }

    public Triple addTriple(URI subject, URI predicate, String object, String language)
        throws GraphElementFactoryException {
        return addTriple(subject, predicate, elementFactory.createLiteral(object, language));
    }

    public Triple addTriple(URI subject, URI predicate, String object, URI dataType)
        throws GraphElementFactoryException {
        return addTriple(subject, predicate, elementFactory.createLiteral(object, dataType));
    }

    private Triple addTriple(URI subject, URI predicate, ObjectNode objectNode) throws GraphElementFactoryException {
        try {
            SubjectNode subjectNode = elementFactory.createURIReference(subject);
            PredicateNode predicateNode = elementFactory.createURIReference(predicate);
            graph.localizeAndAdd(subjectNode, predicateNode, objectNode);
            return new TripleImpl(subjectNode, predicateNode, objectNode);
        } catch (GraphException e) {
            throw new GraphElementFactoryException(e);
        }
    }

    public void reifyTriple(SubjectNode subjectNode, PredicateNode predicateNode, ObjectNode objectNode,
        SubjectNode reificationNode) throws TripleFactoryException {

        // create the reification node
        try {
            reallyReifyTriple(subjectNode, predicateNode, objectNode, reificationNode);
        } catch (GraphElementFactoryException gefe) {
            throw new TripleFactoryException(gefe);
        }
    }

    public void reifyTriple(Triple triple, SubjectNode reificationNode) throws TripleFactoryException {
        try {
            reallyReifyTriple(triple.getSubject(), triple.getPredicate(), triple.getObject(), reificationNode);
        } catch (GraphElementFactoryException gefe) {
            throw new TripleFactoryException(gefe);
        }
    }

    private SubjectNode reallyReifyTriple(SubjectNode subjectNode, PredicateNode predicateNode, ObjectNode objectNode,
        SubjectNode reificationNode) throws GraphElementFactoryException, AlreadyReifiedException {

        // get the nodes used for reification
        PredicateNode hasSubject = elementFactory.createURIReference(RDF.SUBJECT);
        PredicateNode hasPredicate = elementFactory.createURIReference(RDF.PREDICATE);
        PredicateNode hasObject = elementFactory.createURIReference(RDF.OBJECT);
        URIReference rdfType = elementFactory.createURIReference(RDF.TYPE);
        URIReference rdfStatement = elementFactory.createURIReference(RDF.STATEMENT);

        // assert that the statement is not already reified
        try {

            // An error if ru already reifies anything but the given s, p, o.
            if (graph.contains(reificationNode, rdfType, rdfStatement) &&
                !(graph.contains(reificationNode, hasSubject, (ObjectNode) subjectNode) &&
                    graph.contains(reificationNode, hasPredicate, (ObjectNode) predicateNode) &&
                    graph.contains(reificationNode, hasObject, objectNode))) {
                throw new AlreadyReifiedException("Node: " + reificationNode + " already used in reification");
            }

            // insert the reification statements
            graph.localizeAndAdd(reificationNode, rdfType, rdfStatement);
            graph.localizeAndAdd(reificationNode, hasSubject, (ObjectNode) subjectNode);
            graph.localizeAndAdd(reificationNode, hasPredicate, (ObjectNode) predicateNode);
            graph.localizeAndAdd(reificationNode, hasObject, objectNode);
        } catch (GraphException e) {
            throw new GraphElementFactoryException(e);
        }

        // return the ru to make it easier for returning the value from this method
        return reificationNode;
    }

    public void addAlternative(SubjectNode subjectNode, Alternative alternative) throws TripleFactoryException {
        try {
            graph.localizeAndAdd(subjectNode, elementFactory.createURIReference(RDF.TYPE),
                elementFactory.createURIReference(RDF.ALT));
            addContainer(subjectNode, alternative);
        } catch (GraphException e) {
            throw new TripleFactoryException(e);
        }
    }

    public void addBag(SubjectNode subjectNode, Bag bag) throws TripleFactoryException {
        try {
            graph.localizeAndAdd(subjectNode, elementFactory.createURIReference(RDF.TYPE),
                elementFactory.createURIReference(RDF.BAG));
            addContainer(subjectNode, bag);
        } catch (GraphException e) {
            throw new TripleFactoryException(e);
        }
    }

    public void addSequence(SubjectNode subjectNode, Sequence sequence) throws TripleFactoryException {
        try {
            graph.localizeAndAdd(subjectNode, elementFactory.createURIReference(RDF.TYPE),
                elementFactory.createURIReference(RDF.SEQ));
            addContainer(subjectNode, sequence);
        } catch (GraphException e) {
            throw new TripleFactoryException(e);
        }
    }

    private void addContainer(SubjectNode subjectNode, Container container) throws TripleFactoryException {
        try {
            // Insert statements from colletion.
            long counter = 1L;
            for (ObjectNode object : container) {
                final URI uri = create(RDF.BASE_URI + "_" + counter++);
                final URIReference predicateNode = elementFactory.createURIReference(uri);
                graph.localizeAndAdd(subjectNode, predicateNode, object);
            }
        } catch (RuntimeException e) {
            throw new TripleFactoryException(e);
        }
    }

    public void addCollection(SubjectNode firstNode, Collection collection) throws TripleFactoryException {
        try {
            // Constants.
            PredicateNode rdfFirst = elementFactory.createURIReference(RDF.FIRST);
            PredicateNode rdfRest = elementFactory.createURIReference(RDF.REST);
            ObjectNode rdfNil = elementFactory.createURIReference(RDF.NIL);

            // Insert statements from the Colletion using the first given node.
            addElementsToCollection(collection, firstNode, rdfFirst, rdfRest, rdfNil);
        } catch (GraphElementFactoryException e) {
            throw new TripleFactoryException(e);
        } catch (GraphException e) {
            throw new TripleFactoryException(e);
        }
    }

    private void addElementsToCollection(Collection collection, SubjectNode subject, PredicateNode rdfFirst,
        PredicateNode rdfRest, ObjectNode rdfNil) throws GraphException {
        // Iterate through all elements in the Collection.
        Iterator<ObjectNode> iter = collection.iterator();
        SubjectNode currentSubjectNode = subject;
        while (iter.hasNext()) {

            // Get the next object and create the new FIRST statement.
            ObjectNode object = iter.next();
            graph.localizeAndAdd(currentSubjectNode, rdfFirst, object);

            // Check if there are any more elements in the Collection.
            if (iter.hasNext()) {
                // Create a new blank node, link the existing subject to it using the REST predicate.
                ObjectNode newSubject = elementFactory.createBlankNode();
                graph.localizeAndAdd(subject, rdfRest, newSubject);
                currentSubjectNode = (SubjectNode) newSubject;
            } else {
                // If we are at the end of the list link the existing subject to NIL using the REST predicate.
                graph.localizeAndAdd(currentSubjectNode, rdfRest, rdfNil);
            }
        }
    }
}
