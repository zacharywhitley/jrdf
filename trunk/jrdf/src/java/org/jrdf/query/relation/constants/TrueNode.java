package org.jrdf.query.relation.constants;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Node;

/**
 * Something in here
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class TrueNode implements Node {
  public static final Node TRUE = new TrueNode();
  private static final long serialVersionUID = 1808216129525892255L;
  private TrueNode() {
  }
}