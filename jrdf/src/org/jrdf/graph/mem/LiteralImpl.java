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

// Java 2 standard
import java.net.URI;

// JRDF objects
import org.jrdf.graph.*;

/**
 * RDF literal node.
 *
 * @author Paul Gearon
 *
 * @version $Revision$
 */
public class LiteralImpl extends AbstractLiteral implements MemNode {

  /**
   * The internal identifier for this node.
   */
  private Long id;

  /**
   * A cache of the toString value for efficiency in mapping.
   */
  private String thisString;

  /**
   * Construct a literal.  For use by a NodeFactoryImpl.
   *
   * @param lexicalForm  the text part of the literal
   * @throws IllegalArgumentException if <var>lexicalForm</var> is <code>null</code>
   */
  LiteralImpl(String lexicalForm) {
    // Validate "lexicalForm" parameter
    if (lexicalForm == null) {
      throw new IllegalArgumentException("Null \"lexicalForm\" parameter");
    }

    // Initialize fields
    this.lexicalForm = lexicalForm;
    this.language = "";
    this.datatypeURI = null;
    thisString = super.toString();
  }

  /**
   * Construct a fully general literal.  For use by a NodeFactoryImpl.
   *
   * @param lexicalForm  the text part of the literal
   * @param language  the language code, possibly the empty string but not
   *    <code>null</code>
   * @throws IllegalArgumentException if <var>lexicalForm</var> or
   *    <var>lang</var> are <code>null</code>
   */
  LiteralImpl(String lexicalForm, String language) {
    // Validate "lexicalForm" parameter
    if (lexicalForm == null) {
      throw new IllegalArgumentException("Null \"lexicalForm\" parameter");
    }

    // Validate "language" parameter
    if (language == null) {
      throw new IllegalArgumentException("Null \"language\" parameter");
    }

    // Initialize fields
    this.lexicalForm = lexicalForm;
    this.language = language;
    this.datatypeURI = null;
    thisString = super.toString();
  }


  /**
   * Construct a fully general literal.  For use by a NodeFactoryImpl.
   *
   * @param lexicalForm  the text part of the literal
   * @param datatype  the URI for a datatyped literal, or <code>null</code> for
   *     an untyped literal
   * @param id The internal identifier for this node.
   * @throws IllegalArgumentException if <var>lexicalForm</var> or
   *     <var>lang</var> are <code>null</code>
   */
  LiteralImpl(String lexicalForm, URI datatype) {

    // Validate "lexicalForm" parameter
    if (lexicalForm == null) {
      throw new IllegalArgumentException("Null \"lexicalForm\" parameter");
    }

    // Initialize fields
    this.lexicalForm = lexicalForm;
    this.language = "";
    this.datatypeURI = datatype;
    thisString = super.toString();
  }


  /**
   * Retrieves an internal identifier for this node.
   *
   * @return A numeric identifier for thisa node.
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the interanl identifier for this node.
   *
   * @param newId new id.
   */
  public void setId(Long newId) {
    id = newId;
  }

  /**
   * Provide a legible representation of a literal.  Caches the immutable value
   * so it gets mapped efficiently.
   *
   * @return the <var>lexicalForm</var> property, enclosed in <code>"</code>
   *     characters.
   */
  public String toString() {
    return thisString;
  }
}

