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

package org.jrdf.graph.local.index.nodepool.bdb;

import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.NodePoolImpl;
import org.jrdf.graph.local.index.nodepool.NodeTypePool;
import org.jrdf.graph.local.index.nodepool.NodeTypePoolImpl;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.collection.BdbMapFactory;
import org.jrdf.collection.MapFactory;
import org.jrdf.util.bdb.BdbEnvironmentHandler;

import java.util.Map;

public class BdbNodePoolFactory implements NodePoolFactory {
    private static final String DB_NAME_NODEPOOL = "nodePool";
    private static final String DB_NAME_STRINGPOOL = "stringPool";
    private final BdbEnvironmentHandler handler;
    private final long graphNumber;
    private MapFactory nodePoolMapFactory;
    private MapFactory stringPoolMapFactory;

    public BdbNodePoolFactory(final BdbEnvironmentHandler newHandler, final long newGraphNumber) {
        this.handler = newHandler;
        this.graphNumber = newGraphNumber;
    }

    @SuppressWarnings({ "unchecked" })
    public NodePool createNewNodePool() {
        nodePoolMapFactory = new BdbMapFactory(handler, DB_NAME_NODEPOOL + graphNumber);
        stringPoolMapFactory = new BdbMapFactory(handler, DB_NAME_STRINGPOOL + graphNumber);
        StringNodeMapper mapper = new StringNodeMapperFactoryImpl().createMapper();
        final Map<Long, String> blankNodePool = nodePoolMapFactory.createMap(Long.class, String.class, "bnp");
        final Map<Long, String> uriNodePool = nodePoolMapFactory.createMap(Long.class, String.class, "npm");
        final Map<Long, String> literalNodePool = nodePoolMapFactory.createMap(Long.class, String.class, "lnp");
        final Map<String, Long> stringPool = stringPoolMapFactory.createMap(String.class, Long.class, "sp");
        final NodeTypePool nodeTypePool = new NodeTypePoolImpl(mapper, blankNodePool, uriNodePool, literalNodePool);
        return new NodePoolImpl(nodeTypePool, stringPool);
    }

    @SuppressWarnings({ "unchecked" })
    public NodePool openExistingNodePool() {
        nodePoolMapFactory = new BdbMapFactory(handler, DB_NAME_NODEPOOL + graphNumber);
        stringPoolMapFactory = new BdbMapFactory(handler, DB_NAME_STRINGPOOL + graphNumber);
        StringNodeMapper mapper = new StringNodeMapperFactoryImpl().createMapper();
        final Map<Long, String> blankNodePool = nodePoolMapFactory.openExistingMap(Long.class, String.class, "bnp");
        final Map<Long, String> uriNodePool = nodePoolMapFactory.openExistingMap(Long.class, String.class, "npm");
        final Map<Long, String> literalNodePool = nodePoolMapFactory.openExistingMap(Long.class, String.class, "lnp");
        final Map<String, Long> stringPool = stringPoolMapFactory.openExistingMap(String.class, Long.class, "sp");
        final NodeTypePool nodeTypePool = new NodeTypePoolImpl(mapper, blankNodePool, uriNodePool, literalNodePool);
        return new NodePoolImpl(nodeTypePool, stringPool);
    }

    public void close() {
        try {
            stringPoolMapFactory.close();
        } finally {
            nodePoolMapFactory.close();
        }
    }
}