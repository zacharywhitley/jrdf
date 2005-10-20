package org.jrdf.query.relation.constants;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.jrdf.graph.Node;

/**
 * A class which simply contains the True Node constant.
 * @author Andrew Newman
 * @version $Revision$
 */
public final class TrueNode implements Node, Serializable {

    /**
     * The node which represents the boolean logic value "TRUE".
     */
    public static final Node TRUE = new TrueNode();
    private static final long serialVersionUID = 1808216129525892255L;

    private TrueNode() {
    }

    private Object readResolve() throws ObjectStreamException {
        return TRUE;
    }
}