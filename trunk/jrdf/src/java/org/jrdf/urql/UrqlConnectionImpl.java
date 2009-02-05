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

package org.jrdf.urql;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.query.AskQueryImpl;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.AskAnswerImpl;
import static org.jrdf.query.answer.EmptyAnswer.EMPTY_ANSWER;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.urql.builder.QueryBuilder;
import static org.jrdf.util.param.ParameterUtil.checkNotEmptyString;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;


/**
 * Default implementation of a {@link UrqlConnection}.
 *
 * @author Tom Adams
 * @version $Id: SparqlConnectionImpl.java 982 2006-12-08 08:42:51Z newmana $
 */
public final class UrqlConnectionImpl implements UrqlConnection {
    // FIXME TJA: Ensure connections are threadsafe.
    private final QueryBuilder builder;
    private final QueryEngine<Relation> queryEngine;

    /**
     * Creates a new SPARQL connection.
     *
     * @param builder     the query builder that builds queries.
     * @param queryEngine the engine that executed the query.
     */
    public UrqlConnectionImpl(QueryBuilder builder, QueryEngine<Relation> queryEngine) {
        checkNotNull(builder, queryEngine);
        this.queryEngine = queryEngine;
        this.builder = builder;
    }

    public Answer executeQuery(Graph graph, String queryText) throws InvalidQuerySyntaxException, GraphException {
        checkNotNull(graph, queryText);
        checkNotEmptyString("queryText", queryText);
        Query query = builder.buildQuery(graph, queryText);
        if (graph.isEmpty()) {
            return getEmptyAnswer(query);
        }
        return query.executeQuery(graph, queryEngine);
    }

    private Answer getEmptyAnswer(Query query) {
        if (query instanceof AskQueryImpl) {
            return new AskAnswerImpl(0, false);
        } else {
            return EMPTY_ANSWER;
        }
    }
}
