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

package org.jrdf.graph.local.iterator;

import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.local.ResourceFactory;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.NoSuchElementException;

public abstract class ResourceIterator<E> implements ClosableIterator<E> {
    protected final LongIndex longIndex012;
    protected final ResourceFactory resourceFactory;
    protected ClosableIterator<Long[]> iterator012;    //spo iterator
    protected ClosableIterator<Long[]> iterator201;    //osp iterator
    protected Resource nextResource;
    protected boolean firstTime = true;
    protected NodePool nodePool;
    private boolean hasClosed;

    public ResourceIterator(final LongIndex[] newLongIndexes, final ResourceFactory newResourceFactory,
        final NodePool newNodePool) {
        checkNotNull(newLongIndexes, newResourceFactory, newNodePool);
        resourceFactory = newResourceFactory;
        longIndex012 = newLongIndexes[0];
        iterator201 = newLongIndexes[2].iterator();
        iterator012 = newLongIndexes[0].iterator();
        nodePool = newNodePool;
        nextResource = getNextNode();
    }

    public boolean close() {
        if (!hasClosed) {
            iterator201.close();
            iterator012.close();
        }
        hasClosed = true;
        return true;
    }

    public boolean hasNext() {
        boolean hasNext = nextResource != null;
        if (!hasNext) {
            close();
        }
        return hasNext;
    }

    private Resource getNextNode() {
        Resource resource = null;
        if (iterator201.hasNext()) {
            resource = getNextOSPElement();
            if (resource == null) {
                return getNextNode();
            }
        }
        if (iterator012.hasNext()) {
            resource = getNextSPOElement();
        }
        if (resource == null) {
            close();
        }
        return resource;
    }

    /**
     * Get the next object element from the OSP index.
     *
     * @return next object in the osp index.
     */
    private Resource getNextOSPElement() {
        while (iterator201.hasNext()) {
            //Long index = iterator201.next().getKey();
            final Long nodeId = getNextNodeId(iterator201);

            //check the SPO does not contain the given index and that we haven't reached the end of iterator
            if (nodeId != -1 && !longIndex012.contains(nodeId)) {
                final Node node = nodePool.getNodeById(nodeId);
                //check node is not a literal
                if (!(node instanceof Literal)) {
                    return resourceFactory.createResource(node);
                }
            }
        }
        return null;
    }


    /**
     * Get the next subject element in the SPO index.
     *
     * @return next element in the SPO index.
     */
    private Resource getNextSPOElement() {
        final Long index = getNextNodeId(iterator012);
        if (index != -1) {
            final Node node = nodePool.getNodeById(index);
            return resourceFactory.createResource(node);
        }
        return null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected Resource getNextResource() {
        //for the first time retrieve the current one as well as the next one
        final Resource resource;
        if (nextResource == null) {
            throw new NoSuchElementException();
        } else {
            resource = nextResource;
            nextResource = getNextNode();
        }
        return resource;
    }

    /**
     * Provides a customizable way in which to filter out resource nodes based on type.
     *
     * @param iterator iterators over the index.
     * @return the next node identifier.
     */
    protected abstract long getNextNodeId(ClosableIterator<Long[]> iterator);
}
