package org.jrdf.graph;

import java.io.Serializable;
import java.io.ObjectStreamException;

/**
 * A node which represents any subject - unconstrained.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class AnySubjectNode implements SubjectNode, Serializable {
    /**
     * The any predicate node constant - represents an unconstrained predicate node.
     */
    public static final AnySubjectNode ANY_SUBJECT_NODE = new AnySubjectNode();

    private static final long serialVersionUID = -971680612480915602L;
    private static final String STRING_FORM = "ANY_SUBJECT";

    private AnySubjectNode() {
    }

    public String toString() {
        return STRING_FORM;
    }

    private Object readResolve() throws ObjectStreamException {
        return ANY_SUBJECT_NODE;
    }
}