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

package org.jrdf.graph.local.index.nodepool.mem;

import org.jrdf.graph.Node;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.NodePoolImpl;
import org.jrdf.graph.local.index.nodepool.NodeTypePoolImpl;
import org.jrdf.graph.local.index.nodepool.NodeTypePool;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperImpl;
import org.jrdf.map.MapFactory;
import org.jrdf.map.MemMapFactory;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;
import org.jrdf.parser.ntriples.parser.LiteralMatcher;
import org.jrdf.parser.ntriples.parser.RegexLiteralMatcher;
import org.jrdf.parser.ntriples.parser.NTripleUtilImpl;

import java.util.Map;

public class MemNodePoolFactory implements NodePoolFactory {
    public NodePool createNodePool() {
        MapFactory factory = new MemMapFactory();
        Map<String, Long> stringPool = factory.createMap(String.class, Long.class);
        Map<Long, Node> nodePool = factory.createMap(Long.class, Node.class);
        Map<Long, String> bnodePool = factory.createMap(Long.class, String.class);
        Map<Long, String> uriPool = factory.createMap(Long.class, String.class);
        Map<Long, String> literalPool = factory.createMap(Long.class, String.class);
        RegexMatcherFactory regexFactory = new RegexMatcherFactoryImpl();
        LiteralMatcher matcher = new RegexLiteralMatcher(regexFactory, new NTripleUtilImpl(regexFactory));
        StringNodeMapper mapper = new StringNodeMapperImpl(matcher);
        final NodeTypePool nodeTypePool = new NodeTypePoolImpl(bnodePool, uriPool, literalPool, mapper);
//        NodeTypePoolImpl nodeTypePool = new NodeTypePoolImpl(nodePool);
        return new NodePoolImpl(nodeTypePool, stringPool);
    }

    public void close() {
        // Do nothing - in memory.
    }
}
