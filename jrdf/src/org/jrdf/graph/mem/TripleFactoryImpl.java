/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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
import org.jrdf.vocabulary.*;
import org.jrdf.util.ClosableIterator;  // used by reification only

// Java 2 standard packages
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A Node Factory is a class which create the various components of a graph.
 * It is tied to a specific instance of GraphImpl.
 *
 * @author Paul Gearon
 *
 * @version $Revision$
 */
public class TripleFactoryImpl implements TripleFactory {

  /**
   * The graph that this factory constructs nodes for.
   */
  private Graph graph;

  /**
   * The graph element factory.
   */
  private GraphElementFactory elementFactory;

  /**
   * Package scope constructor.
   *
   * @param graph The GraphImpl that this class is attached to.
   */
  TripleFactoryImpl(GraphImpl graph, GraphElementFactory elementFactory)
      throws TripleFactoryException {
    this.graph = graph;
    this.elementFactory = elementFactory;
  }

  /**
   * Reifies a triple.  A triple made up of the first three nodes is added to
   * graph and the reificationNode is used to reify the triple.
   *
   * @param subjectNode the subject of the triple.
   * @param predicateNode the predicate of the triple.
   * @param objectNode the object of the triple.
   * @param reificationNode a node denoting the reified triple.
   * @throws NodeFactoryException If the resource failed to be created.
   * @throws AlreadyReifiedException If there was already a triple URI for
   *     the given triple.
   */
  public void reifyTriple(SubjectNode subjectNode,
      PredicateNode predicateNode, ObjectNode objectNode,
      SubjectNode reificationNode) throws TripleFactoryException,
      AlreadyReifiedException {

    // create the reification node
    try {
      reallyReifyTriple(subjectNode, predicateNode, objectNode, reificationNode);
    }
    catch (GraphElementFactoryException gefe) {
      throw new TripleFactoryException(gefe);
    }
  }

  /**
   * Creates a reification of a triple.  The triple added to the graph and the
   * reificationNode is used to reify the triple.
   *
   * @param triple the triple to be reified.
   * @param reificationNode a node denoting the reified triple.
   * @throws NodeFactoryException If the resource failed to be created.
   * @throws AlreadyReifiedException If there was already a triple URI for
   *     the given triple.
   */
  public void reifyTriple(Triple triple, SubjectNode reificationNode)
      throws TripleFactoryException, AlreadyReifiedException {

    try {
      reallyReifyTriple(triple.getSubject(), triple.getPredicate(),
          triple.getObject(), reificationNode);
    }
    catch (GraphElementFactoryException gefe) {
      throw new TripleFactoryException(gefe);
    }
  }

  public void insertContainer(SubjectNode subjectNode,
      PredicateNode predicateNode, ObjectNode object, Container container)
      throws TripleFactoryException {

  }

  public void insertContainer(Triple triple, Container container)
      throws TripleFactoryException {

  }

  public void insertCollection(SubjectNode subjectNode,
      PredicateNode predicateNode, ObjectNode object, Collection collection)
      throws TripleFactoryException {

  }

  public void insertCollection(Triple triple, Collection collection)
      throws TripleFactoryException {

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
  private Node reallyReifyTriple(
      SubjectNode subjectNode, PredicateNode predicateNode,
      ObjectNode objectNode, Node ru
  ) throws GraphElementFactoryException, AlreadyReifiedException {

    // get the nodes used for reification
    URIReference hasSubject = elementFactory.createResource(RDF.SUBJECT);
    URIReference hasPredicate = elementFactory.createResource(RDF.PREDICATE);
    URIReference hasObject = elementFactory.createResource(RDF.OBJECT);

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
      throw new GraphElementFactoryException(e);
    }

    // return the ru to make it easier for returning the value from this method
    return ru;
  }
}
