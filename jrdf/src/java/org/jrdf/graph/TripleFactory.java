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

import java.net.URI;

/**
 * A Triple Factory is a class which defines the creation of triples and certain sets of triples.  This includes
 * generating triples for reification, containers and collections.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface TripleFactory {

    /**
     * Creates and adds a new triple to the graph.
     *
     * @param subject The subject of the statement.
     * @param predicate The predicate of the statement.
     * @param object The object of the statement.
     * @return the newly created triple object.
     * @throws GraphElementFactoryException if it fails to create the given subject, predicate object.
     */
    Triple addTriple(URI subject, URI predicate, URI object) throws GraphElementFactoryException;

    /**
     * Creates and adds a new triple to the graph.
     *
     * @param subject The subject of the statement.
     * @param predicate The predicate of the statement.
     * @param object The object of the statement.  Creates a literal without a language or datatype with the given
     *      lexical value.
     * @return the newly created triple object.
     * @throws GraphElementFactoryException if it fails to create the given subject, predicate and object.
     */
    Triple addTriple(URI subject, URI predicate, String object) throws GraphElementFactoryException;

    /**
     * Creates and adds a new triple to the graph.
     *
     * @param subject The subject of the statement.
     * @param predicate The predicate of the statement.
     * @param object The object of the statement.
     * @return the newly created triple object.
     * @throws GraphElementFactoryException if it fails to create the given subject, predicate and object.
     */
    Triple addTriple(URI subject, URI predicate, Resource object) throws GraphElementFactoryException;

    /**
     * Creates and adds a new triple to the graph.
     *
     * @param subject The subject of the statement.
     * @param predicate The predicate of the statement.
     * @param object The native value of the object of the statement.
     * @return the newly created triple object.
     * @throws GraphElementFactoryException if it fails to create the given subject, predicate and object.  The object
     *   must have a type registered in the DatatypeFactory.
     */
    Triple addTriple(URI subject, URI predicate, Object object) throws GraphElementFactoryException;

    /**
     * Creates and adds new triple to the graph.
     *
     * @param subject The subject of the statement.
     * @param predicate The predicate of the statement.
     * @param object The lexical value of the literal.
     * @param language The language of the object.
     * @return the newly created triple object.
     * @throws GraphElementFactoryException if it fails to create the given subject, predicate and object.
     */
    Triple addTriple(URI subject, URI predicate, String object, String language) throws GraphElementFactoryException;

    /**
     * Creates and adds new triple to graph.
     *
     * @param subject The subject of the statement.
     * @param predicate The predicate of the statement.
     * @param object The lexical value of the literal.
     * @param dataType The datatype of the object.
     * @return the newly created triple object.
     * @throws GraphElementFactoryException if it fails to create the given subject, predicate and object.
     */
    Triple addTriple(URI subject, URI predicate, String object, URI dataType) throws GraphElementFactoryException;

    /**
     * Creates a new triple to be used in the graph.  Does not add it to an associated graph.  Use @see Graph#add or
     * #addTriple.
     *
     * @param subject The subject of the statement.
     * @param predicate The predicate of the statement.
     * @param object The object of the statement.
     * @return the newly created triple object.
     */
    Triple createTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object);

    /**
     * Reifies a triple.  4 statements are added with the given subject, predicate and object.  These are added as
     * objects to the the reified triple.  The triple being reified is not added to the graph.
     *
     * @param subjectNode the subject of the triple.
     * @param predicateNode the predicate of the triple.
     * @param objectNode the object of the triple.
     * @param reificationNode a node denoting the reified triple.
     * @throws TripleFactoryException If the resource failed to be added.
     * @throws AlreadyReifiedException If there was already a triple URI for the given triple.
     */
    void reifyTriple(SubjectNode subjectNode, PredicateNode predicateNode, ObjectNode objectNode,
        SubjectNode reificationNode) throws TripleFactoryException;

    /**
     * Reifies a triple.  4 statements are added with the triple's subject, predicate and object.  These are added as
     * objects to the reified triple.  The triple being reified is not added to the graph.
     *
     * @param triple the triple to be reified.
     * @param reificationNode a node denoting the reified triple.
     * @throws TripleFactoryException If the resource failed to be added, for example already reified.
     */
    void reifyTriple(Triple triple, SubjectNode reificationNode) throws TripleFactoryException;

    /**
     * Inserts a alternative using the given subject.  The subject is also the object of a proceeding statement that
     * identifies the container.
     *
     * @param subjectNode the subject node of the triple.
     * @param alternative the alternative to add.
     * @throws TripleFactoryException If the resources were failed to be added.
     */
    void addAlternative(SubjectNode subjectNode, Alternative alternative) throws TripleFactoryException;

    /**
     * Inserts a bag using the given subject.  The subject is also the object of a proceeding statement that
     * identifies the container.
     *
     * @param subjectNode the subject node of the triple.
     * @param bag         the bag to add.
     * @throws TripleFactoryException If the resources were failed to be added.
     */
    void addBag(SubjectNode subjectNode, Bag bag) throws TripleFactoryException;

    /**
     * Inserts a sequence using the given subject.  The subject is also the object of a proceeding statement that
     * identifies the container.
     *
     * @param subjectNode the subject node of the triple.
     * @param sequence    the sequence to add.
     * @throws TripleFactoryException If the resources were failed to be added.
     */
    void addSequence(SubjectNode subjectNode, Sequence sequence) throws TripleFactoryException;

    /**
     * Inserts a collection using the given subject.  The subject is also the object of a proceeding statement that
     * identifies the collection.
     *
     * @param firstNode  the subject node of the triple.
     * @param collection the collection to add.
     * @throws TripleFactoryException If the resources were failed to be added.
     */
    void addCollection(SubjectNode firstNode, Collection collection) throws TripleFactoryException;
}
