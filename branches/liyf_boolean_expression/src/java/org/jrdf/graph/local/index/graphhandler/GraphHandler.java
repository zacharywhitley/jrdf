/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.local.index.graphhandler;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.util.ClosableIterator;

/**
 * An interface used to make modifications on the internal indexes (012, 120 and 201) of a graph.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface GraphHandler {

    /**
     * Returns an array of results from the index.  For example, a given subject id is given and the array of predicates
     * and objects.
     *
     * @param first the entry to find.
     * @return an array containing the results.
     */
    ClosableIterator<Long[]> getSubIndex(Long first);

    /**
     * Returns an array of results from the index.  For example, a given subject and predicate ids  the array of
     * objects is returned.
     *
     * @param first the entry to find.
     * @param second the entry to find.
     * @return an array containing the results.
     */
    ClosableIterator<Long> getSubSubIndex(Long first, Long second);

    /**
     * Removes the given entry of long to set of longs with the given entry.  For example, a given subject id is
     * given and it will remove all the associated predicate and objects for that subject.
     *
     * @param first the entry set to remove.
     * @return true if the entry set was non-null.
     */
    boolean removeSubIndex(Long first);

    /**
     * Returns an iterator over an internal representation of the graph in the fixed order based on the underlying
     * index.
     *
     * @return an iterator over an internal representation of the graph in the fixed order based on the underlying
     *         index.
     */
    ClosableIterator<Long[]> getEntries();

    /**
     * Creates the globalized nodes based on the internal representation of the nodes.  This may move to the NodePool
     * interface.
     *
     * @param nodes an array of three triple values to create.
     * @return an array of three nodes.
     */
    Triple createTriple(Long... nodes);

    /**
     * Creates a globalized PredicateNode.
     *
     * @param node the internal node number to convert.
     * @return the PredicateNode.
     */
    PredicateNode createPredicateNode(Long node);

    /**
     * Removes a triple from the other indexes of the graph.  For example, if this is the 012 GraphHandler it will
     * remove the 120 and 201.
     *
     * @param nodes the array of nodes to remove.
     * @throws GraphException if the nodes do not exist.
     */
    void remove(Long... nodes) throws GraphException;
}