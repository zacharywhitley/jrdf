package org.jrdf.graph.local.index.nodepool.sesame;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.mem.LocalizedNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class SesameNodePool implements NodePool {
    private IterativeDataStore blankNodePool;
    private IterativeDataStore uriNodePool;
    private IterativeDataStore literalNodePool;
    private StringNodeMapper mapper;
    private CRC32 crc32 = new CRC32();

    public SesameNodePool(File file, long graphNumber, StringNodeMapper newMapper) {
        this.mapper = newMapper;
        try {
            blankNodePool = new IterativeDataStore(file, "b" + graphNumber, false);
            uriNodePool = new IterativeDataStore(file, "u" + graphNumber, false);
            literalNodePool = new IterativeDataStore(file, "l" + graphNumber, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Node getNodeById(Long id) {
        try {
            Node node = null;
            int blankNodeId = blankNodePool.hashIterator(id.intValue()).next();
            int uriNodeId = uriNodePool.hashIterator(id.intValue()).next();
            int literalNodeId = literalNodePool.hashIterator(id.intValue()).next();
            if (blankNodeId != -1) {
                node = mapper.convertToBlankNode(new String(blankNodePool.getData(blankNodeId)));
            } else if (uriNodeId != -1) {
                node = mapper.convertToURIReference(new String(uriNodePool.getData(uriNodeId)), new Long(uriNodeId));
            } else if (literalNodeId != -1) {
                node = mapper.convertToLiteral(new String(literalNodePool.getData(literalNodeId)),
                    new Long(literalNodeId));
            }
            return node;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getNodeIdByString(String str) {
        synchronized (crc32) {
            crc32.update(str.getBytes());
            int crc = (int) crc32.getValue();
            crc32.reset();
            if (nodeIdMissing(crc)) {
                return null;
            } else {
                return (long) crc;
            }
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
            if (BlankNode.class.isAssignableFrom(node.getClass())) {
                blankNodePool.storeData(value.getBytes());
            } else if (URIReference.class.isAssignableFrom(node.getClass())) {
                uriNodePool.storeData(value.getBytes());
            } else if (Literal.class.isAssignableFrom(node.getClass())) {
                literalNodePool.storeData(value.getBytes());
            } else {
                throw new IllegalArgumentException("Failed to add node: " + node);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Long localize(Node node) throws GraphException {
        if (node == ANY_SUBJECT_NODE || node == ANY_PREDICATE_NODE || node == ANY_OBJECT_NODE) {
            return null;
        } else {
            Long id = getNodeId(mapper.convertToString(node));
            if (BlankNode.class.isAssignableFrom(node.getClass()) && isBlankNode(id.intValue())) {
                throw new GraphException("The node id was not found in the graph: " + id);
            }
            return id;
        }
    }

    public Long[] localize(Node first, Node second, Node third) throws GraphException {
        Long subject = localize(first);
        Long predicate = localize(second);
        Long object = localize(third);
        return new Long[]{subject, predicate, object};
    }

    public void registerNodePoolValues(List<Map<Long, String>> values) {
        addValues(values.get(0), blankNodePool);
        addValues(values.get(1), uriNodePool);
        addValues(values.get(2), literalNodePool);
    }

    public void clear() {
        try {
            blankNodePool.clear();
            uriNodePool.clear();
            literalNodePool.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<Long, String>> getNodePoolValues() {
        ArrayList<Map<Long, String>> list = new ArrayList<Map<Long, String>>();
        HashMap<Long, String> bnodes = new HashMap<Long, String>();
        addToMap(bnodes, blankNodePool);
        Map<Long, String> uris = new HashMap<Long, String>();
        addToMap(uris, uriNodePool);
        Map<Long, String> literals = new HashMap<Long, String>();
        addToMap(literals, literalNodePool);
        list.add(bnodes);
        list.add(uris);
        list.add(literals);
        return list;
    }

    private void addValues(Map<Long, String> values, IterativeDataStore pool) {
        try {
            for (String string : values.values()) {
                pool.storeData(string.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToMap(Map<Long, String> bNodes, IterativeDataStore pool) {
        DataFile.DataIterator iterator = pool.iterator();
        int counter = 0;
        try {
            while (iterator.hasNext()) {
                bNodes.put(new Long(counter++), new String(iterator.next()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean nodeIdMissing(int crc) {
        return isBlankNode(crc) && isLiteralNode(crc) && isURINode(crc);
    }

    private boolean isBlankNode(int id) {
        try {
            return blankNodePool.hashIterator(id).next() == -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isURINode(int id) {
        try {
            return uriNodePool.hashIterator(id).next() == -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isLiteralNode(int id) {
        try {
            return literalNodePool.hashIterator(id).next() == -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
