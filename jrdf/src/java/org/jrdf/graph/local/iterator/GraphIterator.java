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

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.NoSuchElementException;

/**
 * An iterator that iterates over an entire graph.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class GraphIterator implements ClosableLocalIterator<Triple> {

    /**
     * The iterator for the first index.
     */
    private ClosableIterator<Long[]> iterator;

    /**
     * Handles the removal of nodes
     */
    private GraphHandler handler;

    /**
     * True if next has been called and it's okay to remove the current value.
     */
    private boolean nextCalled;

    /**
     * The current nodes.
     */
    private Long[] currentNodes;

    /**
     * If the resources have been closed.
     */
    private boolean hasClosed;

    /**
     * Constructor.  Sets up the internal iterators.
     *
     * @throws IllegalArgumentException Must be created with implementations from the memory package.
     */
    public GraphIterator(GraphHandler newHandler) {
        checkNotNull(newHandler);
        // store the node factory
        handler = newHandler;
        iterator = handler.getEntries();
    }

    /**
     * Returns true if the iteration has more elements.
     *
     * @return <code>true</code> If there is an element to be read.
     */
    public boolean hasNext() {
        boolean hasNext = iterator.hasNext();
        if (!hasNext) {
            close();
        }
        return hasNext;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements.
     */
    public Triple next() throws NoSuchElementException {
        if (null == iterator) {
            throw new NoSuchElementException();
        }
        nextCalled = true;
        currentNodes = iterator.next();
        return handler.createTriple(currentNodes);
    }


    /**
     * Implemented for java.util.Iterator.
     */
    public void remove() {
        if (nextCalled) {
            try {
                handler.remove(currentNodes);
            } catch (GraphException ge) {
                IllegalStateException illegalStateException = new IllegalStateException();
                illegalStateException.setStackTrace(ge.getStackTrace());
                throw illegalStateException;
            }
        } else {
            throw new IllegalStateException("Next not called or beyond end of data");
        }
    }


    /**
     * Closes the iterator by freeing any resources that it current holds.
     * Nothing to be done for this class.
     *
     * @return <code>true</code> indicating success.
     */
    public boolean close() {
        if (iterator != null && !hasClosed) {
            iterator.close();
        }
        hasClosed = true;
        return true;
    }
}
