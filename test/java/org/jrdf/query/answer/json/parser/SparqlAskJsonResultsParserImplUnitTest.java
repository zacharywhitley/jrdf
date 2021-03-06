/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.query.answer.json.parser;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.query.answer.SparqlProtocol;
import org.jrdf.query.answer.SparqlResultType;
import org.jrdf.query.answer.TypeValue;
import org.jrdf.query.answer.TypeValueImpl;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.NoSuchElementException;

@RunWith(PowerMockRunner.class)
public class SparqlAskJsonResultsParserImplUnitTest {
    private static final String CANNOT_PARSE = "Cannot parse token: ";
    private static final JsonToken BOGUS_TOKEN = JsonToken.NOT_AVAILABLE;
    @Mock private JsonParser mockJsonParser;

    @Test
    public void testCreateWithoutBoolean() throws Exception {
        expect(mockJsonParser.getCurrentName()).andReturn("fred");
        replayAll();
        assertThrows(IllegalStateException.class, CANNOT_PARSE + "null", new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlAskJsonResultsParserImpl(mockJsonParser);
            }
        });
        verifyAll();
    }

    @Test
    public void testCreateWithTrue() throws Exception {
        createConstructorExpectations(SparqlProtocol.BOOLEAN, JsonToken.VALUE_TRUE);
        replayAll();
        final SparqlAskJsonResultsParserImpl parser = new SparqlAskJsonResultsParserImpl(mockJsonParser);
        verifyAll();
        checkResult(parser, "true");
    }

    @Test
    public void testCreateWithFalse() throws Exception {
        createConstructorExpectations(SparqlProtocol.BOOLEAN, JsonToken.VALUE_FALSE);
        replayAll();
        final SparqlAskJsonResultsParserImpl parser = new SparqlAskJsonResultsParserImpl(mockJsonParser);
        verifyAll();
        checkResult(parser, "false");
    }

    @Test
    public void tryingToReturnTwoResultsThrowsException() throws Exception {
        createConstructorExpectations(SparqlProtocol.BOOLEAN, JsonToken.VALUE_FALSE);
        replayAll();
        assertThrows(NoSuchElementException.class, "No more results available", new AssertThrows.Block() {
            public void execute() throws Throwable {
                final SparqlAskJsonResultsParserImpl parser = new SparqlAskJsonResultsParserImpl(mockJsonParser);
                verifyAll();
                parser.next();
                parser.next();
            }
        });

    }

    @Test
    public void testCreateWithIllegalValueThrowsIllegalStateException() throws Exception {
        createConstructorExpectations(SparqlProtocol.BOOLEAN, BOGUS_TOKEN);
        replayAll();
        assertThrows(IllegalStateException.class, CANNOT_PARSE + BOGUS_TOKEN, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlAskJsonResultsParserImpl(mockJsonParser);
            }
        });
        verifyAll();
    }

    @Test(expected = RuntimeException.class)
    public void testWrapsException() throws Exception {
        expect(mockJsonParser.getCurrentName()).andReturn(SparqlProtocol.BOOLEAN);
        expect(mockJsonParser.nextValue()).andThrow(new IOException());
        replayAll();
        new SparqlAskJsonResultsParserImpl(mockJsonParser);
        verifyAll();
    }

    @Test
    public void testRemove() throws Exception {
        createConstructorExpectations(SparqlProtocol.BOOLEAN, JsonToken.VALUE_FALSE);
        replayAll();
        assertThrows(UnsupportedOperationException.class, "Cannot remove on this iterator", new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlAskJsonResultsParserImpl(mockJsonParser).remove();
            }
        });
        verifyAll();
    }

    @Test
    public void testAlwaysReturnTrue() throws Exception {
        createConstructorExpectations(SparqlProtocol.BOOLEAN, JsonToken.VALUE_FALSE);
        replayAll();
        final boolean closeResult = new SparqlAskJsonResultsParserImpl(mockJsonParser).close();
        verifyAll();
        assertThat(closeResult, is(true));
    }

    private void createConstructorExpectations(final String nameToReturn, final JsonToken tokenToReturn)
        throws IOException {
        expect(mockJsonParser.getCurrentName()).andReturn(nameToReturn);
        expect(mockJsonParser.nextValue()).andReturn(tokenToReturn);
    }

    private void checkResult(final SparqlAskJsonResultsParserImpl parser, final String value) {
        assertThat(parser.hasNext(), is(true));
        assertThat(parser.next(), equalTo(new TypeValue[]{new TypeValueImpl(SparqlResultType.BOOLEAN, value)}));
    }
}