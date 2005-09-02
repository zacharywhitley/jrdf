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

package org.jrdf.query;

import java.net.URI;
import org.jrdf.connection.JrdfConnectionException;
import org.jrdf.graph.Graph;
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
 * @version $Revision$
 */
final class NaiveQueryExecutor implements JrdfQueryExecutor {

    // FIXME TJA: Use an iterator-based approach to finding triples.

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
    public Answer executeQuery(Query query) throws JrdfConnectionException {
        // FIXME TJA: Breadcrumb - Was implementing this after chasing down the null issue...
        return null;
//        throw new UnsupportedOperationException("Implement me...");
    }
}
