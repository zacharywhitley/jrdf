package org.jrdf.graph;

/**
 * A node which represents any subject - unconstrained.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public final class AnySubjectNode implements SubjectNode {
    private static final long serialVersionUID = -971680612480915602L;
    public static final AnySubjectNode ANY_SUBJECT_NODE = new AnySubjectNode();

    private AnySubjectNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_SUBJECT_NODE;
    }
}