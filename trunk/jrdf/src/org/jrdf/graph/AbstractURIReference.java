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

package org.jrdf.graph;

// Java 2 standard packages
import java.net.URI;

/**
 * A base implementation of an RDF {@link URIReference}.
 *
 * @author <a href="http://staff.pisoftware.com/raboczi">Simon Raboczi</a>
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public abstract class AbstractURIReference implements URIReference {

  /**
   * The URI of the node.
   */
  protected URI uri;

  /**
   * Constructor.
   *
   * Enforces a non-<code>null</code> and absolute <var>uri</var> parameter.
   *
   * @param uri the URI to use in creation.
   * @throws IllegalArgumentException if <var>uri</var> is <code>null</code> or
   *     not absolute
   */
  protected AbstractURIReference(URI uri) {

    // Validate "uri" parameter
    if (uri == null) {
      throw new IllegalArgumentException("Null \"uri\" parameter");
    }

    if (!uri.isAbsolute()) {
      throw new IllegalArgumentException("\""+uri+"\" is not absolute");
    }

    // Initialize the field
    this.uri = uri;
  }

  /**
   * Constructor.
   *
   * Enforces a non-<code>null</code> parameter.  Use only for applications
   * where enforcement of valid URIs is too expensive or not necessary.
   *
   * @param uri the URI to use in creation.
   * @param validate whether to enforce valid RDF URIs.
   * @throws IllegalArgumentException if <var>uri</var> is not absolute and
   *   validate is true.
   */
  protected AbstractURIReference(URI uri, boolean validate) {

    // Validate "uri" parameter
    if (uri == null) {
      throw new IllegalArgumentException("Null \"uri\" parameter");
    }

    if (validate && !uri.isAbsolute()) {
      throw new IllegalArgumentException("\""+uri+"\" is not absolute");
    }

    // Initialize the field
    this.uri = uri;
  }

  /**
   * The {@link URI} identifiying this resource.
   *
   * @return the {@link URI} identifying this resource.
   */
  public URI getURI() {
    return uri;
  }

  /**
   * Accept a call from a TypedNodeVisitor.
   *
   * @param visitor the object doing the visiting.
   */
  public void accept(TypedNodeVisitor visitor) {
    visitor.visit(this);
  }

  public boolean equals(Object obj) {
    boolean returnValue = false;

    // Object must not be null
    if (obj != null) {

      try {
        URIReference tmpURIReference = (URIReference) obj;

        // Ensure that both data type URIs are null or are equal by their
        // string values.
        if (getURI().toString().equals(tmpURIReference.getURI().toString())) {
          returnValue = true;
        }
      }
      catch (ClassCastException cce) {

        // Leave return value to be false.
      }
    }

    return returnValue;
  }

  public int hashCode() {
    return uri.hashCode();
  }

  /**
   * Provide a legible representation of a URI reference. Currently, just the
   * URI of the reference.
   *
   * @return the <var>uri</var> property called toString() on.
   */
  public String toString() {
    return uri.toString();
  }
}
