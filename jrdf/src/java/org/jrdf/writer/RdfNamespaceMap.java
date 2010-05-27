/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.writer;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Set;

/**
 * Contains mappings between namespaces and partial URIs.
 *
 * @author TurnerRX
 * @version $Id$
 */
public interface RdfNamespaceMap {
    /**
     * Loads namespaces from the graph.
     *
     * @param graph Graph containing URIs to load from.
     * @throws org.jrdf.graph.GraphException If the graph cannot be read.
     */
    void load(Graph graph) throws GraphException;

    /**
     * Adds a new namespace mapping, if no mapping already exists.
     *
     * @param namespace the shortened name to add, e.g. rdf.
     * @param partialUri the URI to map, e.g. http://www.w3.org/1999/02/22-rdf-syntax-ns#.
     */
    void addNamespace(String namespace, String partialUri) throws NamespaceException;

    /**
     * Returns the Names mapping entry set.
     *
     * @return name map entries.
     */
    Set<Map.Entry<String, String>> getNameEntries();

    /**
     * Parses a string and finds the last # or / and treats the characters before that as the namespace URI and the
     * characters after it as the local part.
     *
     * @param uri the uri to parse
     * @return either a QName with a namespace URI and local part if a # or / is found or a QName with the given
     *      string as the namespace uri and an empty string as the local part.
     */
    QName getQName(String uri);

    /**
     * Reset the name and uri mappings.
     */
    void reset();
}
