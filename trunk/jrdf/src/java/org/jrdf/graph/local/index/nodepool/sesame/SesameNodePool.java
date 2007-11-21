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
            node = mapper.convertToURIReference(value, new Long(nodeId));
        } else if (type == LITERAL_PREFIX) {
            node = mapper.convertToLiteral(value, new Long(nodeId));
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