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

// Java 2 standard
import java.net.URI;

/**
 * A base implementation of an RDF {@link Literal}.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public abstract class AbstractLiteral implements Literal {

  /**
   * The lexical form of the literal.
   */
  protected String lexicalForm = null;

  /**
   * The language code of the literal.
   */
  protected String language = null;

  /**
   * Whether the literal is well formed XML.
   */
  protected boolean wellFormedXML = false;

  /**
   * RDF datatype URI, <code>null</code> for untyped literal.
   */
  protected URI datatypeURI = null;

  /**
   * Obtain the text of this literal.
   *
   * @return the text of the literal, never <code>null</code>
   */
  public String getLexicalForm() {
    return lexicalForm;
  }

  /**
   * Returns the language code of the literal, or <code>null</code> if no
   *     language specified.
   *
   * @return the language code of the literal, or <code>null</code> if no
   *     language specified.
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Whether the literal is well formed XML.
   *
   * @return whether the literal is wll formed XML.
   */
  public boolean isWellFormedXML() {
    return wellFormedXML;
  }

  /**
   * Returns the URI of the RDF datatype of this resource, or <code>null</code>
   *     for an untyped node.
   *
   * @return the URI of the RDF datatype of this resource, or <code>null</code>
   *     for an untyped node.
   */
  public URI getDatatypeURI() {
    return datatypeURI;
  }

  public boolean equals(Object obj) {
    boolean returnValue = false;

    // Object must not be null
    if (obj != null) {
      try {
        Literal tmpLiteral = (Literal) obj;

        // Ensure that the lexical form is equal character by character.
        if (getLexicalForm().equals(tmpLiteral.getLexicalForm())) {

          // Ensure that either both languages are null or are equal.
          if (((getLanguage() == null) && (tmpLiteral.getLanguage() == null)) ||
              (getLanguage().equals(tmpLiteral.getLanguage()))) {

            // Ensure that both data type URIs are null or are equal by their
            // string values.
            if (((getDatatypeURI() == null) && (tmpLiteral.getDatatypeURI() == null)) ||
                (getDatatypeURI().toString().equals(tmpLiteral.getDatatypeURI().toString()))) {
              returnValue = true;
            }
          }
        }
      }
      catch (ClassCastException cce) {

        // Leave return value to be false.
      }
    }

    return returnValue;
  }

  public int hashCode() {
    int hashCode = getLexicalForm().hashCode();

    if (getDatatypeURI() != null) {
      hashCode = hashCode ^ getDatatypeURI().hashCode();
    }

    if (getLanguage() != null) {
      hashCode = hashCode ^ getLanguage().hashCode();
    }

    return hashCode;
  }

  /**
   * Provide a legible representation of a literal. Currently, quotes within the
   * literal aren't correctly escaped.
   *
   * @return the <var>lexicalForm</var> property, enclosed in <code>"</code>
   *     characters.
   */
  public String toString() {
    return '"' + getLexicalForm() + '"';
  }
}