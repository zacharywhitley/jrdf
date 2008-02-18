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
   * @throws NodeFactoryException If anonymous resources can't be generated.
   */
  public BlankNode createResource() throws NodeFactoryException;

  /**
   * Create a URI reference.
   *
   * @param uri The URI of the resource.
   * @throws NodeFactoryException If the resource failed to be created.
   */
  public URIReference createResource(URI uri);

  /**
   * Creates a new literal with the given lexical value, with no language or
   * datatype.
   *
   * @param lexicalValue The lexical value for the literal.
   */
  public Literal createLiteral(String lexicalValue);

  /**
   * Creates a new literal with the given lexical value, with a given language
   * but no datatype.
   *
   * @param lexicalValue The lexical value for the literal.  Cannot be null.
   * @param languageType The language of the literal or null if not required.
   */
  public Literal createLiteral(String lexicalValue, String languageType);

  /**
   * Creates a new literal with the given lexical value, with a given language
   * and given datatype.
   *
   * @param lexicalValue The lexical value for the literal.  Cannot be null.
   * @param languageType The language of the literal or null if not required.
   * @param datatypeURI The URI of the datatype of the literal or null if not
   *     required.
   */
  public Literal createLiteral(String lexicalValue, String languageType,
      URI datatypeURI);

  /**
   * Creates a new statement to be used in the graph.  Does not add it to the
   * graph.  Use @see Graph#add.
   *
   * @param subject The subject of the statement.
   * @param predicate The predicate of the statement.
   * @param object The object of the statement.
   */
  public Statement createStatement(Resource subject, URIReference predicate,
      Node object);
}