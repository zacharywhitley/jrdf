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

import org.jrdf.graph.*;
import org.jrdf.util.ClosableIterator;  // used by reification only

// Java 2 standard packages
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * A Node Factory is a class which create the various components of a graph.
 * It is tied to a specific instance of GraphImpl.
 *
 * @author Paul Gearon
 *
 * @version $Revision$
 */
public class NodeFactoryImpl implements NodeFactory {

  /** The predicate describing the subject of a reification. */
  public static final URI REIFICATION_SUBJECT;

  /** The predicate describing the predicate of a reification. */
  public static final URI REIFICATION_PREDICATE;

  /** The predicate describing the object of a reification. */
  public static final URI REIFICATION_OBJECT;

  /** The pool of all nodes, mapped from their ids. */
  private Map nodePool;

  /** A reverse mapping of all ids, mapped from their string. */
  private Map stringPool;

  /** The graph that this factory constructs nodes for. */
  private Graph graph;

  /** The next available node id. */
  private long nextNode;

  static {
    // set up the constants that can throw exceptions
    try {
      REIFICATION_SUBJECT = new URI("jrdf:hasSubject");
      REIFICATION_PREDICATE = new URI("jrdf:hasPredicate");
      REIFICATION_OBJECT = new URI("jrdf:hasObject");
    } catch (URISyntaxException e) {
      throw new RuntimeException("Unexpected problem building constants.", e);
    }
  }

  /**
   * Package scope constructor.
   *
   * @param graph The GraphImpl that this class is attached to.
   */
  NodeFactoryImpl(GraphImpl graph) throws NodeFactoryException {
    this.graph = graph;
    nodePool = new HashMap();
    stringPool = new HashMap();
    nextNode = 1;
  }


  /**
   * Create a blank nodes that is associated with a specific graph.
   *
   * @return A new blank node within the graph.
   * @return the newly created blank node value.
   * @throws NodeFactoryException If anonymous resources can't be generated.
   */
  public BlankNode createResource() throws NodeFactoryException {
    Long id = new Long(nextNode);

    // create the new node
    BlankNode node = new BlankNodeImpl(id);

    // put the node in the pool
    nodePool.put(id, node);

    // go on to the next node id
    nextNode++;
    return node;
  }


  /**
   * Create a URI reference.
   *
   * @param uri The URI of the resource.
   * @return the newly created URI reference value.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public URIReference createResource(URI uri) throws NodeFactoryException {
    if (uri == null) {
      throw new NodeFactoryException("URI may not be null for a URIReference");
    }

    // check if the node already exists in the string pool
    Long nodeid = getNodeIdByString(uri.toString());
    if (nodeid != null) {
      return (URIReference)getNodeById(nodeid);
    }

    // create the node identifier and increment the node
    nodeid = new Long(nextNode++);

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
   * Creates a new literal with the given lexical value, with no language or
   * datatype.
   *
   * @param lexicalValue The lexical value for the literal.
   * @return the newly created literal value.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public Literal createLiteral(String lexicalValue) throws NodeFactoryException {
    LiteralImpl newLiteral = new LiteralImpl(lexicalValue);
    addNodeId(newLiteral);
    return newLiteral;
  }


  /**
   * Creates a new literal with the given lexical value, with a given language
   * but no datatype.
   *
   * @param lexicalValue The lexical value for the literal.  Cannot be null.
   * @param languageType The language of the literal or null if not required.
   * @return the newly created literal value.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public Literal createLiteral(String lexicalValue, String languageType)
      throws NodeFactoryException {
    LiteralImpl newLiteral = new LiteralImpl(lexicalValue, languageType);
    addNodeId(newLiteral);
    return newLiteral;
  }


  /**
   * Creates a new literal with the given lexical value and given datatype.
   *
   * @param lexicalValue The lexical value for the literal.  Cannot be null.
   * @param datatypeURI The URI of the datatype of the literal or null if not
   *     required.
   * @return the newly created literal value.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public Literal createLiteral(String lexicalValue, URI datatypeURI)
      throws NodeFactoryException {
    // create the node identifier
    LiteralImpl newLiteral = new LiteralImpl(lexicalValue, datatypeURI);
    addNodeId(newLiteral);
    return newLiteral;
  }


  /**
   * Creates a new node id for the given Literal.  Sets the node id of the
   * given literal.
   *
   * @param newLiteral A newly created literal.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  private void addNodeId(LiteralImpl literal) throws NodeFactoryException {
    // create the node identifier
    Long nodeId = new Long(nextNode);

    // find the string identifier for this node
    String strId = literal.toString();

    // check if the node already exists in the string pool
    Long tmpNodeId = (Long) stringPool.get(strId);
    if (tmpNodeId != null) {
      // return the existing node instead
      literal = (LiteralImpl) nodePool.get(tmpNodeId);
    }

    // put the node in the pool
    nodePool.put(nodeId, literal);

    // put the URI string into the pool
    stringPool.put(strId, nodeId);

    // increment the node, since we used it
    nextNode++;
    literal.setId(nodeId);
  }


  /**
   * Creates a new triple to be used in the graph.  Does not add it to the
   * graph.  Use @see Graph#add.
   *
   * @param subject The subject of the statement.
   * @param predicate The predicate of the statement.
   * @param object The object of the statement.
   * @return the newly created triple object.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public Triple createTriple(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws NodeFactoryException {
    return new TripleImpl(subject, predicate, object);
  }


  /**
   * Creates a reification of a triple.
   *
   * @param subjectNode the subject of the triple.
   * @param predicateNode the predicate of the triple.
   * @param objectNode the object of the triple.
   * @param reifiedTripleURI a URIReference denoting the reified triple.
   * @throws NodeFactoryException If the resource failed to be created.
   * @throws AlreadyReifiedException If there was already a triple URI for
   *     the given triple.
   */
  public URIReference reifyTriple(
      SubjectNode subjectNode, PredicateNode predicateNode,
      ObjectNode objectNode, URI reifiedTripleURI
  ) throws NodeFactoryException, AlreadyReifiedException {

    // create the reification node
    return (URIReference)reifyTriple(subjectNode, predicateNode, objectNode, createResource(reifiedTripleURI));
  }


  /**
   * Creates a reification of a triple.
   *
   * @param triple the triple to be reified.
   * @param reifiedTripleURI a URIReference denoting the reified triple.
   * @throws NodeFactoryException If the resource failed to be created.
   * @throws AlreadyReifiedException If there was already a triple URI for
   *     the given triple.
   */
  public URIReference reifyTriple(Triple triple, URI reifiedTripleURI)
      throws NodeFactoryException, AlreadyReifiedException {
    return (URIReference)reifyTriple(triple.getSubject(), triple.getPredicate(), triple.getObject(), createResource(reifiedTripleURI));
  }


  /**
   * Creates a reification of a triple.
   *
   * @param subject the subject of the triple.
   * @param predicate the predicate of the triple.
   * @param object the object of the triple.
   * @throws NodeFactoryException If the resource failed to be created or to
   *     be reified.
   */
  public BlankNode reifyTriple(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws NodeFactoryException {
    return (BlankNode)reifyTriple(subject, predicate, object, createResource());
  }


  /**
   * Creates a reification of a triple.
   *
   * @param triple the triple to be reified.
   * @throws NodeFactoryException If the resource failed to be created.
   * @throws AlreadyReifiedException If there was already a triple URI for
   *     the given triple.
   */
  public BlankNode reifyTriple(Triple triple) throws NodeFactoryException {
    return (BlankNode)reifyTriple(triple.getSubject(), triple.getPredicate(), triple.getObject(), createResource());
  }


  /**
   * Creates a reification of a triple.
   *
   * @param subjectNode the subject of the triple.
   * @param predicateNode the predicate of the triple.
   * @param objectNode the object of the triple.
   * @param ru a Node denoting the reified triple.
   * @throws NodeFactoryException If the resource failed to be created.
   * @throws AlreadyReifiedException If there was already a triple URI for
   *     the given triple.
   */
  private Node reifyTriple(
      SubjectNode subjectNode, PredicateNode predicateNode,
      ObjectNode objectNode, Node ru
  ) throws NodeFactoryException, AlreadyReifiedException {

    // get the nodes used for reification
    URIReference hasSubject = createResource(REIFICATION_SUBJECT);
    URIReference hasPredicate = createResource(REIFICATION_PREDICATE);
    URIReference hasObject = createResource(REIFICATION_OBJECT);

    // assert that the statement is not already reified
    try {
      ClosableIterator it = graph.find((SubjectNode)ru, hasSubject, null);
      try {
        if (
            it.hasNext() ||
            graph.contains(subjectNode, predicateNode, objectNode) ||
            graph.contains((SubjectNode)ru, hasSubject, (ObjectNode)subjectNode) &&
            graph.contains((SubjectNode)ru, hasPredicate, (ObjectNode)predicateNode) &&
            graph.contains((SubjectNode)ru, hasObject, objectNode)
        ) {
          throw new AlreadyReifiedException(
              "Triple: " + subjectNode + " " + predicateNode + " " + objectNode
          );
        }
      } finally {
        it.close();
      }

      // insert the reified statement
      graph.add(subjectNode, predicateNode, objectNode);

      // insert the reification statements
      graph.add((SubjectNode)ru, hasSubject, (ObjectNode)subjectNode);
      graph.add((SubjectNode)ru, hasPredicate, (ObjectNode)predicateNode);
      graph.add((SubjectNode)ru, hasObject, (ObjectNode)objectNode);
    } catch (GraphException e) {
      throw new NodeFactoryException(e);
    }

    // return the ru to make it easier for returning the value from this method
    return ru;
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
    MemNode existingNode = (MemNode)nodePool.get(id);
    if (existingNode != null) {
      // check that the node is equal to the one that is already in the graph
      if (existingNode.equals(node)) {
        return;
      }
      // node does not match
      throw new IllegalArgumentException("Node conflicts with one already in the graph");
    }
    // add the node
    nodePool.put(id, node);

    // check if the node has a string representation
    if (!(node instanceof BlankNode)) {
      stringPool.put(node.toString(), node);
    }

    // update the nextNode counter to a unique number
    if (id.longValue() >= nextNode) {
      nextNode = id.longValue() + 1;
    }
  }


  /**
   * Package scope method to get all the nodes in the node pool.  Used by GraphImpl for serializing.
   *
   * @return The node pool.
   */
  Collection getNodePool() {
    return nodePool.values();
  }


  /**
   * Package method to find a node in the node pool by its id.
   *
   * @param id The id of the node to search for.
   * @return The node referred to by the id, null if not found.
   */
  Node getNodeById(Long id) {
    return (Node)nodePool.get(id);
  }


  /**
   * Package method to find a node id based on its string representation.
   *
   * @param str The string representation of a node.
   * @return The id of the node with the given string.
   */
  Long getNodeIdByString(String str) {
    return (Long)stringPool.get(str);
  }

}
