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

package org.jrdf.graph;

// Java 2 standard packages
import java.net.URI;

/**
 * A Node Factory is a class which create the various components of a graph.
 * It is generally tied to a specific graph.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface NodeFactory {

  /**
   * Create a blank nodes that is associated with a specific graph.
   *
   * @return A new blank node within the graph.
   * @return the newly created blank node value.
   * @throws NodeFactoryException If anonymous resources can't be generated.
   */
  public BlankNode createResource() throws NodeFactoryException;

  /**
   * Create a URI reference.
   *
   * @param uri The URI of the resource.
   * @return the newly created URI reference value.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public URIReference createResource(URI uri) throws NodeFactoryException;

  /**
   * Creates a new literal with the given lexical value, with no language or
   * datatype.
   *
   * @param lexicalValue The lexical value for the literal.
   * @return the newly created literal value.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public Literal createLiteral(String lexicalValue) throws NodeFactoryException;

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
      throws NodeFactoryException;

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
      throws NodeFactoryException;

  /**
   * Creates a new literal with the given lexical value, with a given language
   * and given datatype.
   *
   * @param lexicalValue The lexical value for the literal.  Cannot be null.
   * @param languageType The language of the literal or null if not required.
   * @param datatypeURI The URI of the datatype of the literal or null if not
   *     required.
   * @return the newly created literal value.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public Literal createLiteral(String lexicalValue, String languageType,
      URI datatypeURI) throws NodeFactoryException;

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
      ObjectNode object) throws NodeFactoryException;

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
  public URIReference reifyTriple(SubjectNode subjectNode,
      PredicateNode predicateNode, ObjectNode objectNode, URI reifiedTripleURI)
      throws NodeFactoryException, AlreadyReifiedException;

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
      throws NodeFactoryException, AlreadyReifiedException;

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
      ObjectNode object) throws NodeFactoryException;

  /**
   * Creates a reification of a triple.
   *
   * @param triple the triple to be reified.
   * @throws NodeFactoryException If the resource failed to be created.
   * @throws AlreadyReifiedException If there was already a triple URI for
   *     the given triple.
   */
  public BlankNode reifyTriple(Triple triple) throws NodeFactoryException;
}
