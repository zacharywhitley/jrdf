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

package org.jrdf.graph;

import java.net.URI;

/**
 * A Graph Element Factory is a class which creates the various components of a
 * graph including: resources, literals and blank nodes.  It is generally tied to a
 * specific graph (in the case of blank nodes it must be).
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public interface GraphElementFactory extends GraphValueFactory {

    /**
     * Create a resource, wrapping a blank node that is associated with a specific graph.
     *
     * @return the newly created resource wrapping a blank node value.
     * @throws GraphElementFactoryException if adding a blank node fails.
     */
    Resource createResource() throws GraphElementFactoryException;

    /**
     * Wrap a blank node in a resource.
     *
     * @param node the node to wrap.
     * @return a new resource.
     * @throws GraphElementFactoryException if the blank node does not exist.
     */
    Resource createResource(BlankNode node) throws GraphElementFactoryException;

    /**
     * Wrap a URIReference in a resource.
     *
     * @param node the node to wrap.
     * @return a new resource.
     * @throws GraphElementFactoryException if the URIReference does not exist.
     */
    Resource createResource(URIReference node) throws GraphElementFactoryException;

    /**
     * Create a blank node that is associated with a specific graph.
     *
     * @return the newly created blank node value.
     * @throws GraphElementFactoryException if adding a blank node fails.
     */
    BlankNode createBlankNode() throws GraphElementFactoryException;

    /**
     * Create a URI reference wrapped in a Resource.
     *
     * @param uri The URI of the resource.
     * @return the newly created resource wrapping a URI reference value.
     * @throws GraphElementFactoryException If the resource failed to be created.
     */
    Resource createResource(URI uri) throws GraphElementFactoryException;

    /**
     * Create a URI reference wrapped in a Resource without checking if the URI given is a valid RDF
     * URI, currently if the URI is absolute.
     *
     * @param uri The URI of the resource.
     * @param validate true if we disbale checking to see if the URI is valid.
     * @return The newly created URI reference value.
     * @throws GraphElementFactoryException If the resource failed to be created.
     */
    Resource createResource(URI uri, boolean validate) throws GraphElementFactoryException;

    Resource createResource(Node node) throws GraphElementFactoryException;
}
