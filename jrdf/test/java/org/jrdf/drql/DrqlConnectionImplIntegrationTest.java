/*
 * $Header$
 * $Revision: 962 $
 * $Date: 2006-11-16 22:09:02 +1000 (Thu, 16 Nov 2006) $
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

package org.jrdf.drql;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.query.Answer;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.drql.builder.QueryBuilder;
import org.jrdf.util.test.ReflectTestUtil;
import static org.jrdf.util.test.DrqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
import static org.jrdf.util.test.DrqlQueryTestUtil.QUERY_BOOK_2_DC_TITLE;
import static org.jrdf.util.test.TripleTestUtil.LITERAL_BOOK_TITLE;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_1;
import static org.jrdf.util.test.TripleTestUtil.URI_DC_TITLE;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;

public final class DrqlConnectionImplIntegrationTest extends TestCase {

    // FIXME TJA: Add test that ensures that createConnection() returns a new connection each time.

    private static final String QUERY_SHOULD_RETURN_ONE_SOLUTION = QUERY_BOOK_1_DC_TITLE;
    private static final String QUERY_SHOULD_RETURN_NOTHING = QUERY_BOOK_2_DC_TITLE;
    private static final URI URI_SUBJECT = URI_BOOK_1;
    private static final URI URI_PREDICATE = URI_DC_TITLE;
    private static final String LITERAL_TITLE = LITERAL_BOOK_TITLE;
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final QueryBuilder QUERY_BUILDER = FACTORY.getNewQueryBuilder();
    private static final Graph GRAPH = FACTORY.getNewGraph();
    private static final QueryEngine QUERY_ENGINE = FACTORY.getNewQueryEngine();

    @Override
    public void setUp() throws Exception {
        GraphElementFactory elementFactory = GRAPH.getElementFactory();
        SubjectNode subject = elementFactory.createResource(URI_BOOK_1);
        PredicateNode predicate = elementFactory.createResource(URI_DC_TITLE);
        ObjectNode object = elementFactory.createLiteral(LITERAL_BOOK_TITLE);
        GRAPH.add(subject, predicate, object);
    }

    public void testCreateDrqlConnection() {
        checkConnectionReturnsOneSolution(createRawConnection());
        checkConnectionReturnsNoSolutions(createRawConnection());
    }

    private DrqlConnection createRawConnection() {
        return new DrqlConnectionImpl(QUERY_BUILDER, QUERY_ENGINE);
    }

    private void checkConnectionReturnsOneSolution(DrqlConnection connection) {
        Relation relation = executeQuery(connection, QUERY_SHOULD_RETURN_ONE_SOLUTION);
        Set<Tuple> answer = relation.getTuples();
        checkFirstRowOfAnswer(answer);
    }

    private void checkConnectionReturnsNoSolutions(DrqlConnection connection) {
        Relation relation = executeQuery(connection, QUERY_SHOULD_RETURN_NOTHING);
        Set<Tuple> solutions = relation.getTuples();
        assertTrue(solutions.isEmpty());
    }

    private Relation executeQuery(DrqlConnection connection, String query) {
        try {
            Answer answer = connection.executeQuery(GRAPH, query);
            return (Relation) ReflectTestUtil.getFieldValue(answer, "results");
        } catch (InvalidQuerySyntaxException e) {
            throw new RuntimeException(e);
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkFirstRowOfAnswer(Set<Tuple> solutions) {
        Tuple tuple = solutions.iterator().next();
        Set<AttributeValuePair> sortedAttributeValues = tuple.getSortedAttributeValues();
        Iterator<AttributeValuePair> iterator = sortedAttributeValues.iterator();
        checkSubject((SubjectNode) iterator.next().getValue());
        checkPredicate((PredicateNode) iterator.next().getValue());
        checkLiteral((Literal) iterator.next().getValue());
    }

    // TODO AN Why do we need to call toString should they be equal?
    private void checkSubject(SubjectNode subject) {
        assertEquals(URI_SUBJECT.toString(), subject.toString());
    }

    // TODO AN Why do we need to call toString should they be equal?
    private void checkPredicate(PredicateNode predicate) {
        assertEquals(URI_PREDICATE.toString(), predicate.toString());
    }

    private void checkLiteral(Literal object) {
        assertEquals(LITERAL_TITLE, object.getLexicalForm());
    }
}
