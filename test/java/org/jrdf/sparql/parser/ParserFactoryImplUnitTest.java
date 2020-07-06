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

package org.jrdf.sparql.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.sparql.parser.lexer.Lexer;
import org.jrdf.sparql.parser.parser.Parser;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAndEmptyAssertions;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.MockTestUtil.createMock;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldPrivateConstant;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldIsOfTypePrivateAndFinal;
import static org.jrdf.util.test.ReflectTestUtil.checkFieldValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.createMockAndExpectNew;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.lang.reflect.Modifier;

@RunWith(PowerMockRunner.class)
public final class ParserFactoryImplUnitTest {
    private static final int BUFFER_SIZE = 256;
    private static final String BUFFER_SIZE_FIELD = "PUSHBACK_BUFFER_SIZE";

    @Test
    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(ParserFactory.class, ParserFactoryImpl.class);
        checkConstructor(ParserFactoryImpl.class, Modifier.PUBLIC);
    }

    @Test
    public void testSetBufferSize() {
        checkFieldIsOfTypePrivateAndFinal(ParserFactoryImpl.class, Integer.TYPE, BUFFER_SIZE_FIELD);
        checkFieldPrivateConstant(ParserFactoryImpl.class, BUFFER_SIZE_FIELD);
        checkFieldValue(new ParserFactoryImpl(), BUFFER_SIZE_FIELD, BUFFER_SIZE);
    }

    @Test
    public void testCreateParserBadQuery() {
        ParserFactory parserFactory = new ParserFactoryImpl();
        checkMethodNullAndEmptyAssertions(parserFactory, "getParser", new ParameterDefinition(
            new String[]{"queryText"}, new Class[]{String.class}));
    }

    @Test
    @PrepareForTest(ParserFactoryImpl.class)
    public void createParser() throws Exception {
        final StringReader reader = createMockAndExpectNew(StringReader.class, "foo");
        final PushbackReader pushbackReader = createMockAndExpectNew(PushbackReader.class, reader, 256);
        final Lexer lexer = createMockAndExpectNew(Lexer.class, pushbackReader);
        final Parser mockParser = createMockAndExpectNew(Parser.class, lexer);
        replayAll();
        final ParserFactory parserFactory = new ParserFactoryImpl();
        final Parser actualParser = parserFactory.getParser("foo");
        verifyAll();
        assertThat(actualParser, equalTo(mockParser));
    }

    @Test
    @PrepareForTest(ParserFactoryImpl.class)
    public void closeThrowsException() throws Exception {
        final PushbackReader reader = createMock(PushbackReader.class);
        reader.close();
        expectLastCall().andThrow(new IOException());
        replayAll();
        final ParserFactory parserFactory = new ParserFactoryImpl();
        Whitebox.setInternalState(parserFactory, "reader", reader);
        AssertThrows.assertThrows(RuntimeException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                parserFactory.close();
            }
        });
        verifyAll();
    }
}
