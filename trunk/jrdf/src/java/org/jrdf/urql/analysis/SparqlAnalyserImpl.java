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

package org.jrdf.urql.analysis;

import org.jrdf.graph.Graph;
import org.jrdf.query.AskQueryImpl;
import org.jrdf.query.Query;
import org.jrdf.query.SelectQueryImpl;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import static org.jrdf.urql.analysis.NoQuery.NO_QUERY;
import org.jrdf.urql.builder.TripleBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.Start;
import org.jrdf.urql.parser.parser.ParserException;
import org.jrdf.util.param.ParameterUtil;

/**
 * Default implementation of {@link SparqlAnalyser}.
 *
 * @author Tom Adams
 * @version $Revision: 982 $
 */
public final class SparqlAnalyserImpl extends DepthFirstAdapter implements SparqlAnalyser {
    private final GraphRelationFactory graphRelationFactory;
    private Query query = NO_QUERY;
    private TripleBuilder tripleBuilder;
    private Graph graph;
    private Expression expression;
    private ParserException exception;

    public SparqlAnalyserImpl(TripleBuilder tripleBuilder, Graph graph, GraphRelationFactory graphRelationFactory) {
        ParameterUtil.checkNotNull(tripleBuilder, graph, graphRelationFactory);
        this.tripleBuilder = tripleBuilder;
        this.graph = graph;
        this.graphRelationFactory = graphRelationFactory;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        if (expression != null && query == NO_QUERY) {
            if (expression instanceof Projection) {
                query = new SelectQueryImpl(expression, graphRelationFactory);
            } else if (expression instanceof Ask) {
                query = new AskQueryImpl(expression, graphRelationFactory);
            }
        }
        return query;
    }

    @Override
    public void inStart(Start node) {
        try {
            PrefixAnalyser analyser = new PrefixAnalyserImpl(tripleBuilder, graph);
            node.apply(analyser);
            expression = analyser.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }
}
