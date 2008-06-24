package org.jrdf.graph.global.index;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.global.MoleculeLocalizer;
import org.jrdf.graph.global.molecule.mem.MoleculeHandler;
import org.jrdf.graph.global.molecule.mem.NewMolecule;

import java.util.Set;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Apr 11, 2008
 * Time: 11:35:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemoveNewMoleculeFromIndex implements MoleculeHandler {
    private static final int QUIN_SIZE = 5;
    private static final int MOLECULE_ID_INDEX = 3;
    private static final int PARENT_ID_INDEX = 4;
    private final WritableIndex<Long> index;
    private final MoleculeLocalizer localizer;
    private Long parentId = 0L;
    private Long moleculeId = 0L;
    private Stack<Long> parentIds;

    public RemoveNewMoleculeFromIndex(WritableIndex<Long> newIndex, MoleculeLocalizer newLocalizer) {
        this.index = newIndex;
        this.localizer = newLocalizer;
        this.parentIds = new Stack<Long>();
        parentIds.push(0L);
        parentIds.push(0L);
    }

    public void handleEmptyMolecules() {
    }

    public void handleStartContainsMolecules(Set<NewMolecule> newMolecules) {
        parentId = moleculeId;
        moleculeId = localizer.getNextMoleculeId();
        parentIds.push(moleculeId);
    }

    public void handleEndContainsMolecules(Set<NewMolecule> newMolecules) {
        Long tmpId = parentIds.pop();
        // Check to see if we have come to the deepest level of the molecule - that is we've not pushed on a new
        // molecule id and we've come to the end.  If we have then we need to pop that id off and get the next level
        // up.
        if (tmpId.equals(moleculeId)) {
            tmpId = parentIds.pop();
        }
        moleculeId = tmpId;
        parentId = parentIds.peek();
    }

    public void handleTriple(Triple triple) {
        try {
            Long[] quin = new Long[QUIN_SIZE];
            System.arraycopy(localizer.localizeTriple(triple), 0, quin, 0, MOLECULE_ID_INDEX);
            quin[MOLECULE_ID_INDEX] = moleculeId;
            quin[PARENT_ID_INDEX] = parentId;
            index.remove(quin);
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }
}
