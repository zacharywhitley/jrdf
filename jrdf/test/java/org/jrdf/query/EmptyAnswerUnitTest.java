package org.jrdf.query;

import junit.framework.TestCase;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivate;
import org.jrdf.util.test.SerializationTestUtil;
import org.jrdf.util.test.FieldPropertiesTestUtil;
import static org.jrdf.util.test.SerializationTestUtil.*;
import static org.jrdf.util.test.FieldPropertiesTestUtil.*;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.mem.TupleImpl;
import org.jrdf.graph.AnyNode;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Arrays;

public class EmptyAnswerUnitTest extends TestCase {
    private static final Class[] PARAM_TYPES ={};

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Answer.class, EmptyAnswer.class);
        checkImplementationOfInterfaceAndFinal(Serializable.class, EmptyAnswer.class);
        checkConstructor(EmptyAnswer.class, Modifier.PRIVATE, PARAM_TYPES);
        checkFieldPublicConstant(EmptyAnswer.class, "EMPTY_ANSWER");
        checkSerialialVersionUid(EmptyAnswer.class, -7374613298128439580L);
    }

    public void testEmptyAnswerValues() {
        Answer answer = EmptyAnswer.EMPTY_ANSWER;
        assertTrue(Arrays.equals(new String[]{}, answer.getColumnNames()));
        assertTrue(Arrays.equals(new String[][]{}, answer.getColumnValues()));
        assertEquals(0, answer.getTimeTaken());
        assertEquals(0, answer.numberOfTuples());
        assertEquals(EmptyAnswer.EMPTY_ANSWER, EmptyAnswer.EMPTY_ANSWER);
    }
}
