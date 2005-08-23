package org.jrdf.query.relation.constants;

import org.jrdf.graph.Node;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.query.relation.AttributeNameValue;
import org.jrdf.query.relation.Relation;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Dum is a relation with no tuples and is the base relation for FALSE.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class Relation_DUM implements Relation {
    private static final Node FALSE_SUBJECT_NODE = FalseNode.FALSE_SUBJECT_NODE;
    private static final Set<AttributeNameValue> EMPTY = Collections.emptySet();

    public Set<SubjectNode> getTupleNames() {
        return Collections.singleton((SubjectNode) FALSE_SUBJECT_NODE);
    }

    public Set<PredicateNode> getAttributeNames() {
        return Collections.emptySet();
    }

    public Map<SubjectNode, Set<AttributeNameValue>> getTuples(Set<SubjectNode> tupleNames) {
        return Collections.singletonMap((SubjectNode) FALSE_SUBJECT_NODE, EMPTY);
    }

}