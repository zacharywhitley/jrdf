/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.index.graphhandler.GraphHandler;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.mem.ResourceFactory;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class AnyResourceIterator implements ClosableIterator<Resource> {
    private final LongIndex longIndex012;
    private final GraphHandler graphHandler012;
    private final ResourceFactory resourceFactory;
    private final GraphHandler graphHandler201;
    private Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator012;    //spo iterator
    private Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator201;    //osp iterator

    public AnyResourceIterator(final LongIndex newLongIndex012, final LongIndex newLongIndex201,
        final GraphHandler newGraphHandler012, final GraphHandler newGraphHandler201,
        final ResourceFactory newResourceFactory) {
        checkNotNull(newLongIndex012, newLongIndex201, newGraphHandler012, newGraphHandler201, newResourceFactory);
        this.resourceFactory = newResourceFactory;
        this.longIndex012 = newLongIndex012;
        this.graphHandler012 = newGraphHandler012;
        this.iterator012 = newLongIndex012.iterator();
        this.graphHandler201 = newGraphHandler201;
        this.iterator201 = newLongIndex201.iterator();
    }

    public boolean close() {
        return true;
    }

    public boolean hasNext() {
        return iterator012.hasNext();
    }

    public Resource next() {
        Resource resource = null;

        try {
            if (iterator201.hasNext()) {
                resource = getNextOSPElement();
            }
            if (iterator012.hasNext() || resource == null) {
                resource = getNextSPOElement();
            } else {
                throw new NoSuchElementException();
            }
        } catch (GraphElementFactoryException e) {
            throw new NoSuchElementException();
        }
        return resource;
    }


    /**
     * Get the next subject elemtn in the SPO index.
     *
     * @return next element in the SPO index.
     */
    private Resource getNextSPOElement() throws GraphElementFactoryException {
        Resource resource = null;
        Node node = graphHandler012.createNode(iterator012.next().getKey());
        resource = toResource(node);

        return resource;
    }

    /**
     * Get the next object element from the OSP index.
     *
     * @return next object in the osp index.
     */
    private Resource getNextOSPElement() throws GraphElementFactoryException {
        Resource resource = null;
        //move through the
        while (iterator201.hasNext()) {
            Long index = iterator201.next().getKey();

            //check the SPO does not contain the given index
            if (!longIndex012.contains(index)) {
                Node node = graphHandler201.createNode(index);
                //check node is not a literal
                if (!(node instanceof Literal)) {
                    resource = toResource(node);
                    break;
                }
            }
        }
        return resource;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts the given node into a Resource.
     *
     * @param node to be converted.
     * @return passed node as a resource.
     */
    private Resource toResource(Node node) throws GraphElementFactoryException {
        Resource resource = null;
        if (node instanceof BlankNode) {
            return resourceFactory.createResource((BlankNode) node);
        } else if (node instanceof URIReference) {
            return resourceFactory.createResource((URIReference) node);
        } else if (node instanceof Literal) {
            throw new UnsupportedOperationException("Cannot convert Literals to Resources");
        }
        return resource;
    }
}
