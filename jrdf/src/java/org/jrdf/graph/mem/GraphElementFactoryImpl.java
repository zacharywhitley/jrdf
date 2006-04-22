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

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.index.nodepool.mem.NodePoolMem;
import org.jrdf.util.UuidGenerator;

import java.net.URI;

/**
 * A SkipListNode Factory is a class which create the various components of a graph.
 * It is tied to a specific instance of GraphImpl.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public final class GraphElementFactoryImpl implements GraphElementFactory {

    /**
     * The node pool.
     */
    private NodePoolMem nodePool;

    /**
     * Package scope constructor.
     */
    GraphElementFactoryImpl(NodePoolMem nodePool) {
        this.nodePool = nodePool;
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

        // create the node identifier and add
        Long nodeId = nodePool.getNextNodeId();
        BlankNode node = new BlankNodeImpl(nodeId, uid);
        nodePool.registerNode((MemNode) node);
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
        Long nodeId = nodePool.getNodeIdByString(uri.toString());

        if (null != nodeId) {
            return (URIReference) nodePool.getNodeById(nodeId);
        }

        // create the node identifier and add
        nodeId = nodePool.getNextNodeId();
        URIReference node = new URIReferenceImpl(uri, nodeId);
        nodePool.registerNode((MemNode) node);
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
        Long nodeId = nodePool.getNodeIdByString(uri.toString());
        if (null != nodeId) {
            return (URIReference) nodePool.getNodeById(nodeId);
        }

        // create the node identifier and add
        nodeId = nodePool.getNextNodeId();
        URIReference node = new URIReferenceImpl(uri, validate, nodeId);
        nodePool.registerNode((MemNode) node);
        return node;
    }


    /**
     * Creates a new literal with the given lexical value, with no language or
     * datatype.
     *
     * @param lexicalValue The lexical value for the literal.
     * @return the newly created literal value.
     */
    public Literal createLiteral(String lexicalValue) {
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
     */
    public Literal createLiteral(String lexicalValue, String languageType) {
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
     */
    public Literal createLiteral(String lexicalValue, URI datatypeURI) {
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
        Long tmpNodeId = nodePool.getNodeIdByString(strId);

        if (null != tmpNodeId) {

            // return the existing node instead
            newLiteral.setId(tmpNodeId);
        } else {

            // create the node identifier
            Long nextNode = nodePool.getNextNodeId();
            newLiteral.setId(nextNode);
            nodePool.registerNode(newLiteral);
        }
    }

    public NodePoolMem getNodePool() {
        return nodePool;
    }
}
