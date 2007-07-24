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

package org.jrdf.graph.index.nodepool.jcs;

import org.apache.jcs.engine.control.CompositeCacheManager;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.Node;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.mem.LocalizedNode;

import java.util.Properties;
import java.util.Collection;
import java.io.File;

public class NodePoolImpl implements NodePool {
    private static final File SYSTEM_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String REGION = "nodepool";
    private final CompositeCacheManager manager;
    private JCS cache;

    public NodePoolImpl(CompositeCacheManager manager) {
        this.manager = manager;
        configure(manager);
        try {
            cache = JCS.getInstance(REGION);
        } catch (CacheException e) {
            throw new RuntimeException("Cannot get cache", e);
        }
    }

    private void configure(CompositeCacheManager manager) {
        Properties props = new Properties();
        props.put("jcs.default", REGION);
        props.put("jcs.default.cacheattributes", "org.apache.jcs.engine.CompositeCacheAttributes");
        props.put("jcs.default.cacheattributes.MaxObjects", "1000");
        props.put("jcs.default.cacheattributes.MemoryCacheName", "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
        props.put("jcs.auxiliary." + REGION, "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
        props.put("jcs.auxiliary." + REGION + ".attributes",
            "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
        File dir = new File(SYSTEM_TEMP_DIR, "jrdf_" + System.getProperty("user.name"));
        dir.mkdirs();
        props.put("jcs.auxiliary." + REGION + ".attributes.DiskPath", dir.getAbsolutePath());
        props.put("jcs.auxiliary."  + REGION + ".attributes.maxKeySize", "0");
        manager.configure(props);
    }

    public Node getNodeById(Long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Long getNodeIdByString(String str) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Long[] localize(Node first, Node second, Node third) throws GraphException {
        return new Long[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<Node> getNodePoolValues() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void registerNode(LocalizedNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Long getNextNodeId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
