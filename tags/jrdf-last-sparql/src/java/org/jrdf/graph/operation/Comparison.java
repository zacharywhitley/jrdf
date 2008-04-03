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

package org.jrdf.graph.operation;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;

/**
 * Provides the ability to compare two graph with one another.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface Comparison {
    // TODO AN Extend set operation and modify the API to change how this is used - perhaps by setting "setGrounded".
    // TODO AN Add the ability to test isomorphism and equality.

    /**
     * Returns true if the graph is grounded (does not contain blank nodes).
     *
     * @param g the graph to test.
     * @return true if the graph is grounded (does not contain blank nodes).
     */
    boolean isGrounded(Graph g) throws GraphException;

    /**
     * Return true if both graphs are equivalent (isomorphic) to one another.  That is, that the nodes in one graph map
     * equivalently to nodes in the other.  In a non-grounded graph (ones with blank nodes) nodes can map to other
     * nodes with different values but are equivalent.  This may inclue: &lt;a&gt;, &lt;b&gt;, &lt;c&gt; is equivalient
     * to _x, &lt;b&gt;, &lt;c&gt;, where _x is a blank node or isomorphism could be limited to just blank nodes
     * (not able to stand in place for URIs or Literals).
     *
     * @param g1 The first graph to test.
     * @param g2 The second graph to test.
     * @return true if they are equivalent.
     */
    boolean areIsomorphic(Graph g1, Graph g2) throws GraphException;

    /**
     * Return true if both graphs are equivalent (isomophic) to one another.  These graphs must contain only labelled
     * nodes i.e. no blank nodes.
     *
     * @param g1 The first graph to test.
     * @param g2 The second graph to test.
     * @return true if they are equivalent.
     */
    boolean groundedGraphsAreEqual(Graph g1, Graph g2) throws GraphException;
}