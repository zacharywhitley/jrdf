package org.jrdf.graph.global.molecule;

import org.jrdf.graph.BlankNode;

import java.util.Map;

public interface BlankNodeMapper {
    public Map<BlankNode, BlankNode> createMap(NewMolecule m1, NewMolecule m2);
}
