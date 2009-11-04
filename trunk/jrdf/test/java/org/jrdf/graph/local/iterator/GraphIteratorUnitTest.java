/*
 * $Header$
 * $Revision$
 * $Date$
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
package org.jrdf.graph.local.iterator;

import junit.framework.TestCase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.IteratorTestUtil.expectCallingRemoveBeforeNextThrowsException;
import static org.jrdf.util.test.IteratorTestUtil.expectCallingWithNoResultsThrowsException;
import static org.jrdf.util.test.MockTestUtil.createMock;
import org.jrdf.util.test.ParamSpec;
import org.jrdf.util.ClosableIterator;
import org.junit.Test;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.reflect.Whitebox;
import static org.easymock.EasyMock.expect;

import static java.lang.reflect.Modifier.PUBLIC;
import java.util.Iterator;

public class GraphIteratorUnitTest extends TestCase {
    private static final Class<GraphIterator> CLASS_UNDER_TEST = GraphIterator.class;

    public void testClassProperties() throws Exception {
        checkImplementationOfInterfaceAndFinal(ClosableLocalIterator.class, GraphIterator.class);
        checkConstructor(GraphIterator.class, PUBLIC, GraphHandler.class);
        checkConstructNullAssertion(CLASS_UNDER_TEST, GraphHandler.class);
    }

    @Test
    public void closableIteratorContractForNext() {
        final ParamSpec args = createArgsAndNoResultsExpectations();
        replayAll();
        final Iterator<Triple> iterator = expectCallingWithNoResultsThrowsException(CLASS_UNDER_TEST,
            args);
        verifyAll();
        assertThat(Whitebox.<Boolean>getInternalState(iterator, "hasClosed"), is(true));
    }

    @Test
    public void closableIteratorConstractForRemove() {
        final ParamSpec args = createArgsAndNoResultsExpectations();
        replayAll();
        final Iterator<Triple> iterator = expectCallingRemoveBeforeNextThrowsException(CLASS_UNDER_TEST,
            args);
        verifyAll();
        assertThat(Whitebox.<Boolean>getInternalState(iterator, "hasClosed"), is(true));
    }

    @SuppressWarnings({ "unchecked" })
    private ParamSpec createArgsAndNoResultsExpectations() {
        final GraphHandler handler = createMock(GraphHandler.class);
        final ClosableIterator<Long[]> iterator = createMock(ClosableIterator.class);
        expect(iterator.hasNext()).andReturn(false);
        expect(handler.getEntries()).andReturn(iterator);
        return new ParamSpec(new Class<?>[]{Long.class, GraphHandler.class}, new Object[]{handler});
    }
}
