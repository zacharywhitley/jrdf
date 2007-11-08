package org.jrdf.graph.local.mem.copyUtil;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Nov 7, 2007
 * Time: 10:58:22 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CopyGraphUtil {
        // a triple with blank nodes is only added once to the hash map
    Graph copyGraph(Graph sg, Graph tg) throws GraphException;

    Graph copyTriplesForNode(Node node, Graph sg, Graph tg) throws GraphException;
}
