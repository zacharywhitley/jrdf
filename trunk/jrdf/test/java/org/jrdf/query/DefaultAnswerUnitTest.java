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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;
import org.jrdf.graph.Triple;
import org.jrdf.sparql.SparqlQueryTestUtil;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Unit test for {@link DefaultAnswer}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultAnswerUnitTest extends TestCase {

    private static final String NEW_LINE = "\n";
    private static final String INDENT = "  ";
    private static final String STRING_FORM_NO_ELEMENTS = "{}";
    private static final String STRING_FORM_SINGLE_ELEMENT =
            "{" + NEW_LINE +
            INDENT + SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL + NEW_LINE +
            "}";
    private static final String STRING_FORM_MULTIPLE_ELEMENTS =
            "{" + NEW_LINE +
            INDENT + SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL + NEW_LINE +
            INDENT + SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE + NEW_LINE +
            "}";

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Answer.class, DefaultAnswer.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Serializable.class, DefaultAnswer.class);
        ClassPropertiesTestUtil.checkConstructor(DefaultAnswer.class, Modifier.PUBLIC, Iterator.class);
    }

    public void testSerialVersionUid() {
        assertEquals(-4724846731215773529L, DefaultAnswer.serialVersionUID);
    }

    public void testConstructorFailsWithNull() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new DefaultAnswer(null);
            }
        });
    }

    public void testGetAnswer() {
        MockClosableIterator expected = new MockClosableIterator(createEmptyTripleList());
        Answer answer = new DefaultAnswer(expected);
        Iterator<Triple> actual = answer.getSolutions();
        assertEquals(expected, actual);
    }

    public void testToString() {
        checkStringForm(STRING_FORM_NO_ELEMENTS, createEmptyTripleList());
        checkStringForm(STRING_FORM_SINGLE_ELEMENT, createSingleTripleList());
        checkStringForm(STRING_FORM_MULTIPLE_ELEMENTS, createMultipleTripleList());
    }


    private void checkStringForm(String expectedStringForm, Collection<Triple> content) {
        Answer answer = new DefaultAnswer(new MockClosableIterator(content));
        assertEquals(expectedStringForm, answer.toString());
    }

    private Collection<Triple> createEmptyTripleList() {
        return Collections.emptyList();
    }

    private Collection<Triple> createSingleTripleList() {
        Collection<Triple> triples = new ArrayList<Triple>();
        triples.add(SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL);
        return triples;
    }

    private Collection<Triple> createMultipleTripleList() {
        Collection<Triple> triples = new ArrayList<Triple>();
        triples.add(SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL);
        triples.add(SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE);
        return triples;
    }
}
