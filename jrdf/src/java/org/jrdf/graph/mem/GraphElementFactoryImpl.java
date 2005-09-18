/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The JRDF Project.  All rights reserved.
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
 */

package org.jrdf.graph.mem;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.util.UuidGenerator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * A SkipListNode Factory is a class which create the various components of a graph.
 * It is tied to a specific instance of GraphImpl.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public class GraphElementFactoryImpl implements GraphElementFactory, NodePool {

    /**
     * Three being the number, and the number being 3.
     */
    private static final int TRIPLE = 3;

    /**
     * The pool of all nodes, mapped from their ids.
     */
    private Map<Long, Node> nodePool;

    /**
     * A reverse mapping of all ids, mapped from their string.
     */
    private Map<String, Long> stringPool;

    /**
     * The next available node id.
     */
    private long nextNode;

    /**
     * Package scope constructor.
     */
    GraphElementFactoryImpl() {
        nodePool = new HashMap<Long, Node>();
        stringPool = new HashMap<String, Long>();
        nextNode = 1;
    }


    /**
     * Create a blank nodes that is associated with a specific graph.
     *
     * @return the newly created blank node value.
     * @throws GraphElementFactoryException If anonymous resources can't be generated.
     */
    public BlankNode createResource() throws GraphElementFactoryException {

        //get an Unique Identifier
        String uid;
        try {
            uid = UuidGenerator.generateUuid();
        } catch (Exception exception) {
            throw new GraphElementFactoryException("Could not generate Unique Identifier for BlankNode.", exception);
        }

        // create the new node
        BlankNode node = new BlankNodeImpl(nextNode, uid);

        // put the node in the pool
        nodePool.put(nextNode, node);

        // go on to the next node id
        nextNode++;
        return node;
    }

    /**
     * Create a URI reference.
     *
     * @param uri The URI of the resource.
     * @return the newly created URI reference value.
     * @throws GraphElementFactoryException If the resource failed to be created.
     */
    public URIReference createResource(URI uri) throws GraphElementFactoryException {
        if (null == uri) {
            throw new GraphElementFactoryException("URI may not be null for a URIReference");
        }

        // check if the node already exists in the string pool
        Long nodeid = getNodeIdByString(uri.toString());

        if (null != nodeid) {
            return (URIReference) getNodeById(nodeid);
        }

        // create the node identifier and increment the node
        nodeid = nextNode++;

        // create the new node
        URIReference node = new URIReferenceImpl(uri, nodeid);

        // put the node in the pool
        nodePool.put(nodeid, node);

        // put the URI string into the pool
        // TODO: This could conflict with a literal
        stringPool.put(uri.toString(), nodeid);
        return node;
    }

    /**
     * Create a URI reference without checking if the URI given is a valid RDF
     * URI, currently if the URI is absolute.
     *
     * @param uri      The URI of the resource.
     * @param validate true if we disbale checking to see if the URI is valid.
     * @return The newly created URI reference value.
     * @throws GraphElementFactoryException
     */
    public URIReference createResource(URI uri, boolean validate) throws GraphElementFactoryException {

        if (null == uri) {
            throw new GraphElementFactoryException("URI may not be null for a URIReference");
        }

        // check if the node already exists in the string pool
        Long nodeid = getNodeIdByString(uri.toString());
        if (null != nodeid) {
            return (URIReference) getNodeById(nodeid);
        }

        // create the node identifier and increment the node
        nodeid = nextNode++;

        // create the new node
        URIReference node = new URIReferenceImpl(uri, validate, nodeid);

        // put the node in the pool
        nodePool.put(nodeid, node);

        // put the URI string into the pool
        // TODO: This could conflict with a literal
        stringPool.put(uri.toString(), nodeid);
        return node;
    }


    /**
     * Creates a new literal with the given lexical value, with no language or
     * datatype.
     *
     * @param lexicalValue The lexical value for the literal.
     * @return the newly created literal value.
     * @throws GraphElementFactoryException If the resource failed to be created.
     */
    public Literal createLiteral(String lexicalValue) throws GraphElementFactoryException {
        LiteralImpl literal = new LiteralImpl(lexicalValue);
        addNodeId(literal);
        return literal;
    }


    /**
     * Creates a new literal with the given lexical value, with a given language
     * but no datatype.
     *
     * @param lexicalValue The lexical value for the literal.  Cannot be null.
     * @param languageType The language of the literal or null if not required.
     * @return the newly created literal value.
     * @throws GraphElementFactoryException If the resource failed to be created.
     */
    public Literal createLiteral(String lexicalValue, String languageType) throws GraphElementFactoryException {
        LiteralImpl newLiteral = new LiteralImpl(lexicalValue, languageType);
        addNodeId(newLiteral);
        return newLiteral;
    }


    /**
     * Creates a new literal with the given lexical value and given datatype.
     *
     * @param lexicalValue The lexical value for the literal.  Cannot be null.
     * @param datatypeURI  The URI of the datatype of the literal or null if not
     *                     required.
     * @return the newly created literal value.
     * @throws GraphElementFactoryException If the resource failed to be created.
     */
    public Literal createLiteral(String lexicalValue, URI datatypeURI) throws GraphElementFactoryException {
        // create the node identifier
        LiteralImpl newLiteral = new LiteralImpl(lexicalValue, datatypeURI);
        addNodeId(newLiteral);
        return newLiteral;
    }


    /**
     * Creates a new node id for the given Literal.  Sets the node id of the
     * given newLiteral.
     *
     * @param newLiteral A newly created newLiteral.
     */
    private void addNodeId(LiteralImpl newLiteral) {

        // find the string identifier for this node
        String strId = newLiteral.getEscapedForm();

        // check if the node already exists in the string pool
        Long tmpNodeId = stringPool.get(strId);

        if (null != tmpNodeId) {

            // return the existing node instead
            newLiteral.setId(tmpNodeId);
        } else {

            // create the node identifier
            newLiteral.setId(nextNode);

            // put the node in the pool
            nodePool.put(nextNode, newLiteral);

            // put the URI string into the pool
            stringPool.put(strId, nextNode);

            // increment the node, since we used it
            nextNode++;
        }
    }


    public Node getNodeById(Long id) {
        return nodePool.get(id);
    }


    public Long getNodeIdByString(String str) {
        return stringPool.get(str);
    }

    /**
     * Package method for adding in a node that was not created by this factory.
     * Used by GraphImpl for deserializing.
     *
     * @param node The node to add.
     * @throws IllegalArgumentException The node conflicts with one already in use.
     */
    void registerNode(MemNode node) {
        // get the id for this node
        Long id = node.getId();

        // look the node up to see if it already exists in the graph
        MemNode existingNode = (MemNode) nodePool.get(id);
        if (null != existingNode) {
            // check that the node is equal to the one that is already in the graph
            if (existingNode.equals(node)) {
                return;
            }
            // node does not match
            throw new IllegalArgumentException("SkipListNode conflicts with one already in the graph");
        }
        // add the node
        nodePool.put(id, node);

        // check if the node has a string representation
        if (!(node instanceof BlankNode)) {

            if (node instanceof Literal) {
                stringPool.put(((Literal) node).getEscapedForm(), node.getId());
            } else {
                stringPool.put(node.toString(), node.getId());
            }
        }

        // update the nextNode counter to a unique number
        if (!(id < nextNode)) {
            nextNode = id + 1;
        }
    }


    /**
     * Package scope method to get all the nodes in the node pool.  Used by GraphImpl for serializing.
     *
     * @return The node pool.
     */
    java.util.Collection<Node> getNodePoolValues() {
        return nodePool.values();
    }

    public Long[] localize(Node first, Node second, Node third) throws GraphException {
        Long[] localValues = new Long[TRIPLE];

        // convert the nodes to local memory nodes for convenience
        localValues[0] = convertSubject(first);
        localValues[1] = convertPredicate(second);
        localValues[2] = convertObject(third);
        return localValues;
    }

    private Long convertSubject(Node first) throws GraphException {
        Long subjectValue = null;
        if (ANY_SUBJECT_NODE != first) {
            if (first instanceof BlankNodeImpl) {
                subjectValue = ((BlankNodeImpl) first).getId();
            } else {
                subjectValue = getNodeIdByString(String.valueOf(first));
            }

            if (null == subjectValue) {
                throw new GraphException("Subject does not exist in graph");
            }
        }

        return subjectValue;
    }

    private Long convertPredicate(Node second) throws GraphException {
        Long predicateValue = null;
        if (ANY_PREDICATE_NODE != second) {
            predicateValue = getNodeIdByString(String.valueOf(second));

            if (null == predicateValue) {
                throw new GraphException("Predicate does not exist in graph");
            }
        }

        return predicateValue;
    }

    private Long convertObject(Node third) throws GraphException {
        Long objectValue = null;
        if (ANY_OBJECT_NODE != third) {
            if (third instanceof BlankNodeImpl) {
                objectValue = ((BlankNodeImpl) third).getId();
            } else if (third instanceof LiteralImpl) {
                objectValue = getNodeIdByString(((LiteralImpl) third).getEscapedForm());
            } else {
                objectValue = getNodeIdByString(String.valueOf(third));
            }

            if (null == objectValue) {
                throw new GraphException("Object does not exist in graph");
            }
        }

        return objectValue;
    }
}
