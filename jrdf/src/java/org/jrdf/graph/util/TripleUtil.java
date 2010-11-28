/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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
 *
 */

package org.jrdf.graph.util;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import java.util.Set;

/**
 * Provides methods for returning all the triples in a given graph for a given node including the blank nodes that are
 * found in any of the triples.  So a graph <u1> <u1> <_1>, <_1> <u2> <_2> will return the all of triples if you call
 * getAllTriplesForSubjectNode(<u1>, graph) - as the first triple has _1 in it and the blank node _1 in the first
 * triple is found in the second.
 */
public interface TripleUtil {
    /**
     * Get all the triples (including any blank nodes that are found) for a given node.
     *
     * @param graph the graph to search
     * @param node to find(node, *, *), find (*, node, *), find (*, *, node)
     * @return a set of triples for the node and any blank nodes found in any triples it is found in.
     * @throws GraphException
     */
    Set<Triple> getAllTriplesForNode(Graph graph, Node node) throws GraphException;

    /**
     * Get all triples (including any blank nodes that are found) for a given subject node.
     *
     * @param graph the graph to search
     * @param subjectNode to find(subjectNode, *, *)
     * @return a set of triples that contain the subject node and any blank nodes found in any triples it is found in.
     * @throws GraphException if there is an exception finding the nodes in the graph.
     */
    Set<Triple> getAllTriplesForSubjectNode(Graph graph, SubjectNode subjectNode) throws GraphException;

    /**
     * Get all triples (including any blank nodes that are found) for a given object node.
     *
     * @param graph the graph to search
     * @param objectNode to find(*, *, objectNode)
     * @return a set of triples contain the object node and any blank nodes found in any triples it is found in.
     * @throws GraphException if there is an exception finding the nodes in the graph.
     */
    Set<Triple> getAllTriplesForObjectNode(Graph graph, ObjectNode objectNode) throws GraphException;
}
