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

package org.jrdf.graph.global;

import org.jrdf.JRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphFactory;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.global.index.adapter.LongIndexAdapter;
import org.jrdf.graph.global.index.longindex.MoleculeIndex;
import org.jrdf.graph.global.index.longindex.mem.MoleculeIndexMem;
import org.jrdf.graph.local.BlankNodeComparator;
import org.jrdf.graph.local.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.LocalizedNodeComparator;
import org.jrdf.graph.local.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.NodeComparatorImpl;
import org.jrdf.graph.local.OrderedGraphFactoryImpl;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.mem.MemNodePoolFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.attributename.AttributeNameComparatorImpl;
import org.jrdf.query.relation.mem.AttributeComparatorImpl;
import org.jrdf.query.relation.mem.AttributeValuePairComparatorImpl;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.query.relation.mem.AttributeValuePairHelperImpl;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import org.jrdf.query.relation.mem.GraphRelationFactoryImpl;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import org.jrdf.query.relation.mem.TupleComparatorImpl;
import org.jrdf.query.relation.mem.TupleFactoryImpl;
import org.jrdf.query.relation.type.TypeComparator;
import org.jrdf.query.relation.type.TypeComparatorImpl;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.urql.UrqlConnectionImpl;
import org.jrdf.urql.builder.QueryBuilder;
import org.jrdf.urql.builder.UrqlQueryBuilder;
import org.jrdf.urql.parser.ParserFactory;
import org.jrdf.urql.parser.ParserFactoryImpl;
import org.jrdf.urql.parser.SableCcSparqllParser;
import org.jrdf.urql.parser.SparqlParser;
import org.jrdf.util.ClosableMap;
import org.jrdf.util.ClosableMapImpl;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.util.Set;

/**
 * Uses default in memory constructors to create JRDF entry points.  Returns sorted results.
 *
 * @author Andrew Newman
 * @version $Id: TestJRDFFactory.java 533 2006-06-04 17:50:31 +1000 (Sun, 04 Jun 2006) newmana $
 */
public final class SortedDiskGlobalJRDFFactory implements JRDFFactory {
    private static final NodeTypeComparator NODE_TYPE_COMPARATOR = new NodeTypeComparatorImpl();
    private static final TypeComparator TYPE_COMPARATOR = new TypeComparatorImpl(NODE_TYPE_COMPARATOR);
    private static final AttributeNameComparator ATTRIBUTE_NAME_COMPARATOR = new AttributeNameComparatorImpl();
    private static final AttributeComparator ATTRIBUTE_COMPARATOR = new AttributeComparatorImpl(TYPE_COMPARATOR,
        ATTRIBUTE_NAME_COMPARATOR);
    private static final SortedAttributeFactory ATTRIBUTE_FACTORY = new SortedAttributeFactoryImpl(
        ATTRIBUTE_COMPARATOR, 0L);
    private static final LocalizedNodeComparator LOCALIZED_NODE_COMPARATOR = new LocalizedNodeComparatorImpl();
    private static final BlankNodeComparator BLANK_NODE_COMPARATOR = new LocalizedBlankNodeComparatorImpl(
        LOCALIZED_NODE_COMPARATOR);
    private static final NodeComparator NODE_COMPARATOR = new NodeComparatorImpl(NODE_TYPE_COMPARATOR,
        BLANK_NODE_COMPARATOR);
    private static final AttributeValuePairComparator ATTRIBUTE_VALUE_PAIR_COMPARATOR =
        new AttributeValuePairComparatorImpl(ATTRIBUTE_COMPARATOR, NODE_COMPARATOR);
    private static final TupleFactory TUPLE_FACTORY = new TupleFactoryImpl(ATTRIBUTE_VALUE_PAIR_COMPARATOR);
    private static final TupleComparator TUPLE_COMPARATOR = new TupleComparatorImpl(ATTRIBUTE_VALUE_PAIR_COMPARATOR);
    private static final QueryBuilder BUILDER = createQueryBuilder();
    private static final QueryEngine QUERY_ENGINE = new QueryFactoryImpl().createQueryEngine();
    private GraphFactory orderedGraphFactory;

    private SortedDiskGlobalJRDFFactory() {
    }

    public static JRDFFactory getFactory() {
        return SortedMemoryGlobalJRDFFactory.getFactory();
    }

    public void refresh() {
    }

    public Graph getNewGraph() {
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> map1 =
            new ClosableMapImpl<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>();
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> map2 =
            new ClosableMapImpl<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>();
        ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>> map3 =
            new ClosableMapImpl<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>();
        MoleculeIndex<Long> spom = new MoleculeIndexMem(map1);
        MoleculeIndex<Long> posm = new MoleculeIndexMem(map2);
        MoleculeIndex<Long> ospm = new MoleculeIndexMem(map3);
        LongIndex[] indexes = new LongIndex[]{new LongIndexAdapter(spom), new LongIndexAdapter(posm),
            new LongIndexAdapter(ospm)};
        NodePoolFactory nodePoolFactory = new MemNodePoolFactory();
        orderedGraphFactory = new OrderedGraphFactoryImpl(indexes, nodePoolFactory);
        return orderedGraphFactory.getGraph();
    }

    public UrqlConnection getNewUrqlConnection() {
        return new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }

    public void close() {
    }

    private static QueryBuilder createQueryBuilder() {
        AttributeValuePairHelper avpHelper = new AttributeValuePairHelperImpl(ATTRIBUTE_FACTORY);
        GraphRelationFactory graphRelationFactory = new GraphRelationFactoryImpl(ATTRIBUTE_FACTORY, avpHelper,
            TUPLE_COMPARATOR, TUPLE_FACTORY);
        ParserFactory parserFactory = new ParserFactoryImpl();
        SparqlParser sparqlParser = new SableCcSparqllParser(parserFactory, graphRelationFactory, avpHelper,
            ATTRIBUTE_FACTORY);
        return new UrqlQueryBuilder(sparqlParser);
    }
}