/*
 * $Header$
 * $Revision$
 * $Date$
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
package org.jrdf.graph.local.iterator;

import org.jrdf.collection.IteratorTrackingCollectionFactory;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.List;
import java.util.SortedSet;

/**
 * An ordered version of the iterator factory that sorts all results first.
 */
public final class OrderedIteratorFactoryImpl implements IteratorFactory {
    private final Localizer localizer;
    private final List<GraphHandler> graphHandlers;
    private final IteratorTrackingCollectionFactory collectionFactory;

    public OrderedIteratorFactoryImpl(Localizer newLocalizer, List<GraphHandler> newGraphHandlers,
        IteratorTrackingCollectionFactory newCollectionFactory) {
        checkNotNull(newLocalizer, newGraphHandlers, newCollectionFactory);
        this.localizer = newLocalizer;
        this.graphHandlers = newGraphHandlers;
        this.collectionFactory = newCollectionFactory;
    }

    public ClosableIterator<Triple> newEmptyClosableIterator() {
        return new TripleEmptyClosableIterator();
    }

    public ClosableIterator<Triple> newGraphIterator() {
        return sortTriples(new GraphIterator(graphHandlers.get(0)));
    }

    public ClosableIterator<Triple> newOneFixedIterator(Long fixedFirstNode, int index) {
        return sortTriples(new OneFixedIterator(fixedFirstNode, graphHandlers.get(index)));
    }

    public ClosableIterator<Triple> newTwoFixedIterator(Long fixedFirstNode, Long fixedSecondNode, int index) {
        return sortTriples(new TwoFixedIterator(fixedFirstNode, fixedSecondNode, graphHandlers.get(index)));
    }

    public ClosableIterator<Triple> newThreeFixedIterator(Long[] nodes) {
        return new ThreeFixedIterator(nodes, graphHandlers.get(0));
    }

    public ClosableIterator<PredicateNode> newPredicateIterator() {
        return sortPredicates(new AnyResourcePredicateIterator(graphHandlers.get(1)));
    }

    public ClosableIterator<PredicateNode> newPredicateIterator(Long resource) {
        return sortPredicates(new FixedResourcePredicateIterator(resource, graphHandlers.get(0), graphHandlers.get(1)));
    }

    public IteratorFactory getUnsortedIteratorFactory() {
        return new LocalIteratorFactory(graphHandlers);
    }

    private ClosableIterator<Triple> sortTriples(ClosableIterator<Triple> closableIterator) {
        SortedSet<Triple> orderedSet = collectionFactory.createSet(Triple.class);
        while (closableIterator.hasNext()) {
            orderedSet.add(closableIterator.next());
        }
        closableIterator.close();
        return new TripleClosableIterator(collectionFactory, orderedSet.iterator(), localizer, graphHandlers.get(0));
    }

    private ClosableIterator<PredicateNode> sortPredicates(ClosableIterator<PredicateNode> closableIterator) {
        SortedSet<PredicateNode> orderedSet = collectionFactory.createSet(PredicateNode.class);
        while (closableIterator.hasNext()) {
            orderedSet.add(closableIterator.next());
        }
        closableIterator.close();
        return new PredicateClosableIterator(collectionFactory, orderedSet.iterator());
    }
}
