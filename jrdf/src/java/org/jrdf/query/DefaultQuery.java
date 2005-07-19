package org.jrdf.query;

import java.io.Serializable;
import java.util.List;
import java.util.Collections;

/**
 * Default implementation of a {@link Query}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultQuery implements Query, Serializable {
    // FIXME: Check for immutability of parameters.

    // FIXME: Breadcrumb - Things we may need.
    // List<Variable> variables
    // GraphPattern constraints

    // FIXME: Fix serialVersionUID value
    static final long serialVersionUID = -1L;

    public void setProjectedVariables(List<? extends Variable> variables) {
        // do something
    }

    public List<Variable> getProjectedVariables() {
        // FIXME: Breadcrumb - Implement this!!!
        return Collections.emptyList();
//        throw new UnsupportedOperationException("Implement me...");
    }
}
