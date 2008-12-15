/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.query;

import org.jrdf.graph.NodeComparator;
import org.jrdf.query.execute.OptimizingQueryEngineImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.attributename.AttributeNameComparatorImpl;
import org.jrdf.query.relation.mem.AttributeComparatorImpl;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.query.relation.mem.AttributeValuePairHelperImpl;
import org.jrdf.query.relation.mem.ComparatorFactory;
import org.jrdf.query.relation.mem.ComparatorFactoryImpl;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import org.jrdf.query.relation.mem.GraphRelationFactoryImpl;
import org.jrdf.query.relation.mem.RelationFactoryImpl;
import org.jrdf.query.relation.mem.RelationHelper;
import org.jrdf.query.relation.mem.RelationHelperImpl;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import org.jrdf.query.relation.mem.TupleComparatorImpl;
import org.jrdf.query.relation.mem.TupleFactoryImpl;
import org.jrdf.query.relation.operation.BooleanEvaluator;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.operation.NadicJoin;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.operation.Restrict;
import org.jrdf.query.relation.operation.Union;
import org.jrdf.query.relation.operation.mem.common.RelationProcessor;
import org.jrdf.query.relation.operation.mem.common.RelationProcessorImpl;
import org.jrdf.query.relation.operation.mem.join.NadicJoinImpl;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;
import org.jrdf.query.relation.operation.mem.join.natural.SortMergeNaturalJoinEngine;
import org.jrdf.query.relation.operation.mem.logic.SimpleBooleanEvaluator;
import org.jrdf.query.relation.operation.mem.project.ProjectImpl;
import org.jrdf.query.relation.operation.mem.restrict.RestrictImpl;
import org.jrdf.query.relation.operation.mem.union.MinimumUnionImpl;
import org.jrdf.query.relation.operation.mem.union.MinimumUnionLeftOuterJoinImpl;
import org.jrdf.query.relation.operation.mem.union.OuterUnionEngine;
import org.jrdf.query.relation.operation.mem.union.OuterUnionImpl;
import org.jrdf.query.relation.operation.mem.union.SubsumptionEngine;
import org.jrdf.query.relation.type.TypeComparator;
import org.jrdf.query.relation.type.TypeComparatorImpl;
import org.jrdf.urql.builder.QueryBuilder;
import org.jrdf.urql.builder.UrqlQueryBuilder;
import org.jrdf.urql.parser.ParserFactory;
import org.jrdf.urql.parser.ParserFactoryImpl;
import org.jrdf.urql.parser.SableCcSparqllParser;
import org.jrdf.urql.parser.SparqlParser;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;

public class QueryFactoryImpl implements QueryFactory {
    private static final NodeTypeComparator NODE_TYPE_COMPARATOR = new NodeTypeComparatorImpl();
    private static final TypeComparator TYPE_COMPARATOR = new TypeComparatorImpl(NODE_TYPE_COMPARATOR);
    private static final AttributeNameComparator ATTRIBUTE_NAME_COMPARATOR = new AttributeNameComparatorImpl();
    private static final AttributeComparator ATTRIBUTE_COMPARATOR = new AttributeComparatorImpl(TYPE_COMPARATOR,
        ATTRIBUTE_NAME_COMPARATOR);
    private static final SortedAttributeFactory ATTRIBUTE_FACTORY = new SortedAttributeFactoryImpl(
        ATTRIBUTE_COMPARATOR, 0L);
    private static final ComparatorFactory COMPARATOR_FACTORY = new ComparatorFactoryImpl();
    private static final NodeComparator NODE_COMPARATOR = COMPARATOR_FACTORY.createNodeComparator();
    private static final TupleFactory TUPLE_FACTORY = new TupleFactoryImpl();
    private static final TupleComparator TUPLE_COMPARATOR = new TupleComparatorImpl(NODE_COMPARATOR,
        ATTRIBUTE_COMPARATOR);
    private static final RelationFactory RELATION_FACTORY = new RelationFactoryImpl(ATTRIBUTE_COMPARATOR,
        TUPLE_COMPARATOR);
    private static final RelationHelper RELATION_HELPER = new RelationHelperImpl(ATTRIBUTE_COMPARATOR);
    private static final RelationProcessor RELATION_PROCESSOR = new RelationProcessorImpl(RELATION_FACTORY,
        TUPLE_COMPARATOR);

    public QueryBuilder createQueryBuilder() {
        AttributeValuePairHelper avpHelper = new AttributeValuePairHelperImpl();
        GraphRelationFactory graphRelationFactory = new GraphRelationFactoryImpl(ATTRIBUTE_FACTORY, avpHelper,
            TUPLE_COMPARATOR, TUPLE_FACTORY);
        ParserFactory parserFactory = new ParserFactoryImpl();
        SparqlParser sparqlParser = new SableCcSparqllParser(parserFactory, graphRelationFactory,
            ATTRIBUTE_FACTORY);
        return new UrqlQueryBuilder(sparqlParser);
    }

    public RelationFactory createRelationFactory() {
        return RELATION_FACTORY;
    }

    public QueryEngine createQueryEngine() {
        Project project = new ProjectImpl(TUPLE_FACTORY, RELATION_FACTORY);
        TupleEngine joinTupleEngine =
            new SortMergeNaturalJoinEngine(TUPLE_FACTORY, RELATION_HELPER, NODE_COMPARATOR);
        TupleEngine unionTupleEngine = new OuterUnionEngine(RELATION_HELPER);
        NadicJoin join = new NadicJoinImpl(RELATION_PROCESSOR, joinTupleEngine);
        BooleanEvaluator evaluator = new SimpleBooleanEvaluator(NODE_COMPARATOR);
        Restrict restrict = new RestrictImpl(RELATION_FACTORY, TUPLE_FACTORY, TUPLE_COMPARATOR, evaluator);
        Union union = new OuterUnionImpl(RELATION_PROCESSOR, unionTupleEngine);
        DyadicJoin leftOuterJoin = getLeftOuterJoin(unionTupleEngine, join);
        return new OptimizingQueryEngineImpl(project, join, restrict, union, leftOuterJoin);
    }

    private DyadicJoin getLeftOuterJoin(TupleEngine unionTupleEngine, NadicJoin join) {
        SubsumptionEngine subsumptionEngine = new SubsumptionEngine(TUPLE_FACTORY, RELATION_HELPER);
        Union minUnion = new MinimumUnionImpl(RELATION_PROCESSOR, unionTupleEngine, subsumptionEngine);
        return new MinimumUnionLeftOuterJoinImpl(join, minUnion);
    }
}
