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

package org.jrdf.graph.local.index.nodepool.sesame;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import static org.jrdf.graph.local.index.longindex.sesame.ByteArrayUtil.*;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.mem.LocalizedNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class SesameNodePool implements NodePool {
    private static final byte BNODE_PREFIX = 1;
    private static final byte URI_PREFIX = 2;
    private static final byte LITERAL_PREFIX = 3;
    private IterativeDataStore nodePool;
    private StringNodeMapper mapper;
    private CRC32 crc32 = new CRC32();

    public SesameNodePool(IterativeDataStore newNodePool, StringNodeMapper newMapper) {
        this.nodePool = newNodePool;
        this.mapper = newMapper;
    }

    public Node getNodeById(Long id) {
        try {
            Node node = null;
            int nodeId = nodePool.hashIterator(id.intValue()).next();
            if (nodeId != -1) {
                node = getNodeFromId(nodeId);
            }
            return node;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getNodeIdByString(String str) {
        Long crc = getNodeId(str);
        if (nodeIdMissing(crc)) {
            return null;
        } else {
            return crc;
        }
    }

    public Long getNodeId(String value) {
        synchronized (crc32) {
            crc32.update(value.getBytes());
            int crc = (int) crc32.getValue();
            crc32.reset();
            return (long) crc;
        }
    }

    public void registerNode(LocalizedNode node) {
        try {
            String value = mapper.convertToString(node);
            final byte[] bytes = nodeToBytes(node, value);
            nodePool.storeData(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try {
            nodePool.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerNodePoolValues(List<Map<Long, String>> values) {
        throw new UnsupportedOperationException();
    }

    public List<Map<Long, String>> getNodePoolValues() {
        throw new UnsupportedOperationException();
    }

    private boolean nodeIdMissing(Long id) {
        try {
            return nodePool.hashIterator(id.intValue()).next() == -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Node getNodeFromId(int nodeId) throws IOException {
        final byte[] bytes = nodePool.getData(nodeId);
        final byte type = bytes[0];
        final String value = new String(bytes, 1, bytes.length - 1);
        Node node = null;
        if (type == BNODE_PREFIX) {
            node = mapper.convertToBlankNode(value);
        } else if (type == URI_PREFIX) {
            node = mapper.convertToURIReference(value, Long.valueOf(nodeId));
        } else if (type == LITERAL_PREFIX) {
            node = mapper.convertToLiteral(value, Long.valueOf(nodeId));
        }
        return node;
    }

    private byte[] nodeToBytes(Node node, String value) {
        final byte[] bytes = new byte[value.length() + 1];
        put(value.getBytes(), bytes, 1);
        if (BlankNode.class.isAssignableFrom(node.getClass())) {
            bytes[0] = BNODE_PREFIX;
        } else if (URIReference.class.isAssignableFrom(node.getClass())) {
            bytes[0] = URI_PREFIX;
        } else if (Literal.class.isAssignableFrom(node.getClass())) {
            bytes[0] = LITERAL_PREFIX;
        } else {
            throw new IllegalArgumentException("Failed to add node: " + node);
        }
        return bytes;
    }
}