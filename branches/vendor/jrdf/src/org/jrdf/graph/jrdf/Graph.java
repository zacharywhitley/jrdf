/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
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
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */

package org.jrdf.graph;

// Java 2 standard packages
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.*;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * An RDF Graph. As defined by the
 * <a href="http://www.w3.org/TR/2003/WD-rdf-concepts-20031010"><cite>Resource
 * Description Framework (RDF): Concepts and Abstract Syntax</cite> </a>
 * specification.  An RDF graph is a set of RDF triples.  The set of nodes of
 * an RDF graph is the set of subjects and objects of triples in the graph.
 *
 * @author <a href="http://staff.pisoftware.com/raboczi">Simon Raboczi</a>
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface Graph {

  /**
   * Namespace prefix. This is defined in <a
   * href="http://www.w3.org/TR/rdf-syntax-grammar/">&sect;5.1 of <cite>RDF/XML
   * Syntax Specification (Revised)</cite> </a> .
   */
  public final static String NAMESPACE =
      "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

  /**
   * Test the graph for the occurrence of a statement.
   *
   * @param subject The subject.
   * @param predicate The predicate.
   * @param object The object.
   * @return True if the statement is found in the model, otherwise false.
   * @throws GraphException If there was an error accessing the model.
   */
  public boolean contains(Resource subject, URIReference predicate,
      Node object) throws GraphException;

  /**
   * Test the graph for the occurrence of a statement.
   *
   * @param statement The statement.
   * @return True if the statement is found in the model, otherwise false.
   * @throws GraphException If there was an error accessing the model.
   */
  public boolean contains(Statement statement) throws GraphException;

  /**
   * Adds a statement to the graph.
   *
   * @param subject The subject.
   * @param predicate The predicate.
   * @param object The object.
   * @throws GraphException If the statement can't be made.
   */
  public void add(Resource subject, URIReference predicate, Node object)
      throws GraphException;

  /**
   * Adds a statement to the graph.
   *
   * @param statement The statement.
   * @throws GraphException If the statement can't be made.
   */
  public void add(Statement statement) throws GraphException;

  /**
   * Removes a statement to the graph.
   *
   * @param subject The subject.
   * @param predicate The predicate.
   * @param object The object.
   * @throws GraphException If there was an error revoking the statement, for
   *     example if it didn't exist.
   */
  public void remove(Resource subject, URIReference predicate, Node object)
      throws GraphException;

  /**
   * Removes a statement to the graph.
   *
   * @param statement The statement.
   * @throws GraphException If there was an error revoking the statement, for
   *     example if it didn't exist.
   */
  public void remove(Statement statement) throws GraphException;

  /**
   * Returns the node factory for the graph, or creates one.
   *
   * @return the node factory for the graph, or creates one.
   */
  public NodeFactory getNodeFactory();
}
