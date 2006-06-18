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

import junit.framework.TestCase;
import org.jrdf.query.GraphFixture;
import org.jrdf.query.Answer;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.TripleTestUtil;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.MockTestUtil;
import org.jrdf.connection.JrdfConnectionFactory;
import org.jrdf.graph.Graph;

import java.net.URI;
import java.lang.reflect.Modifier;

/**
 * Unit test for {@link NaiveQueryExecutor}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class NaiveQueryExecutorUnitTest extends TestCase {

    private static final URI NO_SECURITY_DOMAIN = JrdfConnectionFactory.NO_SECURITY_DOMAIN;
    private static final AttributeValuePairComparator avpComparator =
            MockTestUtil.createFromInterface(AttributeValuePairComparator.class);
    private MockFactory mockFactory;

    public void setUp() {
        mockFactory = new MockFactory();
    }

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterface(JrdfQueryExecutor.class, NaiveQueryExecutor.class);
        ClassPropertiesTestUtil.checkConstructor(NaiveQueryExecutor.class, Modifier.PUBLIC, Graph.class, URI.class,
                AttributeValuePairComparator.class);
    }

    public void testNullSessionInConstructor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new NaiveQueryExecutor(null, NO_SECURITY_DOMAIN, avpComparator);
            }
        });
    }

    public void testNullSecurityDomainInConstructor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new NaiveQueryExecutor(GraphFixture.GRAPH_BAD, null, avpComparator);
            }
        });
    }

    public void testNullAvpComparatorInConstructor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new NaiveQueryExecutor(GraphFixture.GRAPH_BAD, NO_SECURITY_DOMAIN, null);
            }
        });
    }

    public void testExecuteQuery() throws Exception {
        JrdfQueryExecutor executor = new NaiveQueryExecutor(GraphFixture.createGraph(), NO_SECURITY_DOMAIN,
                avpComparator);
        Answer answer = executor.executeQuery(GraphFixture.createQuery());
        GraphFixture.checkAnswer(TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL, answer);
    }
}
