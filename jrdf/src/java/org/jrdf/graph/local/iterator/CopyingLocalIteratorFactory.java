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

import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.collection.CollectionFactory;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.List;

/**
 * Default implementation of the IteratorFactory.  Simply uses the normal iterators and an in memory backend.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class CopyingLocalIteratorFactory implements IteratorFactory {
    private final GraphHandler[] graphHandlers;
    private final Localizer localizer;
    private final CollectionFactory collectionFactory;

    public CopyingLocalIteratorFactory(final GraphHandler[] newGraphHandlers, final Localizer newLocalizer,
        final CollectionFactory newCollectionFactory) {
        checkNotNull(newGraphHandlers, newLocalizer, newCollectionFactory);
        this.graphHandlers = newGraphHandlers;
        this.localizer = newLocalizer;
        this.collectionFactory = newCollectionFactory;
    }

    public IteratorFactory getUnsortedIteratorFactory() {
        return new LocalIteratorFactory(graphHandlers);
    }

    public ClosableIterator<Triple> newEmptyClosableIterator() {
        return new TripleEmptyClosableIterator();
    }

    public ClosableIterator<Triple> newGraphIterator() {
        return copyTriples(new GraphIterator(graphHandlers[0]));
    }

    public ClosableIterator<Triple> newOneFixedIterator(Long fixedFirstNode, int index) {
        return copyTriples(new OneFixedIterator(fixedFirstNode, graphHandlers[index]));
    }

    public ClosableIterator<Triple> newTwoFixedIterator(Long fixedFirstNode, Long fixedSecondNode, int index) {
        return copyTriples(new TwoFixedIterator(fixedFirstNode, fixedSecondNode, graphHandlers[index]));
    }

    public ClosableIterator<Triple> newThreeFixedIterator(Long[] newNodes) {
        return copyTriples(new ThreeFixedIterator(newNodes, graphHandlers[0]));
    }

    public ClosableIterator<PredicateNode> newPredicateIterator() {
        return new AnyResourcePredicateIterator(graphHandlers[1]);
    }

    public ClosableIterator<PredicateNode> newPredicateIterator(Long resource) {
        return new FixedResourcePredicateIterator(resource, graphHandlers[0], graphHandlers[1]);
    }

    private ClosableIterator<Triple> copyTriples(ClosableIterator<Triple> closableIterator) {
        List<Triple> list = collectionFactory.createList(Triple.class);
        while (closableIterator.hasNext()) {
            list.add(closableIterator.next());
        }
        closableIterator.close();
        return new TripleClosableIterator(list.iterator(), localizer, graphHandlers[0]);
    }
}
