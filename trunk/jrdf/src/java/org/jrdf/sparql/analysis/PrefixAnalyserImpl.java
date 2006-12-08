/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.drql.analysis;

import org.jrdf.graph.Graph;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.drql.builder.TripleBuilder;
import org.jrdf.drql.parser.analysis.DepthFirstAdapter;
import org.jrdf.drql.parser.node.APrefixdeclProlog;
import org.jrdf.drql.parser.node.AVariableListSelectClause;
import org.jrdf.drql.parser.node.AWildcardSelectClause;
import org.jrdf.drql.parser.node.Node;
import org.jrdf.drql.parser.node.PPrefixdecl;
import org.jrdf.drql.parser.parser.ParserException;

import java.util.LinkedList;

public class PrefixAnalyserImpl extends DepthFirstAdapter implements PrefixAnalyser {
    private TripleBuilder tripleBuilder;
    private Graph graph;
    private Expression<ExpressionVisitor> expression;
    private ParserException exception;

    public PrefixAnalyserImpl(TripleBuilder tripleBuilder, Graph graph) {
        this.tripleBuilder = tripleBuilder;
        this.graph = graph;
    }

    public Expression<ExpressionVisitor> getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    @Override
    public void caseAWildcardSelectClause(AWildcardSelectClause node) {
        expression = analyseSelectClause(node);
    }

    @Override
    public void caseAVariableListSelectClause(AVariableListSelectClause node) {
        expression = analyseSelectClause(node);
    }

    @Override
    public void caseAPrefixdeclProlog(APrefixdeclProlog node) {
        LinkedList<PPrefixdecl> prefixdecl = node.getPrefixdecl();
        SinglePrefixAnalyser prefixAnalyser = new SinglePrefixAnalyser(tripleBuilder);
        for (PPrefixdecl pPrefixdecl : prefixdecl) {
            pPrefixdecl.apply(prefixAnalyser);
        }
    }

    private Expression<ExpressionVisitor> analyseSelectClause(Node node) {
        try {
            SelectAnalyser analyser = new SelectAnalyserImpl(tripleBuilder, graph);
            node.apply(analyser);
            return analyser.getExpression();
        } catch (ParserException e) {
            exception = e;
            return null;
        }
    }

}
