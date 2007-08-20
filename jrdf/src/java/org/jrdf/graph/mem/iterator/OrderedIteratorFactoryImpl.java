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
package org.jrdf.graph.mem.iterator;

import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.index.graphhandler.GraphHandler;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.mem.TripleComparatorImpl;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.TreeSet;

public final class OrderedIteratorFactoryImpl implements IteratorFactory {
    private final NodeComparator nodeComparator;
    private IteratorFactory iteratorFactory;
    private NodePool nodePool;
    private LongIndex longIndex;
    private GraphHandler graphHandler;

    public OrderedIteratorFactoryImpl(IteratorFactory newIteratorFactory, NodePool newNodePool, LongIndex newLongIndex,
            GraphHandler newGraphHandler, NodeComparator newNodeComparator) {
        checkNotNull(newIteratorFactory, newNodePool, newLongIndex, newGraphHandler, newNodeComparator);
        this.iteratorFactory = newIteratorFactory;
        this.nodePool = newNodePool;
        this.longIndex = newLongIndex;
        this.graphHandler = newGraphHandler;
        this.nodeComparator = newNodeComparator;
    }

    public ClosableMemIterator<Triple> newEmptyClosableIterator() {
        return iteratorFactory.newEmptyClosableIterator();
    }

    public ClosableMemIterator<Triple> newGraphIterator() {
        return sortResults(iteratorFactory.newGraphIterator());
    }

    public ClosableMemIterator<Triple> newOneFixedIterator(Long fixedFirstNode, int index) {
        return sortResults(iteratorFactory.newOneFixedIterator(fixedFirstNode, index));
    }

    public ClosableMemIterator<Triple> newTwoFixedIterator(Long fixedFirstNode, Long fixedSecondNode, int index) {
        return sortResults(iteratorFactory.newTwoFixedIterator(fixedFirstNode, fixedSecondNode, index));
    }

    public ClosableMemIterator<Triple> newThreeFixedIterator(Long[] nodes) {
        return iteratorFactory.newThreeFixedIterator(nodes);
    }

    public ClosableIterator<PredicateNode> newPredicateIterator() {
        return sortResults(iteratorFactory.newPredicateIterator());
    }

    public ClosableIterator<PredicateNode> newPredicateIterator(Long resource) {
        return sortResults(iteratorFactory.newPredicateIterator(resource));
    }

    private ClosableMemIterator<Triple> sortResults(ClosableMemIterator<Triple> closableMemIterator) {
        TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
        TreeSet<Triple> orderedSet = new TreeSet<Triple>(tripleComparator);
        while (closableMemIterator.hasNext()) {
            orderedSet.add(closableMemIterator.next());
        }
        closableMemIterator.close();
        return new TripleClosableIterator(orderedSet.iterator(), nodePool, longIndex, graphHandler);
    }

    private ClosableIterator<PredicateNode> sortResults(ClosableIterator<PredicateNode> closableIterator) {
        TreeSet<PredicateNode> orderedSet = new TreeSet<PredicateNode>(nodeComparator);
        while (closableIterator.hasNext()) {
            orderedSet.add(closableIterator.next());
        }
        closableIterator.close();
        return new PredicateClosableIterator(orderedSet.iterator());
    }
}
