package org.jrdf.query;

import java.util.List;
import java.util.Collections;

/**
 * A variable in a {@link Query} or {@link Answer}.
 * @author Tom Adams
 * @version $Revision$
 */
public interface Variable {
    /**
     * The list of all possible variables, i.e. wildcard projection.
     */
    static final List<? extends Variable> ALL_VARIABLES = Collections.emptyList();

    /**
     * Returns the name of the variable.
     * <p>Note. The variable is returned without a variable prefix, e.g. ?, $ etc.</p>
     * @return The name of the variable.
     */
    String getName();
}
