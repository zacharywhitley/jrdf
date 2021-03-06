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

package org.jrdf.sparql.builder;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.sparql.parser.node.AIriRefIriRefOrPrefixedName;
import org.jrdf.sparql.parser.node.APrefixedNameIriRefOrPrefixedName;
import org.jrdf.sparql.parser.node.AQnameQnameElement;
import org.jrdf.sparql.parser.node.Token;
import org.jrdf.sparql.parser.parser.ParserException;

import java.net.URI;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */

public class URIReferenceBuilderImpl implements URIReferenceBuilder {
    private final GraphElementFactory factory;
    private final Map<String, String> prefixMap;
    private ParserException exception;
    private URIReference uriRef;

    public URIReferenceBuilderImpl(GraphElementFactory newFactory, Map<String, String> newPrefixMap) {
        this.factory = newFactory;
        this.prefixMap = newPrefixMap;
    }

    public Node createURIReference(AIriRefIriRefOrPrefixedName node) throws ParserException {
        final URI uri = TokenHelper.getResource(node.getResource());
        createFromURIReference(node.getResource(), uri);
        return getResult(node);
    }

    public Node createURIReference(APrefixedNameIriRefOrPrefixedName node) throws ParserException {
        createFromPrefixedName(node);
        return getResult(node);
    }

    private void createFromPrefixedName(APrefixedNameIriRefOrPrefixedName node) {
        final AQnameQnameElement element = (AQnameQnameElement) node.getQnameElement();
        final String prefix = element.getNcnamePrefix().getText();
        final String namespace = prefixMap.get(prefix);
        final String localName = element.getNcName().getText();
        createFromURIReference(element.getNcName(), URI.create(namespace + localName));
    }

    private Node getResult(org.jrdf.sparql.parser.node.Node element) throws ParserException {
        if (exception == null) {
            if (uriRef != null) {
                return uriRef;
            } else {
                throw new IllegalStateException("Unable to parse element: " + element);
            }
        } else {
            throw exception;
        }
    }

    private void createFromURIReference(Token node, URI uri)  {
        try {
            uriRef = factory.createURIReference(uri);
        } catch (GraphElementFactoryException e) {
            exception = new ParserException(node,
                "Cannot create URI reference for URI: " + uri.toString());
        }
    }
}
