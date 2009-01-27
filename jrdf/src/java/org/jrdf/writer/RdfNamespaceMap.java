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
import org.jrdf.graph.URIReference;

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
     * Returns a string representing the resource URI with its URI prefix
     * replaced by the mapped namespace.
     *
     * @param resource URIReference resource URI
     * @return String namespaced representation of resource URI
     * @throws org.jrdf.writer.NamespaceException If there is no mapping for the partial resource URI.
     */
    String replaceNamespace(URIReference resource) throws NamespaceException;

    /**
     * Returns the prefix that is mapped to the resource or null if the URI is not mapped.  Extracts the uri to the
     * last '#' or '/' character.
     *
     * @param resource prefix to look up.
     * @return full namespace.
     */
    String getPrefix(URIReference resource);

    /**
     * Returns the URI that is mapped to the prefix or null if the prefix is not mapped.
     *
     * @param partial prefix to lookup.
     * @return full namespace.
     */
    String getFullUri(String partial);

    /**
     * Returns the Names mapping entry set.
     *
     * @return name map entries.
     */
    Set<Map.Entry<String, String>> getNameEntries();

    /**
     * Reset the name and uri mappings.
     */
    void reset();
}
