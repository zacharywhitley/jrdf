package org.jrdf.graph.global.molecule;

import org.jrdf.graph.BlankNode;

import java.util.Map;

/**
 * Class description goes here.
 */
public interface LocalMergeSubmolecules {
    NewMolecule merge(NewMolecule molecule1, NewMolecule molecule2, Map<BlankNode, BlankNode> map);
}
