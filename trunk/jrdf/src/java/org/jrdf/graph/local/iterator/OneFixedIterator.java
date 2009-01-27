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

import static java.util.Arrays.*;
import java.util.NoSuchElementException;

/**
 * An iterator that iterates over a group with a single fixed node.
 * Relies on internal iterators which iterate over all entries in
 * a submap, and the sets they point to.
 * The thirdIndexIterator is used to indicate the current position.
 * It will always be set to return the next value until it reaches
 * the end of the group.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public final class OneFixedIterator implements ClosableLocalIterator<Triple> {

    /**
     * The fixed item.
     */
    private final Long first;

    /**
     * The subIndex of this iterator.  Only needed for initialization and the remove method.
     */
    private ClosableIterator<Long[]> subIndex;

    /**
     * Handles the removal of nodes
     */
    private GraphHandler handler;

    /**
     * The current subject predicate and object, last returned from next().  Only needed by the remove method.
     */
    private Long[] currentNodes;

    /**
     * If the resources have been closed.
     */
    private boolean hasClosed;

    /**
     * Constructor.  Sets up the internal iterators.
     *
     * @throws IllegalArgumentException Must pass in a GraphElementFactory memory implementation.
     */
    public OneFixedIterator(Long fixedFirstNode, GraphHandler newHandler) {
        // store the node factory and other starting data
        handler = newHandler;
        first = fixedFirstNode;
        // find the subIndex from the main index
        subIndex = handler.getSubIndex(first);
    }


    public boolean hasNext() {
        boolean hasNext = subIndex != null && subIndex.hasNext();
        if (!hasNext) {
            close();
        }
        return hasNext;
    }


    public Triple next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        // get back the nodes for these IDs and build the triple
        currentNodes = subIndex.next();
        return handler.createTriple(first, currentNodes[0], currentNodes[1]);
    }

    public void remove() {
        if (currentNodes != null) {
            try {
                handler.remove(first, currentNodes[0], currentNodes[1]);
            } catch (GraphException ge) {
                throw new IllegalStateException(ge.getMessage() + " triple: " + asList(currentNodes));
            }
        } else {
            throw new IllegalStateException("Next not called or beyond end of data");
        }
    }

    public boolean close() {
        if (subIndex != null && !hasClosed) {
            subIndex.close();
        }
        return true;
    }
}
