package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;

/**
 * Represents an indexed set of longs.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface LongIndex {
  void add(Long first, Long second, Long third) throws GraphException;

  void remove(Long first, Long second, Long third)
      throws GraphException;
}
