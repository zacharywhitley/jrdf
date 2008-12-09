/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.graph.local;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.ExternalBlankNodeException;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.iterator.ClosableLocalIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Iterator;

public class WritableGraphImpl implements WritableGraph {
    private static final String MESSAGE = "Failed to add triple.";
    private final NodePool nodePool;
    private final LongIndex[] longIndexes;
    private final Localizer localizer;

    public WritableGraphImpl(LongIndex[] newLongIndexes, NodePool newNodePool, Localizer newLocalizer) {
        checkNotNull(newLongIndexes, newNodePool, newLocalizer);
        this.longIndexes = newLongIndexes;
        this.nodePool = newNodePool;
        this.localizer = newLocalizer;
    }

    public void localizeAndRemove(SubjectNode subject, PredicateNode predicate, ObjectNode object)
        throws GraphException {
        // Get local node values also tests that it's a valid subject, predicate and object.
        Long[] values = localizer.localize(subject, predicate, object);
        longIndexes[0].remove(values[0], values[1], values[2]);
        try {
            // if the first one succeeded then try and attempt removal on both of the others
            longIndexes[1].remove(values[1], values[2], values[0]);
        } finally {
            longIndexes[2].remove(values[2], values[0], values[1]);
        }
        for (Long value : values) {
            if (nodeFreed(value)) {
                nodePool.removeNode(value);
            }
        }
    }

    public void removeIterator(Iterator<Triple> triples) throws GraphException {
        if (triples instanceof ClosableLocalIterator) {
            localIteratorRemove(triples);
        } else {
            globalIteratorRemove(triples);
        }
    }

    private boolean nodeFreed(Long value) {
        return !longIndexes[0].contains(value) && !longIndexes[1].contains(value) && !longIndexes[2].contains(value);
    }

    public void localizeAndAdd(SubjectNode subject, PredicateNode predicate, ObjectNode object) throws GraphException {
        // Get local node values also tests that it's a valid subject, predicate
        // and object.
        try {
            Long[] values = localizer.localize(subject, predicate, object);
            longIndexes[0].add(values);
            longIndexes[1].add(values[1], values[2], values[0]);
            longIndexes[2].add(values[2], values[0], values[1]);
        } catch (ExternalBlankNodeException e) {
            throw new ExternalBlankNodeException(MESSAGE, e);
        } catch (GraphException ge) {
            throw new GraphException(MESSAGE, ge);
        }
    }

    public void clear() {
        longIndexes[0].clear();
        longIndexes[1].clear();
        longIndexes[2].clear();
        nodePool.clear();
    }

    private void localIteratorRemove(Iterator<Triple> triples) {
        ClosableLocalIterator<Triple> localIterator = (ClosableLocalIterator<Triple>) triples;
        try {
            while (localIterator.hasNext()) {
                localIterator.next();
                localIterator.remove();
            }
        } finally {
            localIterator.close();
        }
    }

    private void globalIteratorRemove(Iterator<Triple> triples) throws GraphException {
        while (triples.hasNext()) {
            Triple triple = triples.next();
            localizeAndRemove(triple.getSubject(), triple.getPredicate(), triple.getObject());
        }
    }
}
