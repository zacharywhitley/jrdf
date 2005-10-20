/*
 * $Header$
 * $Revision$
 * $Date$
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.query.Answer;
import org.jrdf.query.ConstraintExpression;
import org.jrdf.query.ConstraintTriple;
import org.jrdf.query.DefaultAnswer;
import org.jrdf.query.Query;
import org.jrdf.util.param.ParameterUtil;

/**
 * A naive query executor that uses an iterator-based approach to finding triples.
 * <p>This is an initial attempt at a very basic JRDF query layer, before the real one gets written.</p>
 * <p>The basic algorithm is:</p>
 * <ul>
 * <li>Get all triples from the graph;</li>
 * <li>Iterate over them and match the query constraints against them.</li>
 * </ul>
 * @author Tom Adams
 * @version $Id$
 */
final class NaiveQueryExecutor implements JrdfQueryExecutor {

    // TODO: Filter out the variables in the projection list.

    private Graph graph;
    private URI securityDomain;

    /**
     * Creates executor to execute queries.
     * @param graph The graph to communicate with.
     * @param securityDomain The security domain of the graph.
     */
    public NaiveQueryExecutor(Graph graph, URI securityDomain) {
        ParameterUtil.checkNotNull("session", graph);
        ParameterUtil.checkNotNull("securityDomain", securityDomain);
        this.graph = graph;
        this.securityDomain = securityDomain;
    }

    /**
     * {@inheritDoc}
     */
    public Answer executeQuery(Query query) throws GraphException {
        List<Triple> triples = findTriples(query);
        return new DefaultAnswer(triples);
    }

    // TODO: When the tests (& grammar) force it, get all the triples and iterate over them :)
    private List<Triple> findTriples(Query query) throws GraphException {
        ConstraintExpression constraints = query.getConstraintExpression();
        Triple triple = ((ConstraintTriple) constraints).getTriple();
        return iteratorToList(graph.find(triple));
    }

    private List<Triple> iteratorToList(Iterator<Triple> iterator) {
        List<Triple> triples = new ArrayList<Triple>();
        while (iterator.hasNext()) {
            triples.add(iterator.next());
        }
        return triples;
    }
}
