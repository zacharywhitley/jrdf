package org.jrdf.graph.global.molecule;

import org.jrdf.graph.AbstractBlankNode;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.molecule.mem.NewMolecule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: 6/05/2008
 * Time: 09:22:09
 * To change this template use File | Settings | File Templates.
 */
public class NewBlankNodeMapperImpl implements BlankNodeMapper {
    private TripleComparator tripleComparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private Map<BlankNode, BlankNode> map;

    public Map<BlankNode, BlankNode> getMap() {
        return map;
    }

    /**
     * Given molecules m1 and m2, try to find a blank node map for m2 inside m1, so this method isn't symmetrical.
     * If m1's root triple size is smaller than m2's root triple size (m1 doesn't subsumes m2),
     * an empty map is returned.
     * <p/>
     * Potential optimizations:
     * 1. By recording no. of unique blank nodes on each level, the non-subsumption may be detected faster.
     * 2. By recording no. of levels of submolecules, the non-subsumption may be detected faster.
     *
     * @param m1
     * @param m2
     * @return
     */
    public Map<BlankNode, BlankNode> createMap(NewMolecule m1, NewMolecule m2) {
        map = new HashMap<BlankNode, BlankNode>();
        map = populateMap(m1, m2, map);
        return map;
    }

    private Map<BlankNode, BlankNode> populateMap(NewMolecule m1, NewMolecule m2, Map<BlankNode, BlankNode> map) {
        Set<Triple> m1RootTriples = m1.getRootTriplesAsSet();
        Set<Triple> m2RootTriples = m2.getRootTriplesAsSet();
        if (m1RootTriples.size() < m2RootTriples.size()) {
            return new HashMap<BlankNode, BlankNode>();
        }
        Iterator<Triple> m1Roots = m1RootTriples.iterator();
        Iterator<Triple> m2Roots = m2RootTriples.iterator();
        while (m2Roots.hasNext()) {
            Triple m2RootTriple = m2Roots.next();
            if (!m1.contains(m2RootTriple)) {
                return new HashMap<BlankNode, BlankNode>();
            }
            findCorrespondingTriple(m1Roots, m2RootTriple, map);
            Set<NewMolecule> sm1s = m1.getSubMolecules(m2RootTriple);
            Set<NewMolecule> sm2s = m2.getSubMolecules(m2RootTriple);
            if (!sm1s.isEmpty() && !sm2s.isEmpty()) {
                if (sm1s.size() == 1 && sm2s.size() == 1) {
                    NewMolecule sm1 = sm1s.iterator().next();
                    NewMolecule sm2 = sm2s.iterator().next();
                    Map<BlankNode, BlankNode> curMap = populateMap(sm1, sm2, new HashMap<BlankNode, BlankNode>());
                    if (curMap.size() == 0) {
                        return curMap;
                    } else {
                        for (Map.Entry<BlankNode, BlankNode> entry : curMap.entrySet()) {
                            map.put(entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    throw new UnsupportedOperationException("Cannot handle more than one level of submolecules at " +
                        "this time");
                }
            } else if (sm1s.size() < sm2s.size()) {
                return new HashMap<BlankNode, BlankNode>();
            }
        }
        return map;
    }

    /**
     * In the calling method, it has already guaranteed that m2RootTriple is contained in m1,
     * hence no checking is necessary here.
     * @param m1Roots
     * @param m2RootTriple
     * @param map
     */
    private void findCorrespondingTriple(Iterator<Triple> m1Roots, Triple m2RootTriple, Map<BlankNode, BlankNode> map) {
        while (m1Roots.hasNext()) {
            Triple t1 = m1Roots.next();
            if (tripleComparator.compare(t1, m2RootTriple) == 0) {
                addBlankNodesToMapForTriples(m2RootTriple, t1, map);
                break;
            }
        }
    }

    private void addBlankNodesToMapForTriples(Triple m2RootTriple, Triple t1, Map<BlankNode, BlankNode> map) {
        if (AbstractBlankNode.isBlankNode(t1.getSubject())) {
            map.put((BlankNode) m2RootTriple.getSubject(), (BlankNode) t1.getSubject());
        }
        if (AbstractBlankNode.isBlankNode(t1.getObject())) {
            map.put((BlankNode) m2RootTriple.getObject(), (BlankNode) t1.getObject());
        }
    }
}
