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

package org.jrdf.graph.local.index.nodepool;

import org.jrdf.graph.Node;
import org.jrdf.graph.util.StringNodeMapper;
import org.jrdf.parser.ntriples.parser.LiteralMatcher;
import org.jrdf.util.test.ParameterDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkClassPublic;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import static org.jrdf.util.test.MockTestUtil.createMock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocalStringNodeMapper.class)
public class LocalStringNodeMapperUnitTest {
    private static final Class<?> TEST_CLASS = LocalStringNodeMapper.class;
    private static final Class<?> TARGET_INTERFACE = StringNodeMapper.class;
    private static final Class<?>[] PARAM_TYPES = {LiteralMatcher.class};

    @Test
    public void classProperties() {
        checkClassPublic(TEST_CLASS);
        checkImplementationOfInterface(TARGET_INTERFACE, TEST_CLASS);
        checkConstructNullAssertion(TEST_CLASS, PARAM_TYPES);
    }

    @Test
    public void mapperChecksForNull() {
        LiteralMatcher matcher = createMock(LiteralMatcher.class);
        StringNodeMapper mapper = new LocalStringNodeMapper(matcher);
        checkMethodNullAssertions(mapper, "convertToString", new ParameterDefinition(new String[]{"node"},
            new Class<?>[]{Node.class}));
        checkMethodNullAssertions(mapper, "convertToBlankNode", new ParameterDefinition(new String[]{"string"},
            new Class<?>[]{String.class}));
        checkMethodNullAssertions(mapper, "convertToURIReference", new ParameterDefinition(new String[]{"string",
            "nodeId"}, new Class<?>[]{String.class, Long.class}));
        checkMethodNullAssertions(mapper, "convertToLiteral", new ParameterDefinition(new String[]{"string",
            "nodeId"}, new Class<?>[]{String.class, Long.class}));
    }

    @Test
    public void anyNodeReturnsNull() {
        StringNodeMapper nodeMapper = new LocalStringNodeMapperFactory().createMapper();
        assertThat(nodeMapper.convertToString(ANY_SUBJECT_NODE), is(nullValue()));
        assertThat(nodeMapper.convertToString(ANY_PREDICATE_NODE), is(nullValue()));
        assertThat(nodeMapper.convertToString(ANY_OBJECT_NODE), is(nullValue()));
    }
}

