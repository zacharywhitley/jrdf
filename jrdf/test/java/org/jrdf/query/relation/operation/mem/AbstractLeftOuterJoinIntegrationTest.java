package org.jrdf.query.relation.operation.mem;

import junit.framework.TestCase;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.Relation;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createASingleTuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_PREDICATE_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelation;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.*;
import org.jrdf.query.relation.operation.DyadicJoin;

import java.util.Set;

/**
 * The common set of tests that all left outer join implementations must pass.
 *
 * @author Andrew Newman
 * @version $Revision: 729 $
 */
public abstract class AbstractLeftOuterJoinIntegrationTest extends TestCase {

    public void testLeftOuterJoin1() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, VAR_BAR1_PREDICATE_R1));

        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2);
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R3));

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_PREDICATE_R2, POS_FOO3_OBJECT_R3, POS_FOO4_PREDICATE_R2);
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, VAR_BAR1_PREDICATE_R1, POS_FOO4_PREDICATE_R3));

        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testLeftOuterJoin2() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2);
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3));
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R5));

        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3,
                VAR_BAR1_SUBJECTPREDICATE_R3);
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4));
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R2));

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3,
                VAR_BAR1_SUBJECTPREDICATE_R3);
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4));
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R5));

        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testLeftOuterJoin3() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2);
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3));
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R2));

        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3,
                VAR_BAR1_SUBJECTPREDICATE_R3);
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4));
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R5));

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3,
                VAR_BAR1_SUBJECTPREDICATE_R3);
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4));
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R2));

        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public abstract DyadicJoin getJoin();

    private void checkJoin(Relation expectedResult, Relation relation1, Relation relation2) {
        Relation relation = getJoin().join(relation1, relation2);

//        Set<Tuple> sortedTuples = relation.getSortedTuples();
//        Set<Tuple> sortedTuples2 = expected.getSortedTuples();
//        System.err.println("Sorted Actual tuples relation: " + relation.getSortedTuples());
//        System.err.println("Sorted Expected tuples relation: " + expected.getSortedTuples());
//        System.err.println("-------------------------------");
//        boolean isEqual = sortedTuples.equals(sortedTuples2);
//        System.err.println("Sorted Expected tuples relation1: " + isEqual);
//        System.err.println("Sorted Expected tuples relation2: " + expected.getSortedTuples().equals(relation.getSortedTuples()));
//        System.err.println("Sorted Expected tuples relation3: " + relation.getSortedTuples().equals(expected.getSortedTuples()));
        assertEquals(expectedResult, relation);
    }
}
