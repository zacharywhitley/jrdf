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

import org.apache.jcs.access.GroupCacheAccess;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.mem.LocalizedNode;
import org.jrdf.graph.mem.BlankNodeImpl;
import org.jrdf.graph.mem.LiteralMutableId;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

public class NodePoolImpl implements NodePool {
    private static final int TRIPLE = 3;
    private static final File SYSTEM_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final String NODEPOOL_REGION = "nodePool";
    private static final String STRINGPOOL_REGION = "stringPool";
    private final CompositeCacheManager manager;
    private GroupCacheAccess nodePool;
    private GroupCacheAccess stringPool;
    private long nextNode = 1L;


    public NodePoolImpl(CompositeCacheManager newManager) {
        this.manager = newManager;
        configure(newManager);
        nodePool = new GroupCacheAccess(manager.getCache(NODEPOOL_REGION));
        stringPool = new GroupCacheAccess(manager.getCache(STRINGPOOL_REGION));
    }

    private void configure(CompositeCacheManager manager) {
        Properties props = new Properties();
        props.put("jcs.default", "");
        props.put("jcs.default.cacheattributes", "org.apache.jcs.engine.CompositeCacheAttributes");
        props.put("jcs.default.cacheattributes.MaxObjects", "-1");
        props.put("jcs.default.cacheattributes.MemoryCacheName", "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
        props.put("jcs.default.elementattributes", "org.apache.jcs.engine.ElementAttributes");
        props.put("jcs.default.elementattributes.IsEternal", "true");
        props.put("jcs.default.elementattributes.IsSpool", "true");
        configAuxiliary(props, NODEPOOL_REGION);
        configAuxiliary(props, STRINGPOOL_REGION);
        manager.configure(props);
    }

    private void configAuxiliary(Properties props, String region) {
        String upperCaseRegion = region.toUpperCase();
        props.put("jcs.region." + region, upperCaseRegion);
        props.put("jcs.auxiliary." + upperCaseRegion, "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
        props.put("jcs.auxiliary." + upperCaseRegion + ".attributes",
            "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
        File dir = new File(SYSTEM_TEMP_DIR, "jrdf_" + System.getProperty("user.name") + "_region");
        dir.mkdirs();
        props.put("jcs.auxiliary." + upperCaseRegion + ".attributes.DiskPath", dir.getAbsolutePath());
        props.put("jcs.auxiliary."  + upperCaseRegion + ".attributes.maxKeySize", "-1");
    }

    public Node getNodeById(Long id) {
        return (Node) nodePool.get(id);
    }

    public Long getNodeIdByString(String str) {
        return (Long) stringPool.get(str);
    }

    public void registerNode(LocalizedNode node) {
        // get the id for this node
        Long id = node.getId();

        // look the node up to see if it already exists in the graph
        LocalizedNode existingNode = (LocalizedNode) nodePool.get(id);
        if (null != existingNode) {
            // check that the node is equal to the one that is already in the graph
            if (existingNode.equals(node)) {
                return;
            }
            // node does not match
            throw new IllegalArgumentException("Node conflicts with one already in the graph");
        }
        // add the node
        tryAdd(id, node);
    }

    private void tryAdd(Long id, LocalizedNode node) {
        try {
            nodePool.put(id, node);
            // check if the node has a string representation
            if (!(node instanceof BlankNode)) {
                if (node instanceof Literal) {
                    stringPool.put(((Literal) node).getEscapedForm(), node.getId());
                } else {
                    stringPool.put(node.toString(), node.getId());
                }
            }
            // update the nextNode counter to a unique number
            if (!(id < nextNode)) {
                nextNode = id + 1L;
            }
        } catch (CacheException e) {
            throw new RuntimeException("Failed to add: " + id);
        }
    }

    public Collection<Node> getNodePoolValues() {
        throw new UnsupportedOperationException();
    }

    public Long getNextNodeId() {
        return nextNode++;
    }

    public Long[] localize(Node first, Node second, Node third) throws GraphException {
        Long[] localValues = new Long[TRIPLE];

        // convert the nodes to local memory nodes for convenience
        localValues[0] = convertSubject(first);
        localValues[1] = convertPredicate(second);
        localValues[2] = convertObject(third);
        return localValues;
    }

    public void clear() {
        nextNode = 1L;
        try {
            nodePool.clear();
        } catch (CacheException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stringPool.clear();
            } catch (CacheException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Long convertSubject(Node first) throws GraphException {
        Long subjectValue = null;
        if (ANY_SUBJECT_NODE != first) {
            if (first instanceof BlankNodeImpl) {
                subjectValue = getBlankNode(first);
            } else {
                subjectValue = getNodeIdByString(String.valueOf(first));
            }

            if (null == subjectValue) {
                throw new GraphException("Subject does not exist in graph: " + first);
            }
        }
        return subjectValue;
    }

    private Long convertPredicate(Node second) throws GraphException {
        Long predicateValue = null;
        if (ANY_PREDICATE_NODE != second) {
            predicateValue = getNodeIdByString(String.valueOf(second));

            if (null == predicateValue) {
                throw new GraphException("Predicate does not exist in graph: " + second);
            }
        }

        return predicateValue;
    }

    // TODO (AN) String of instanceof should be changed to calls by type.
    private Long convertObject(Node third) throws GraphException {
        Long objectValue = null;
        if (ANY_OBJECT_NODE != third) {
            if (third instanceof BlankNodeImpl) {
                objectValue = getBlankNode(third);
            } else if (third instanceof LiteralMutableId) {
                objectValue = getNodeIdByString(((Literal) third).getEscapedForm());
            } else {
                objectValue = getNodeIdByString(String.valueOf(third));
            }

            if (null == objectValue) {
                throw new GraphException("Object does not exist in graph: " + third);
            }
        }

        return objectValue;
    }

    private Long getBlankNode(Node blankNode) throws GraphException {
        Long nodeId = ((LocalizedNode) blankNode).getId();
        Node node = (Node) nodePool.get(nodeId);
        if (node == null) {
            throw new GraphException("The node id was not found in the graph: " + nodeId);
        }
        if (!blankNode.equals(node)) {
            throw new GraphException("The node returned by the nodeId (" + nodeId + ") was not the same blank " +
                "node.  Got: " + node + ", expected: " + blankNode);
        }
        return nodeId;
    }
}
