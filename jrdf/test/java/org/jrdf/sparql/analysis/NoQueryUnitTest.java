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

package org.jrdf.sparql.analysis;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.graph.Graph;
import org.jrdf.query.Query;
import static org.jrdf.query.answer.EmptyAnswer.EMPTY_ANSWER;
import org.jrdf.query.execute.QueryEngine;
import static org.jrdf.sparql.analysis.NoQuery.NO_QUERY;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldPublicConstant;
import static org.jrdf.util.test.SerializationTestUtil.checkSerialialVersionUid;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.jrdf.util.test.MockTestUtil.createMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Serializable;
import java.lang.reflect.Modifier;

@RunWith(PowerMockRunner.class)
public class NoQueryUnitTest {
    private static final Class[] PARAM_TYPES = {};

    @Test
    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Query.class, NoQuery.class);
        checkImplementationOfInterfaceAndFinal(Serializable.class, NoQuery.class);
        checkConstructor(NoQuery.class, Modifier.PRIVATE, PARAM_TYPES);
        checkFieldPublicConstant(NoQuery.class, "NO_QUERY");
        checkSerialialVersionUid(NoQuery.class, -1815852679585213051L);
    }

    @Test
    public void testNoQueryValues() {
        Graph graph = createMock(Graph.class);
        QueryEngine queryEngine = createMock(QueryEngine.class);
        assertThat(NO_QUERY, notNullValue());
        assertThat(NO_QUERY.executeQuery(graph, queryEngine), equalTo(EMPTY_ANSWER));
    }
}
