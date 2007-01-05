/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.index.graphhandler;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.TripleFactoryException;
import org.jrdf.graph.index.longindex.LongIndex;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An interface used to make modifications on the internal indexes (012, 120 and 201) of a graph.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface GraphHandler {

    /**
     * As 012, 120 and 201 are symmetrical this can be used to reconstruct either two from any one index.  Using the
     * 012 index it will add entries correctly to 120 (secondIndex) and 201 (thirdIndex), or 120 will make 201
     * (secondIndex) and 012 (thirdIndex) and 201 will produce 120 and 201.
     *
     * @param firstIndex  the first index.
     * @param secondIndex the second index.
     * @param thirdIndex  the third index.
     * @throws org.jrdf.graph.GraphException if the adds fail.
     */
    void reconstructIndices(LongIndex firstIndex, LongIndex secondIndex, LongIndex thirdIndex) throws GraphException;

    /**
     * Returns an iterator over an internal representation of the graph in the fixed order based on the underlying
     * index.
     *
     * @return an iterator over an internal representation of the graph in the fixed order based on the underlying
     *         index.
     */
    Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> getEntries();

    /**
     * Creates the globalized nodes based on the internal representation of the nodes.  This may move to the NodePool
     * interface.
     *
     * @param nodes an array of three triple values to create.
     * @return an array of three nodes.
     * @throws TripleFactoryException if the nodes could not be mapped - the nodes must refer to something that already
     *                                have existed.
     */
    Node[] createTriple(Long[] nodes) throws TripleFactoryException;

    /**
     * Removes a triple from the other indexes of the graph.  For example, if this is the 012 GraphHandler it will
     * remove the 120 and 201.
     *
     * @param currentNodes the array of nodes to remove.
     * @throws GraphException if the nodes do not exist.
     */
    void remove(Long[] currentNodes) throws GraphException;
}