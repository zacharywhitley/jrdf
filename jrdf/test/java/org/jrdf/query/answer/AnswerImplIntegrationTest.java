/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

package org.jrdf.query.answer;

import com.gargoylesoftware.base.testing.TestUtil;
import junit.framework.TestCase;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.mem.TupleImplUnitTest;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.RESOURCE_1;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.util.test.ReflectTestUtil;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Unit test for {@link AnswerImpl}.
 *
 * @author Tom Adams
 * @version $Revision: 1510 $
 */
public final class AnswerImplIntegrationTest extends TestCase {
    private static final String FOO_SUBJECT_ATTRIBUTE = "Subject|foo1";
    private static final String FOO_SUBJECT_VARIABLE = "foo1";

    public void testSerialization() throws Exception {
        RelationFactory relationFactory = new QueryFactoryImpl().createRelationFactory();
        LinkedHashSet<Attribute> heading = new LinkedHashSet<Attribute>();
        heading.add(POS_FOO1_SUBJECT);
        Set<Tuple> tuples = new HashSet<Tuple>();
        tuples.add(TupleImplUnitTest.TEST_TUPLE_1);
        Answer answer = new AnswerImpl(heading, relationFactory.getRelation(tuples), 1000L, true);
        checkAnswer(answer, FOO_SUBJECT_ATTRIBUTE, FOO_SUBJECT_VARIABLE, RESOURCE_1.toString(), 1000L, true);
        Answer answer2 = (Answer) TestUtil.copyBySerialization(answer);
        // read the graph
        checkAnswer(answer2, FOO_SUBJECT_ATTRIBUTE, FOO_SUBJECT_VARIABLE, RESOURCE_1.toString(), 1000L, true);
    }

    private void checkAnswer(Answer answer, String expectedAttribute, String expectedVariable, String expectedValue,
        long expectedTimeTaken, boolean expectedProjected) {
        String[] variableNames = answer.getVariableNames();
        Attribute[] attributes = answer.getColumns();
        assertTrue(variableNames.length == 1);
        assertTrue(attributes.length == 1);
        assertEquals(expectedVariable, variableNames[0]);
        assertEquals(expectedAttribute, attributes[0].toString());
        String[][] values = answer.getColumnValues();
        assertEquals(1, values.length);
        assertEquals(1, values[0].length);
        assertEquals(expectedValue, values[0][0]);
        assertEquals(expectedTimeTaken, answer.getTimeTaken());
        Boolean actualProjected = (Boolean) ReflectTestUtil.getFieldValue(answer, "hasProjected");
        assertEquals(expectedProjected, actualProjected.booleanValue());
    }
}
