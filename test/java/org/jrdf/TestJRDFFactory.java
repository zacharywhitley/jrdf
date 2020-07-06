/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

package org.jrdf;

import org.jrdf.collection.MapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphFactory;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.local.ReadWriteGraphFactory;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.RelationComparator;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.operation.NadicJoin;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.operation.SemiDifference;
import org.jrdf.query.relation.operation.Union;
import org.jrdf.query.relation.operation.mem.common.RelationProcessor;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;
import org.jrdf.query.relation.type.TypeComparator;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.sparql.analysis.VariableCollector;
import org.jrdf.sparql.builder.QueryBuilder;
import org.jrdf.sparql.builder.TripleBuilder;
import org.jrdf.sparql.parser.ParserFactory;
import org.jrdf.sparql.parser.SparqlParser;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Uses the default wiring xml file or one given to it to construct various JRDF components using Spring.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class TestJRDFFactory implements JRDFFactory {
    private static final TestJRDFFactory SELF = new TestJRDFFactory();
    private static final String DEFAULT_WIRING_CONFIG = "gui-wiring.xml";
    private static final ClassPathXmlApplicationContext BEAN_FACTORY =
        new ClassPathXmlApplicationContext(DEFAULT_WIRING_CONFIG);

    private TestJRDFFactory() {
    }

    public static TestJRDFFactory getFactory() {
        return SELF;
    }

    public void refresh() {
        BEAN_FACTORY.refresh();
    }

    public Graph getGraph() {
        GraphFactory graphFactory = (GraphFactory) BEAN_FACTORY.getBean("graphFactory");
        return graphFactory.getGraph();
    }

    public ReadWriteGraphFactory getNewReadWriteGraphFactory() {
        return (ReadWriteGraphFactory) BEAN_FACTORY.getBean("graphFactory");
    }

    public AttributeComparator getNewAttributeComparator() {
        return (AttributeComparator) BEAN_FACTORY.getBean("attributeComparator");
    }

    public TupleComparator getNewTupleComparator() {
        return (TupleComparator) BEAN_FACTORY.getBean("tupleComparator");
    }

    public SparqlConnection getNewSparqlConnection() {
        return (SparqlConnection) BEAN_FACTORY.getBean("sparqlConnection");
    }

    public void close() {
    }

    public NodeComparator getNewNodeComparator() {
        return (NodeComparator) BEAN_FACTORY.getBean("nodeComparator");
    }

    public RelationComparator getNewRelationComparator() {
        return (RelationComparator) BEAN_FACTORY.getBean("relationComparator");
    }

    public NadicJoin getNewNaturalJoin() {
        return (NadicJoin) BEAN_FACTORY.getBean("naturalJoin");
    }

    public TupleEngine getNewSortMergeNaturalJoinEngine() {
        return (TupleEngine) BEAN_FACTORY.getBean("sortMergeNaturalJoinEngine");
    }

    public DyadicJoin getNewSemiJoin() {
        return (DyadicJoin) BEAN_FACTORY.getBean("semiJoin");
    }

    public QueryBuilder getNewQueryBuilder() {
        return (QueryBuilder) BEAN_FACTORY.getBean("queryBuilder");
    }

    public Project getNewProject() {
        return (Project) BEAN_FACTORY.getBean("project");
    }

    public SparqlParser getNewSparqlParser() {
        return (SparqlParser) BEAN_FACTORY.getBean("sparqlParser");
    }

    public TripleBuilder getNewTripleBuilder() {
        return (TripleBuilder) BEAN_FACTORY.getBean("tripleBuilder");
    }

    public AttributeValuePairHelper getNewAttributeValuePairHelper() {
        return (AttributeValuePairHelper) BEAN_FACTORY.getBean("attributeValuePairHelper");
    }

    public TypeComparator getNewTypeComparator() {
        return (TypeComparator) BEAN_FACTORY.getBean("typeComparator");
    }

    public AttributeNameComparator getNewAttributeNameComparator() {
        return (AttributeNameComparator) BEAN_FACTORY.getBean("attributeNameComparator");
    }

    public ParserFactory getNewParserFactory() {
        return (ParserFactory) BEAN_FACTORY.getBean("parserFactory");
    }

    public RelationProcessor getNewRelationProcessor() {
        return (RelationProcessor) BEAN_FACTORY.getBean("relationProcessor");
    }

    public RelationFactory getNewRelationFactory() {
        return (RelationFactory) BEAN_FACTORY.getBean("relationFactory");
    }

    public VariableCollector getNewVariableCollector() {
        return (VariableCollector) BEAN_FACTORY.getBean("variableCollector");
    }

    public Union getNewMinimumUnion() {
        return (Union) BEAN_FACTORY.getBean("minimumUnion");
    }

    public Union getNewOuterUnion() {
        return (Union) BEAN_FACTORY.getBean("outerUnion");
    }

    public SemiDifference getNewSemiDifference() {
        return (SemiDifference) BEAN_FACTORY.getBean("semiDifference");
    }

    public DyadicJoin getNewLeftOuterJoin() {
        return (DyadicJoin) BEAN_FACTORY.getBean("leftOuterJoin");
    }

    public DyadicJoin getNewFullOuterJoin() {
        return (DyadicJoin) BEAN_FACTORY.getBean("fullOuterJoin");
    }

    public QueryEngine getNewQueryEngine() {
        return (QueryEngine) BEAN_FACTORY.getBean("queryEngine");
    }

    public DyadicJoin getNewMinimumFullOuterJoin() {
        return (DyadicJoin) BEAN_FACTORY.getBean("minFullOuterJoin");
    }

    public DyadicJoin getNewMinimumLeftOuterJoin() {
        return (DyadicJoin) BEAN_FACTORY.getBean("minLeftOuterJoin");
    }

    public TupleEngine getNewSubsumptionEngine() {
        return (TupleEngine) BEAN_FACTORY.getBean("subsumptionJoinEngine");
    }

    public GraphRelationFactory getNewGraphRelationFactory() {
        return (GraphRelationFactory) BEAN_FACTORY.getBean("graphRelationFactory");
    }

    public MapFactory getMapFactory() {
        return (MapFactory) BEAN_FACTORY.getBean("mapFactory");
    }
}
