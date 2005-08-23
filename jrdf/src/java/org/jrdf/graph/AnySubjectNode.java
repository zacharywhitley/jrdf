package org.jrdf.graph;

/**
 * A node which represents any subject - unconstrained.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public final class AnySubjectNode implements SubjectNode {
    public static final AnySubjectNode ANY_SUBJECT_NODE = new AnySubjectNode();

    private AnySubjectNode() {
    }

    Object readResolve() throws java.io.ObjectStreamException {
        return ANY_SUBJECT_NODE;
    }
}