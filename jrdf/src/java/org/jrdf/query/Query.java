package org.jrdf.query;

import java.util.List;

/**
 * A query to a graph.
 * @author Tom Adams
 * @version $Revision$
 */
public interface Query {
    void setProjectedVariables(List<? extends Variable> variables);

    List<Variable> getProjectedVariables();
}
