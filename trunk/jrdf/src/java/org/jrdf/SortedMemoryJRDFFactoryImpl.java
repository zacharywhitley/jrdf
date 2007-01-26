package org.jrdf;

import org.jrdf.graph.Graph;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.operation.Union;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.longindex.mem.LongIndexMem;
import org.jrdf.graph.index.nodepool.mem.NodePoolMemImpl;
import org.jrdf.graph.index.nodepool.mem.NodePoolMem;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.mem.OrderedGraphFactoryImpl;
import org.jrdf.graph.mem.NodeComparatorImpl;
import org.jrdf.graph.mem.GraphFactory;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.sparql.SparqlConnectionImpl;
import org.jrdf.sparql.parser.SparqlParser;
import org.jrdf.sparql.parser.SableCcSparqlParser;
import org.jrdf.sparql.parser.ParserFactory;
import org.jrdf.sparql.parser.ParserFactoryImpl;
import org.jrdf.sparql.builder.QueryBuilder;
import org.jrdf.sparql.builder.SparqlQueryBuilder;
import org.jrdf.util.NodeTypeComparatorImpl;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import org.jrdf.query.relation.mem.GraphRelationFactoryImpl;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import org.jrdf.query.relation.mem.AttributeComparatorImpl;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.query.relation.mem.AttributeValuePairHelperImpl;
import org.jrdf.query.relation.mem.TupleComparatorImpl;
import org.jrdf.query.relation.mem.AttributeValuePairComparatorImpl;
import org.jrdf.query.relation.mem.TupleFactoryImpl;
import org.jrdf.query.relation.mem.RelationFactoryImpl;
import org.jrdf.query.relation.mem.RelationHelper;
import org.jrdf.query.relation.mem.RelationHelperImpl;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.operation.mem.project.ProjectImpl;
import org.jrdf.query.relation.operation.mem.join.NadicJoinImpl;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;
import org.jrdf.query.relation.operation.mem.join.natural.NaturalJoinEngine;
import org.jrdf.query.relation.operation.mem.common.RelationProcessor;
import org.jrdf.query.relation.operation.mem.common.RelationProcessorImpl;
import org.jrdf.query.relation.operation.mem.restrict.RestrictImpl;
import org.jrdf.query.relation.operation.mem.union.OuterUnionImpl;
import org.jrdf.query.relation.operation.mem.union.MinimumUnionLeftOuterJoinImpl;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.operation.NadicJoin;
import org.jrdf.query.relation.operation.Restrict;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.attributename.AttributeNameComparatorImpl;
import org.jrdf.query.relation.type.TypeComparator;
import org.jrdf.query.relation.type.TypeComparatorImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.query.execute.NaiveQueryEngineImpl;

/**
 * Uses default in memory constructors to create JRDF entry points.  Returns sorted results.
 *
 * @author Andrew Newman
 * @version $Id: TestJRDFFactory.java 533 2006-06-04 17:50:31 +1000 (Sun, 04 Jun 2006) newmana $
 */
public final class SortedMemoryJRDFFactoryImpl implements JRDFFactory {
    private static final NodeTypeComparator NODE_TYPE_COMPARATOR = new NodeTypeComparatorImpl();
    private static final TypeComparator TYPE_COMPARATOR = new TypeComparatorImpl(NODE_TYPE_COMPARATOR);
    private static final AttributeNameComparator ATTRIBUTE_NAME_COMPARATOR = new AttributeNameComparatorImpl();
    private static final AttributeComparator ATTRIBUTE_COMPARATOR = new AttributeComparatorImpl(TYPE_COMPARATOR,
        ATTRIBUTE_NAME_COMPARATOR);
    private static final SortedAttributeFactory ATTRIBUTE_FACTORY = new SortedAttributeFactoryImpl(
        ATTRIBUTE_COMPARATOR, 0L);
    private static final NodeComparator NODE_COMPARATOR = new NodeComparatorImpl();
    private static final AttributeValuePairComparator ATTRIBUTE_VALUE_PAIR_COMPARATOR =
        new AttributeValuePairComparatorImpl(ATTRIBUTE_COMPARATOR, NODE_COMPARATOR);
    private static final TupleFactory TUPLE_FACTORY = new TupleFactoryImpl(ATTRIBUTE_VALUE_PAIR_COMPARATOR);
    private static final TupleComparator TUPLE_COMPARATOR = new TupleComparatorImpl(ATTRIBUTE_VALUE_PAIR_COMPARATOR);
    private static final QueryBuilder BUILDER = createQueryBuilder();
    private static final QueryEngine QUERY_ENGINE = createQueryEngine();

    private SortedMemoryJRDFFactoryImpl() {
    }

    public static JRDFFactory getFactory() {
        return new SortedMemoryJRDFFactoryImpl();
    }

    public void refresh() {
    }

    public Graph getNewGraph() {
        LongIndex[] indexes = new LongIndex[]{new LongIndexMem(), new LongIndexMem(), new LongIndexMem()};
        NodePoolMem nodePool = new NodePoolMemImpl();
        NodeComparatorImpl comparator = new NodeComparatorImpl(new NodeTypeComparatorImpl());
        GraphFactory orderedGraphFactory = new OrderedGraphFactoryImpl(indexes, nodePool, comparator);
        return orderedGraphFactory.getGraph();
    }

    public SparqlConnection getNewSparqlConnection() {
        return new SparqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }

    private static QueryBuilder createQueryBuilder() {
        AttributeValuePairHelper avpHelper = new AttributeValuePairHelperImpl(ATTRIBUTE_FACTORY);
        GraphRelationFactory graphRelationFactory = new GraphRelationFactoryImpl(ATTRIBUTE_FACTORY, avpHelper,
            TUPLE_COMPARATOR, TUPLE_FACTORY);
        ParserFactory parserFactory = new ParserFactoryImpl();
        SparqlParser sparqlParser = new SableCcSparqlParser(parserFactory, graphRelationFactory, avpHelper,
            ATTRIBUTE_FACTORY);
        return new SparqlQueryBuilder(sparqlParser);
    }

    private static QueryEngine createQueryEngine() {
        RelationFactory relationFactory = new RelationFactoryImpl(ATTRIBUTE_COMPARATOR, TUPLE_COMPARATOR);
        Project project = new ProjectImpl(TUPLE_FACTORY, relationFactory);
        RelationProcessor relationProcessor = new RelationProcessorImpl(relationFactory, TUPLE_COMPARATOR);
        RelationHelper relationHelper = new RelationHelperImpl(ATTRIBUTE_COMPARATOR);
        TupleEngine tupleEngine = new NaturalJoinEngine(TUPLE_FACTORY, ATTRIBUTE_VALUE_PAIR_COMPARATOR,
            relationHelper);
        NadicJoin join = new NadicJoinImpl(relationProcessor, tupleEngine);
        Restrict restrict = new RestrictImpl(relationFactory);
        Union union = new OuterUnionImpl(relationProcessor, tupleEngine);
        DyadicJoin leftOuterJoin = new MinimumUnionLeftOuterJoinImpl(join, union);
        return new NaiveQueryEngineImpl(project, join, restrict, union, leftOuterJoin);
    }
}
