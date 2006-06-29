/*
 * $Header$
 * $Revision: 582 $
 * $Date: 2006-06-18 17:34:10 +1000 (Sun, 18 Jun 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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

package org.jrdf.query.execute;

import org.jrdf.graph.Graph;
import org.jrdf.query.JrdfQueryExecutor;
import org.jrdf.query.Query;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Constraint;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import org.jrdf.query.relation.operation.Join;
import org.jrdf.query.relation.operation.Restrict;
import org.jrdf.util.param.ParameterUtil;

import java.net.URI;

/**
 * A naive query executor that uses an iterator-based approach to finding triples.
 * <p>This is an initial attempt at a very basic JRDF query layer, before the real one gets written.</p>
 * <p>The basic algorithm is:</p>
 * <ul>
 * <li>Get all triples from the graph;</li>
 * <li>Iterate over them and match the query constraints against them.</li>
 * </ul>
 *
 * @author Tom Adams
 * @version $Id: NaiveQueryExecutor.java 582 2006-06-18 07:34:10Z newmana $
 */
final class NaiveQueryExecutor implements JrdfQueryExecutor {

    // TODO: Filter out the variables in the projection list.

    private Graph graph;
    private final Restrict restrict;
    private final Join join;
    private GraphRelationFactory graphRelationFactory;
    private Relation result;
    private final URI securityDomain;

    /**
     * Creates executor to execute queries.
     *
     * @param graph          The graph to communicate with.
     * @param securityDomain The security domain of the graph.
     */
    public NaiveQueryExecutor(Graph graph, URI securityDomain, Restrict restrict, Join join,
            GraphRelationFactory graphRelationFactory) {
        ParameterUtil.checkNotNull("graph", graph);
        ParameterUtil.checkNotNull("securityDomain", securityDomain);
        ParameterUtil.checkNotNull("restrict", restrict);
        ParameterUtil.checkNotNull("graphRelationFactory", graphRelationFactory);
        this.graph = graph;
        this.securityDomain = securityDomain;
        this.restrict = restrict;
        this.join = join;
        this.graphRelationFactory = graphRelationFactory;
        result = graphRelationFactory.createRelation(graph);
    }

    /**
     * {@inheritDoc}
     */
    public Relation executeQuery(Query query) {
        Expression<ExpressionVisitor> expression = query.getConstraintExpression();
        NaiveQueryEngineImpl naiveQueryEngine = new NaiveQueryEngineImpl(result, restrict, join);
        if (expression instanceof Constraint) {
            naiveQueryEngine.visitConstraint((Constraint<ExpressionVisitor>) expression);
        } else if (expression instanceof Conjunction) {
            naiveQueryEngine.visitConjunction((Conjunction<ExpressionVisitor>) expression);
        } else {
            throw new RuntimeException();
        }
        result = naiveQueryEngine.getResult();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        graph.close();
    }

    /**
     * Attempt to close the underlying session in case the client did not.
     * <p><strong>Clients should not rely on this method being called, it is only here as a last minute check to see if
     * any cleanup can be performed. This method is not guarenteed to be executed by the JVM.</strong></p>
     *
     * @throws Throwable An unknown error occurs, possibly in object finalisation.
     */
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            // FIXME TJA: See http://www.janeg.ca/scjp/gc/finalize.html
            super.finalize();
        }
    }
}
