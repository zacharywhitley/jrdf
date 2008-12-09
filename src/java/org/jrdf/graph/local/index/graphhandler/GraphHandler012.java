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
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.Node;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.util.ClosableIterator;

/**
 * Handles operations on 012 index.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class GraphHandler012 extends AbstractGraphHandler {
    private LongIndex index012;
    private LongIndex index120;
    private LongIndex index201;

    public GraphHandler012(LongIndex[] indexes, NodePool newNodePool) {
        this.index012 = indexes[0];
        this.index120 = indexes[1];
        this.index201 = indexes[2];
        this.nodePool = newNodePool;
    }

    public ClosableIterator<Long[]> getSubIndex(Long first) {
        return index012.getSubIndex(first);
    }

    public ClosableIterator<Long> getSubSubIndex(Long first, Long second) {
        return index012.getSubSubIndex(first, second);
    }

    public boolean removeSubIndex(Long first) {
        return index012.removeSubIndex(first);
    }

    public ClosableIterator<Long[]> getEntries() {
        return index012.iterator();
    }

    public Triple createTriple(Long... nodes) {
        final Node subNode = nodePool.getNodeById(nodes[0]);
        //System.err.println("Subject = " + subNode.toString());
        final Node predNode = nodePool.getNodeById(nodes[1]);
        //System.err.println("Predicate = " + predNode.toString());
        final Node objNode = nodePool.getNodeById(nodes[2]);
        //System.err.println("Object = " + objNode.toString());

        SubjectNode subject = (SubjectNode) subNode;
        PredicateNode predicate = (PredicateNode) predNode;
        ObjectNode object = (ObjectNode) objNode;
        return new TripleImpl(subject, predicate, object);
    }

    public void remove(Long... currentNodes) throws GraphException {
        index012.remove(currentNodes[0], currentNodes[1], currentNodes[2]);
        index120.remove(currentNodes[1], currentNodes[2], currentNodes[0]);
        index201.remove(currentNodes[2], currentNodes[0], currentNodes[1]);
    }
}