package org.jrdf.query;

import org.jrdf.util.ClosableIterator;

/**
 * An answer to a query.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public interface Answer {
  ClosableIterator getClosableIterator();
}
