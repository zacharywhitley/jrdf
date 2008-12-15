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
package org.jrdf.graph.local.disk.iterator;

import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.iterator.FixedResourcePredicateIterator;
import org.jrdf.graph.local.iterator.IteratorFactory;
import org.jrdf.graph.local.iterator.TripleEmptyClosableIterator;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.btree.BTree;
import static org.jrdf.util.param.ParameterUtil.*;

/**
 * Default implementation of the IteratorFactory.  Simply uses the normal iterators and an in memory backend.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class DiskIteratorFactory implements IteratorFactory {
    private final GraphHandler[] graphHandlers;
    private final BTree[] trees;

    public DiskIteratorFactory(final LongIndex[] newLongIndexes, final GraphHandler[] newGraphHandlers,
        final NodePool newNodePool, final Localizer newLocalizer, final BTree[] newTrees) {
        checkNotNull(newLongIndexes, newGraphHandlers, newNodePool, newLocalizer, newTrees);
        this.graphHandlers = newGraphHandlers;
        this.trees = newTrees;
    }

    public ClosableIterator<Triple> newEmptyClosableIterator() {
        return new TripleEmptyClosableIterator();
    }

    public ClosableIterator<Triple> newGraphIterator() {
        return new BTreeGraphIterator(trees[0], graphHandlers[0], 0L, 0L, 0L);
    }

    public ClosableIterator<Triple> newOneFixedIterator(Long fixedFirstNode, int index) {
        return new BTreeGraphIterator(trees[index], graphHandlers[index], fixedFirstNode, 0L, 0L);
    }

    public ClosableIterator<Triple> newTwoFixedIterator(Long fixedFirstNode, Long fixedSecondNode, int index) {
        return new BTreeGraphIterator(trees[index], graphHandlers[index], fixedFirstNode, fixedSecondNode, 0L);
    }

    public ClosableIterator<Triple> newThreeFixedIterator(Long[] newNodes) {
        return new BTreeGraphIterator(trees[0], graphHandlers[0], newNodes);
    }

    public ClosableIterator<PredicateNode> newPredicateIterator() {
        return new AnyResourcePredicateIterator(trees[1], graphHandlers[1]);
    }

    public ClosableIterator<PredicateNode> newPredicateIterator(Long resource) {
        return new FixedResourcePredicateIterator(resource, graphHandlers[0], graphHandlers[1]);
    }

    public IteratorFactory getUnsortedIteratorFactory() {
        return this;
    }
}