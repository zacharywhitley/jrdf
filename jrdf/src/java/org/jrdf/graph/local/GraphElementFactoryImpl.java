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

package org.jrdf.graph.local;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.Node;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.net.URI;

/**
 * A GraphElementFactory is a class which create the various components of a graph. It is tied to a specific instance
 * of GraphImpl.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public final class GraphElementFactoryImpl implements GraphElementFactory {
    private final NodePool nodePool;
    private final ResourceFactory resourceFactory;
    private final Localizer localizer;

    /**
     * Package scope constructor.
     */
    GraphElementFactoryImpl(NodePool newNodePool, ReadWriteGraph newGraph) {
        checkNotNull(newNodePool, newGraph);
        this.nodePool = newNodePool;
        this.resourceFactory = new ResourceFactoryImpl(newGraph, this);
        this.localizer = new LocalizerImpl(nodePool, new StringNodeMapperFactoryImpl().createMapper());
    }

    public Resource createResource() throws GraphElementFactoryException {
        return createResource(createBlankNode());
    }

    public Resource createResource(BlankNode node) throws GraphElementFactoryException {
        try {
            return resourceFactory.createResource(node);
        } catch (IllegalArgumentException e) {
            throw new GraphElementFactoryException(e);
        }
    }

    public Resource createResource(URIReference node) throws GraphElementFactoryException {
        try {
            return resourceFactory.createResource(node);
        } catch (IllegalArgumentException e) {
            throw new GraphElementFactoryException(e);
        }
    }

    public Resource createResource(URI uri) throws GraphElementFactoryException {
        return createResource(createURIReference(uri));
    }

    public Resource createResource(URI uri, boolean validate) throws GraphElementFactoryException {
        return createResource(createURIReference(uri));
    }

    public BlankNode createBlankNode() throws GraphElementFactoryException {
        try {
            return localizer.createLocalBlankNode();
        } catch (GraphException e) {
            throw new GraphElementFactoryException(e);
        }
    }

    public URIReference createURIReference(URI uri) throws GraphElementFactoryException {
        return createURIReference(uri, true);
    }

    public URIReference createURIReference(URI uri, boolean validate) throws GraphElementFactoryException {
        if (null == uri) {
            throw new GraphElementFactoryException("URI may not be null for a URIReference");
        }
        return getLocalURIReference(uri, validate);
    }

    public Literal createLiteral(Object object) {
        final LiteralImpl literal = new LiteralImpl(object);
        return getLocalLiteral(literal.getEscapedForm());
    }

    public Literal createLiteral(String lexicalValue) {
        final LiteralImpl literal = new LiteralImpl(lexicalValue);
        return getLocalLiteral(literal.getEscapedForm());
    }

    public Literal createLiteral(String lexicalValue, String languageType) {
        final LiteralImpl literal = new LiteralImpl(lexicalValue, languageType);
        return getLocalLiteral(literal.getEscapedForm());
    }

    public Literal createLiteral(String lexicalValue, URI datatypeURI) {
        final LiteralImpl literal = new LiteralImpl(lexicalValue, datatypeURI);
        return getLocalLiteral(literal.getEscapedForm());
    }

    private URIReference getLocalURIReference(URI uri, boolean validate) {
        Long nodeId = nodePool.getNodeIdByString(uri.toString());
        URIReference newURIReference;
        if (null != nodeId) {
            newURIReference = (URIReference) nodePool.getNodeById(nodeId);
        } else {
            newURIReference = localizer.createLocalURIReference(uri, validate);
        }
        return newURIReference;
    }

    private Literal getLocalLiteral(String escapedForm) {
        Long nodeId = nodePool.getNodeIdByString(escapedForm);
        Literal newLiteral;
        if (null != nodeId) {
            newLiteral = (Literal) nodePool.getNodeById(nodeId);
        } else {
            newLiteral = localizer.createLocalLiteral(escapedForm);
        }
        return newLiteral;
    }

    public Resource createResource(Node node) throws GraphElementFactoryException {
        if (BlankNode.class.isAssignableFrom(node.getClass())) {
            return resourceFactory.createResource((BlankNode) node);
        } else if (URIReference.class.isAssignableFrom(node.getClass())) {
            return resourceFactory.createResource((URIReference) node);
        } else {
            throw new GraphElementFactoryException("Resource cannot be created from: " + node);
        }
    }
}
