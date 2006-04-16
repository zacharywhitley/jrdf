/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.mem.iterator;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.TripleFactoryException;
import org.jrdf.graph.index.GraphHandler;
import org.jrdf.graph.index.LongIndex;
import org.jrdf.graph.index.mem.GraphHandler012;
import org.jrdf.graph.index.mem.GraphHandler120;
import org.jrdf.graph.index.mem.GraphHandler201;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An iterator that returns only a single triple, if any exists.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public final class ThreeFixedIterator implements ClosableMemIterator<Triple> {

    /**
     * Fixed set of nodes.
     */
    private Long[] nodes;

    /**
     * Allows access to a particular part of the index.
     */
    private LongIndex longIndex;

    /**
     * Handles the removal of nodes.
     */
    private GraphHandler handler;

    /**
     * The triple to return on.
     */
    private Triple triple;

    /**
     * The triple to remove.
     */
    private Triple removeTriple;

    /**
     * Contains the exception to throw if not null when next is called.
     */
    private TripleFactoryException exception;

    /**
     * Constructor.
     */
    // TODO (AN) This goes back to package private after factory is complete
    ThreeFixedIterator(Long[] newNodes, LongIndex newLongIndex, TripleFactory factory, GraphHandler newHandler) {
        nodes = newNodes;
        longIndex = newLongIndex;
        handler = newHandler;
        createTriple(nodes, newHandler, factory);
    }

    private void createTriple(Long[] longNodes, GraphHandler handler, TripleFactory factory) {
        if (contains(longNodes)) {
            try {
                Node[] nodes = handler.createTriple(longNodes);
                triple = factory.createTriple((SubjectNode) nodes[0], (PredicateNode) nodes[1], (ObjectNode) nodes[2]);
            } catch (TripleFactoryException e) {
                exception = e;
            }
        }
    }

    private boolean contains(Long[] longNodes) {
        Map<Long, Set<Long>> subIndex = longIndex.getSubIndex(longNodes[0]);
        if (subIndex != null) {
            Set<Long> predicates = subIndex.get(longNodes[1]);
            if (predicates.contains(longNodes[2])) {
                return true;
            }
        }
        return false;
    }


    public boolean hasNext() {
        return null != triple;
    }


    public Triple next() throws NoSuchElementException {
        if (null == triple) {
            if (exception != null) {
                throw new NoSuchElementException(exception.getMessage());
            } else {
                throw new NoSuchElementException();
            }
        }

        // return the triple, clearing it first so next will fail on a subsequent call
        removeTriple = triple;
        triple = null;
        return removeTriple;
    }


    public void remove() {
        if (null != removeTriple) {
            try {
                longIndex.remove(nodes);
                handler.remove(nodes);
                removeTriple = null;
            } catch (GraphException ge) {
                throw new IllegalStateException(ge.getMessage());
            }
        } else {
            throw new IllegalStateException("Next not called or beyond end of data");
        }
    }


    public boolean close() {
        return true;
    }

    public boolean containsHandler(GraphHandler012 handler012, GraphHandler201 handler201, GraphHandler120 handler120) {
        return handler012 == handler || handler201 == handler || handler120 == handler;
    }
}