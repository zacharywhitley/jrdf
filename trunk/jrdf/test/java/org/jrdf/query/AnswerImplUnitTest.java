/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.query;

import junit.framework.TestCase;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_FOO_POS;
import static org.jrdf.query.relation.mem.TupleImplUnitTest.TEST_TUPLE_1;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ReflectTestUtil;
import static org.jrdf.util.test.SerializationTestUtil.checkSerialialVersionUid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Unit test for {@link AnswerImpl}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class AnswerImplUnitTest extends TestCase {
    private static final Class<?>[] PARAM_TYPES = new Class[]{LinkedHashSet.class, Relation.class, Long.TYPE, Boolean.TYPE};
    private MockFactory factory = new MockFactory();

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Answer.class, AnswerImpl.class);
        checkImplementationOfInterfaceAndFinal(Serializable.class, AnswerImpl.class);
        checkConstructor(AnswerImpl.class, Modifier.PUBLIC, PARAM_TYPES);
        checkConstructNullAssertion(AnswerImpl.class, PARAM_TYPES);
    }

    public void testSerialVersionUid() {
        checkSerialialVersionUid(AnswerImpl.class, 3778815984074679718L);
    }

    public void testNullArgument() {
        Relation relation = factory.createMock(Relation.class);
        LinkedHashSet<Attribute> heading = new LinkedHashSet<Attribute>();
        factory.replay();
        new AnswerImpl(heading, relation, 100, true);
        factory.verify();
    }

    public void testSerialization() throws Exception {
        RelationFactory relationFactory = new QueryFactoryImpl().createRelationFactory();
        LinkedHashSet<Attribute> heading = new LinkedHashSet<Attribute>();
        heading.add(TEST_ATTRIBUTE_FOO_POS);
        Set<Tuple> tuples = new HashSet<Tuple>();
        tuples.add(TEST_TUPLE_1);
        Answer answer = new AnswerImpl(heading, relationFactory.getRelation(tuples), 1000L, true);
        checkAnswer(answer, "foo | Literal", 1000L, true);

        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputBytes);

        // write the graph
        os.writeObject(answer);

        // read a new graph back in
        ByteArrayInputStream inputBytes = new ByteArrayInputStream(outputBytes.toByteArray());
        ObjectInputStream is = new ObjectInputStream(inputBytes);

        // read the graph
        Answer answer2 = (Answer) is.readObject();
        checkAnswer(answer2, "foo | Literal", 1000L, true);
    }

    private void checkAnswer(Answer answer, String expectedColumnName, long expectedTimeTaken,
        boolean expectedProjected) {
        String[] strings = answer.getColumnNames();
        assertTrue(strings.length == 1);
        assertEquals(expectedColumnName, strings[0]);
//        System.err.println("Got: " + answer.getColumnValues().length);
//        System.err.println("Got: " + answer.getColumnValues()[0][0]);
        assertEquals(expectedTimeTaken, answer.getTimeTaken());
        Boolean actualProjected = (Boolean) ReflectTestUtil.getFieldValue(answer, "hasProjected");
        assertEquals(expectedProjected, actualProjected.booleanValue());
    }
}
