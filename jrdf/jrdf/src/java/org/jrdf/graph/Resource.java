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

package org.jrdf.graph;

import org.jrdf.util.ClosableIterator;

import java.io.Serializable;
import java.net.URI;

/**
 * A resource stands for either a Blank Node or a URI Reference.  This is a convienence interface designed to make it
 * easier to create triples programmatically.
 */
public interface Resource extends URIReference, BlankNode, Serializable {

    /**
     * Returns true if this is a URIReference, otherwise it's a BlankNode.
     *
     * @return true if this is a URIReference, otherwise it's a BlankNode.
     */
    boolean isURIReference();

    /**
     * Add a new triple with this as the subject, with the given predicate and object.
     *
     * @param predicate the existing predicate in the graph to use to create the triple.
     * @param object the existing object in the graph to use to create the triple.
     * @throws GraphException if the predicate or object do not exist in the graph.
     */
    void addValue(PredicateNode predicate, ObjectNode object) throws GraphException;

    /**
     * Add a new triple with this as the subject, with the given predicate and object.
     *
     * @param predicate a new or existing predicate to use to create the triple.
     * @param object a new or existing object to use to create the triple.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void addValue(URI predicate, URI object) throws GraphException;

    /**
     * Add a new triple with this as the subject, with the given predicate and object.  The last parameter is
     * equivalent to calling {@link org.jrdf.graph.GraphElementFactory#createLiteral(String)}
     *
     * @param predicate a new or existing predicate to use to create the triple.
     * @param lexicalValue a new or existing literal value to use to create the triple.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void addValue(URI predicate, String lexicalValue) throws GraphException;

    /**
     * Add a new triple with this as the subject, with the given predicate and object.
     *
     * @param predicate a new or existing predicate to use to create the triple.
     * @param object an existing resource from the graph used to create the triple.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes) or the
     *   Resource was not from this Graph.
     */
    void addValue(URI predicate, Resource object) throws GraphException;

    /**
     * Add a new triple with this as the subject, with the given predicate and object.  The last parameter is
     * equivalent to calling {@link org.jrdf.graph.GraphElementFactory#createLiteral(Object)}
     *
     * @param predicate a new or existing predicate to use to create the triple.
     * @param object a new or existing object to use to create the triple.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void addValue(URI predicate, Object object) throws GraphException;

    /**
     * Add a new triple with this as the subject, with the given predicate and object.  The last two parameters are
     * equivalent to calling {@link org.jrdf.graph.GraphElementFactory#createLiteral(String, String)}
     *
     * @param predicate a new or existing predicate to use to create the triple.
     * @param lexicalValue a new or existing literal value to use to create the triple.
     * @param language the language used to create the literal.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void addValue(URI predicate, String lexicalValue, String language) throws GraphException;

    /**
     * Add a new triple with this as the subject, with the given predicate and object.  The last parameter is
     * equivalent to calling {@link org.jrdf.graph.GraphElementFactory#createLiteral(String, URI)}
     *
     * @param predicate a new or existing predicate to use to create the triple.
     * @param lexicalValue a new or existing literal value to use to create the triple.
     * @param dataType the datatype URI used to create the literal.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void addValue(URI predicate, String lexicalValue, URI dataType) throws GraphException;

    /**
     * Remove any other triples with this as the subject, the given predicate and any object and add a new triple
     * with this as the subject and the given predicate and object.  This means that multiple object values will be
     * replaced by a single object value.
     *
     * @param predicate the existing predicate in the graph to set.
     * @param object the existing object in the graph to set.
     * @throws GraphException if the predicate or object do not exist in the graph.
     */
    void setValue(PredicateNode predicate, ObjectNode object) throws GraphException;

    /**
     * Removes any other triples with this as the subject, the given predicate and any object and add a new triple
     * with this as the subject and the given predicate and object.  This means that multiple object values will be
     * replaced by a single object value.
     *
     * @param predicate the existing predicate in the graph to set.
     * @param object a new or existing object to use to create the triple.
     * @throws GraphException if the predicate or object do not exist in the graph.
     */
    void setValue(URI predicate, URI object) throws GraphException;

    /**
     * Removes any other triples with this as the subject, the given predicate and any object and add a new triple
     * with this as the subject and the given predicate and object.  This means that multiple object values will be
     * replaced by a single object value.
     *
     * @param predicate a new or existing predicate to set.
     * @param lexicalValue a new or existing literal value to use to create the triple.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void setValue(URI predicate, String lexicalValue) throws GraphException;

    /**
     * Removes any other triples with this as the subject, the given predicate and any object and add a new triple
     * with this as the subject and the given predicate and object.  This means that multiple object values will be
     * replaced by a single object value.
     *
     * @param predicate a new or existing predicate to set.
     * @param object an existing resource from the graph to set.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes) or the
     *   Resource was not from this Graph.
     */
    void setValue(URI predicate, Resource object) throws GraphException;

    /**
     * Removes any other triples with this as the subject, the given predicate and any object and add a new triple
     * with this as the subject and the given predicate and object.  This means that multiple object values will be
     * replaced by a single object value.
     *
     * @param predicate a new or existing predicate to set.
     * @param object a new or existing object to set.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void setValue(URI predicate, Object object) throws GraphException;

    /**
     * Removes any other triples with this as the subject, the given predicate and any object and add a new triple
     * with this as the subject and the given predicate and object.  This means that multiple object values will be
     * replaced by a single object value.
     *
     * @param predicate a new or existing predicate to set.
     * @param lexicalValue a new or existing literal value to use to create the triple.
     * @param language the language used to create the literal.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void setValue(URI predicate, String lexicalValue, String language) throws GraphException;

    /**
     * Removes any other triples with this as the subject, the given predicate and any object and add a new triple
     * with this as the subject and the given predicate and object.  This means that multiple object values will be
     * replaced by a single object value.
     *
     * @param predicate a new or existing predicate to set.
     * @param lexicalValue a new or existing literal value to use to create the triple.
     * @param dataType the datatype URI used to create the literal.
     * @throws GraphException if there was an error adding the triple (for example localising the nodes).
     */
    void setValue(URI predicate, String lexicalValue, URI dataType) throws GraphException;

    /**
     * Remove the triple with this as the subject, the given predicate and object.
     *
     * @param predicate the existing predicate in the graph to remove.
     * @param object the existing object in the graph to remove.
     * @throws GraphException if the predicate or object does not exist in the graph.
     */
    void removeValue(PredicateNode predicate, ObjectNode object) throws GraphException;

    void removeValue(URI predicate, URI object) throws GraphException;

    void removeValue(URI predicate, String lexicalValue) throws GraphException;

    void removeValue(URI predicate, Resource object) throws GraphException;

    void removeValue(URI predicate, Object object) throws GraphException;

    void removeValue(URI predicate, String lexicalValue, String language) throws GraphException;

    void removeValue(URI predicate, String lexicalValue, URI dataType) throws GraphException;

    Triple asTriple(PredicateNode predicate, ObjectNode object) throws GraphException;

    Triple asTriple(URI predicate, URI object) throws GraphException;

    Triple asTriple(URI predicate, String lexicalValue) throws GraphException;

    Triple asTriple(URI predicate, Resource object) throws GraphException;

    Triple asTriple(URI predicate, Object object) throws GraphException;

    Triple asTriple(URI predicate, String lexicalValue, String language) throws GraphException;

    Triple asTriple(URI predicate, String lexicalValue, URI dataType) throws GraphException;

    /**
     * Remove all the triples with this as the subject and the given predicate.
     *
     * @param predicate the existing predicate in the graph.
     * @throws GraphException if the predicate does not exist in the graph.
     */
    void removeValues(PredicateNode predicate) throws GraphException;

    void removeValues(URI predicate) throws GraphException;

    /**
     * Remove the triple with this as the object, the given subject and predicate.
     *
     * @param subject the existing subject in the graph to remove.
     * @param predicate the existing predicate in the graph to remove.
     * @throws GraphException if the subject or predicate do not exist in the graph.
     */
    void removeSubject(SubjectNode subject, PredicateNode predicate) throws GraphException;

    void removeSubject(URI subject, URI predicate) throws GraphException;

    /**
     * With this as the subject and using the given predicate return all the objects.
     *
     * @param predicate the existing predicate in the graph to use to find the objects.
     * @return all the objects in the graph with this resource as the subject and the given predicate.
     * @throws GraphException if the subject or predicate do not exist in the graph.
     */
    ClosableIterator<ObjectNode> getObjects(PredicateNode predicate) throws GraphException;

    ClosableIterator<ObjectNode> getObjects(URI predicate) throws GraphException;

    /**
     * With this as the object and using the given predicate return all the subjects.
     *
     * @param predicate the existing predicate in the graph to use to find the subjects.
     * @return all the objects in the graph with this resource as the subject and the given predicate.
     * @throws GraphException if the subject or predicate do not exist in the graph.
     */
    ClosableIterator<SubjectNode> getSubjects(PredicateNode predicate) throws GraphException;

    ClosableIterator<SubjectNode> getSubjects(URI predicate) throws GraphException;

    /**
     * Returns the node that the resource represents - either a BlankNode or a URIReference.
     *
     * @return the BlankNode or URIReference.
     */
    Node getUnderlyingNode();

    boolean containsTriple(PredicateNode predicate, ObjectNode object) throws GraphException;
}
