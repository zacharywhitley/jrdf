/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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
 */
package org.jrdf;

import org.jrdf.graph.Graph;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.JrdfQueryExecutorFactory;
import org.jrdf.query.QueryBuilder;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.RelationComparator;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.mem.SortedAttributeValuePairHelper;
import org.jrdf.query.relation.operation.Join;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.type.TypeComparator;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.sparql.analysis.VariableCollector;
import org.jrdf.sparql.builder.TripleBuilder;
import org.jrdf.sparql.parser.SparqlParser;
import org.jrdf.sparql.parser.ParserFactory;

/**
 * Uses the default wiring xml file or one given to it to construct various JRDF components using Spring.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class TestJRDFFactory implements JRDFFactory {
    private static final JRDFFactoryImpl FACTORY = new JRDFFactoryImpl();

    public static TestJRDFFactory getFactory() {
        return new TestJRDFFactory();
    }

    public void refresh() {
        FACTORY.refresh();
    }

    public Graph getNewGraph() {
        return FACTORY.getNewGraph();
    }

    public AttributeValuePairComparator getNewAttributeValuePairComparator() {
        return FACTORY.getNewAttributeValuePairComparator();
    }

    public NodeComparator getNewNodeComparator() {
        return FACTORY.getNewNodeComparator();
    }

    public AttributeComparator getNewAttributeComparator() {
        return FACTORY.getNewAttributeComparator();
    }

    public TupleComparator getNewTupleComparator() {
        return FACTORY.getNewTupleComparator();
    }

    public RelationComparator getNewRelationComparator() {
        return FACTORY.getNewRelationComparator();
    }

    public Join getNewJoin() {
        return FACTORY.getNewJoin();
    }

    public SparqlConnection getNewSparqlConnection() {
        return FACTORY.getNewSparqlConnection();
    }

    public JrdfQueryExecutorFactory getNewJrdfQueryExecutorFactory() {
        return FACTORY.getNewJrdfQueryExecutorFactory();
    }

    public QueryBuilder getNewQueryBuilder() {
        return (QueryBuilder) FACTORY.getContext().getBean("queryBuilder");
    }

    public Project getNewProject() {
        return (Project) FACTORY.getContext().getBean("project");
    }

    public SparqlParser getNewSparqlParser() {
        return (SparqlParser) FACTORY.getContext().getBean("sparqlParser");
    }

    public TripleBuilder getNewTripleBuilder() {
        return (TripleBuilder) FACTORY.getContext().getBean("tripleBuilder");
    }

    public SortedAttributeValuePairHelper getNewSortedAttributeValuePairHelper() {
        return (SortedAttributeValuePairHelper) FACTORY.getContext().getBean("sortedAttributeValuePairHelper");
    }

    public TypeComparator getNewTypeComparator() {
        return (TypeComparator) FACTORY.getContext().getBean("typeComparator");
    }

    public AttributeNameComparator getNewAttributeNameComparator() {
        return (AttributeNameComparator) FACTORY.getContext().getBean("attributeNameComparator");
    }

    public ParserFactory getNewParserFactory() {
        return (ParserFactory) FACTORY.getContext().getBean("parserFactory");
    }

    public RelationFactory getNewRelationFactory() {
        return (RelationFactory) FACTORY.getContext().getBean("relationFactory");
    }

    public VariableCollector getNewVariableCollector() {
        return (VariableCollector) FACTORY.getContext().getBean("variableCollector");
    }
}
