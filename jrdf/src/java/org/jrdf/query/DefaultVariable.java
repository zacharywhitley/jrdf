package org.jrdf.query;

import java.io.Serializable;

/**
 * Default implementation of {@link Variable}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
final class DefaultVariable implements Variable, Serializable {
    // FIXME: Check for immutability of parameters.

    // FIXME: Fix serialVersionUID value
    static final long serialVersionUID = -1L;
    private String name;

    /**
     * Creates a variable with the given name.
     */
    public DefaultVariable(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }
}
