package org.jrdf.graph.global.molecule;

import org.jrdf.graph.BlankNode;

import java.util.HashMap;
import java.util.Map;

public class BlankNodeMapperImpl implements BlankNodeMapper {
    public Map<BlankNode, BlankNode> createMap(NewMolecule m1, NewMolecule m2) {
        Map<BlankNode, BlankNode> map = new HashMap<BlankNode, BlankNode>();
//for each root triple in m1, t1, find it in m2, t2
// if submolecule of t1, sm1, has the same or fewer submolecules of t2, sm2
//   nm = blank nodes(sm1, sm2)
//   if nm is empty
//     return empty map
//   else
//     add nm to bm
// else if t1 has no submolecule and t2 has no submolecules
//   add map of blank nodes in m1 with blank nodes in t2
// else
//   return empty map (indicating m1 cannot be merged with m2)
//end loop
        return map;
    }
}
