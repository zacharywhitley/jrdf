package org.jrdf.graph.local.index.nodepool.sesame;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.Resource;
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
    private static final int TRIPLE = 3;
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
                final byte[] bytes = nodePool.getData(nodeId);
                final byte type = bytes[0];
                final String value = new String(bytes, 1, bytes.length - 1);
                if (type == BNODE_PREFIX) {
                    node = mapper.convertToBlankNode(value);
                } else if (type == URI_PREFIX) {
                    node = mapper.convertToURIReference(value, new Long(nodeId));
                } else if (type == LITERAL_PREFIX) {
                    node = mapper.convertToLiteral(value, new Long(nodeId));
                }
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
            nodePool.storeData(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Long[] localize(Node first, Node second, Node third) throws GraphException {
        Long[] localValues = new Long[TRIPLE];

        // convert the nodes to local memory nodes for convenience
        localValues[0] = convertSubject(first);
        localValues[1] = convertPredicate(second);
        localValues[2] = convertObject(third);
        return localValues;
    }

    public Long localize(Node node) throws GraphException {
        return convertObject(node);
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

    private Long convertSubject(Node first) throws GraphException {
        Long subjectValue = null;
        if (ANY_SUBJECT_NODE != first) {
            if (LocalizedNode.class.isAssignableFrom(first.getClass())) {
                if (BlankNode.class.isAssignableFrom(first.getClass())) {
                    subjectValue = getBlankNode(first);
                } else if (URIReference.class.isAssignableFrom(first.getClass())) {
                    final String value = ((URIReference) first).getURI().toString();
                    final byte[] bytes = new byte[value.length() + 1];
                    put(value.getBytes(), bytes, 1);
                    bytes[0] = URI_PREFIX;
                    subjectValue = getNodeIdByString(new String(bytes));
                }
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
            final String value = ((URIReference) second).getURI().toString();
            final byte[] bytes = new byte[value.length() + 1];
            put(value.getBytes(), bytes, 1);
            bytes[0] = URI_PREFIX;
            predicateValue = getNodeIdByString(new String(bytes));
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
            if (LocalizedNode.class.isAssignableFrom(third.getClass())) {
                if (Resource.class.isAssignableFrom(third.getClass())) {
                    objectValue = getResource(third);
                } else if (BlankNode.class.isAssignableFrom(third.getClass())) {
                    objectValue = getBlankNode(third);
                } else if (Literal.class.isAssignableFrom(third.getClass())) {
                    final String value = ((Literal) third).getEscapedForm();
                    final byte[] bytes = new byte[value.length() + 1];
                    put(value.getBytes(), bytes, 1);
                    bytes[0] = LITERAL_PREFIX;
                    objectValue = getNodeIdByString(new String(bytes));
                } else if (URIReference.class.isAssignableFrom(third.getClass())) {
                    final String value = ((URIReference) third).getURI().toString();
                    final byte[] bytes = new byte[value.length() + 1];
                    put(value.getBytes(), bytes, 1);
                    bytes[0] = URI_PREFIX;
                    objectValue = getNodeIdByString(new String(bytes));
                }
            }
            if (null == objectValue) {
                throw new GraphException("Object does not exist in graph: " + third);
            }
        }
        return objectValue;
    }

    private Long getResource(Node third) throws GraphException {
        Long objectValue;
        if (((Resource) third).isURIReference()) {
            final String value = ((URIReference) third).getURI().toString();
            final byte[] bytes = new byte[value.length() + 1];
            put(value.getBytes(), bytes, 1);
            bytes[0] = URI_PREFIX;
            objectValue = getNodeIdByString(new String(bytes));
        } else {
            objectValue = getBlankNode(third);
        }
        return objectValue;
    }

    private Long getBlankNode(Node blankNode) throws GraphException {
        final String value = mapper.convertToString(blankNode);
        final byte[] bytes = new byte[value.length() + 1];
        put(value.getBytes(), bytes, 1);
        bytes[0] = BNODE_PREFIX;
        Long nodeId = getNodeId(new String(bytes));
        if (nodeIdMissing(nodeId)) {
            throw new GraphException("The node id was not found in the graph: " + nodeId);
        }
        return nodeId;
    }

    private boolean nodeIdMissing(Long id) {
        try {
            return nodePool.hashIterator(id.intValue()).next() == -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}