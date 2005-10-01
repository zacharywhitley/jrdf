package org.jrdf.query.relation.constants;

import org.jrdf.graph.Node;

import java.io.ObjectStreamException;

/**
 * A class which simply contains the False Node constant.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class FalseNode implements Node {

    /**
     * The node which represents the boolean logic value "FALSE".
     */
    public static final Node FALSE_SUBJECT_NODE = new FalseNode();
    private static final long serialVersionUID = 4580621120190884185L;

    private FalseNode() {
    }

    private Object readResolve() throws ObjectStreamException {
        return FALSE_SUBJECT_NODE;
    }
}