package org.jrdf.query.relation.constants;

import org.jrdf.graph.Node;

/**
 * Something in here
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class FalseNode implements Node {
    public static final Node FALSE_SUBJECT_NODE = new FalseNode();
    private static final long serialVersionUID = 4580621120190884185L;

    private FalseNode() {
    }
}