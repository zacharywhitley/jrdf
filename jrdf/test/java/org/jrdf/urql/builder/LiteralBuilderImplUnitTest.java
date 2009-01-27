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

package org.jrdf.urql.builder;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.urql.parser.node.ADbQuotedLiteralLiteralValue;
import org.jrdf.urql.parser.node.ADbQuotedUnescapedDbQuotedStrand;
import org.jrdf.urql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.urql.parser.node.AQuotedLiteralLiteralValue;
import org.jrdf.urql.parser.node.AQuotedUnescapedQuotedStrand;
import org.jrdf.urql.parser.node.ARdfLiteralLiteral;
import org.jrdf.urql.parser.node.AUntypedLiteralRdfLiteral;
import org.jrdf.urql.parser.node.PDbQuotedStrand;
import org.jrdf.urql.parser.node.PLiteralValue;
import org.jrdf.urql.parser.node.PQuotedStrand;
import org.jrdf.urql.parser.node.TDbqtext;
import org.jrdf.urql.parser.node.TDbquote;
import org.jrdf.urql.parser.node.TQtext;
import org.jrdf.urql.parser.node.TQuote;
import org.jrdf.urql.parser.parser.ParserException;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivateFinal;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked" })
public class LiteralBuilderImplUnitTest extends TestCase {
    private static final MockFactory FACTORY = new MockFactory();
    private static final Class[] CONSTRUCTOR_PARAM_TYPES = new Class[]{GraphElementFactory.class, Map.class};
    private static final String[] PARAM_NAMES = {"element"};
    private static final Class[] PARAM_TYPES = {ALiteralObjectTripleElement.class};
    private static final ParameterDefinition BUILD_PARAM_DEFINITION = new ParameterDefinition(PARAM_NAMES, PARAM_TYPES);
    private static final HashMap<String, String> PREFIX_MAP = new HashMap<String, String>();
    private static final LiteralBuilder BUILDER = new LiteralBuilderImpl(FACTORY.createMock(GraphElementFactory.class),
        PREFIX_MAP);

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(LiteralBuilder.class, LiteralBuilderImpl.class);
        checkConstructor(LiteralBuilderImpl.class, Modifier.PUBLIC, GraphElementFactory.class, Map.class);
    }

    public void testBadParams() throws Exception {
        checkConstructorSetsFieldsAndFieldsPrivateFinal(LiteralBuilderImpl.class,
            CONSTRUCTOR_PARAM_TYPES, new String[]{"factory"});
        checkConstructNullAssertion(LiteralBuilderImpl.class, CONSTRUCTOR_PARAM_TYPES);
        checkMethodNullAssertions(BUILDER, "createLiteral", BUILD_PARAM_DEFINITION);
    }

    public void testCreateLiteralFromQuotedLiteral() throws Exception {
        checkLiteralCreation(createQuotedElement());
    }

    public void testCreateLiteralFromDoubleQuotedLiteral() throws Exception {
        checkLiteralCreation(createDoubleQuotedElement());
    }

    public void testCreateLiteralWithException() throws Exception {
        FACTORY.reset();
        final LiteralBuilder builder = createBuilder();
        expectLastCall().andThrow(new GraphElementFactoryException("foo"));
        FACTORY.replay();
        checkThrowsException(builder);
        FACTORY.verify();
    }

    private void checkLiteralCreation(ALiteralObjectTripleElement element) throws Exception {
        FACTORY.reset();
        Literal literal = FACTORY.createMock(Literal.class);
        LiteralBuilder builder = createBuilder();
        expectLastCall().andReturn(literal);

        FACTORY.replay();
        checkReturnsLiteral(builder, element, literal);
        FACTORY.verify();
    }

    private void checkReturnsLiteral(LiteralBuilder builder, ALiteralObjectTripleElement element,
        Literal expectedLiteral) throws Exception {
        Literal actualLiteral = builder.createLiteral(element);
        assertNotNull(actualLiteral);
        assertEquals(expectedLiteral, actualLiteral);
    }

    private void checkThrowsException(final LiteralBuilder builder) {
        AssertThrows.assertThrows(ParserException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                builder.createLiteral(createQuotedElement());
            }
        });
    }

    private LiteralBuilder createBuilder() throws GraphException {
        GraphElementFactory graphFactory = FACTORY.createMock(GraphElementFactory.class);
        graphFactory.createLiteral("hello");
        return new LiteralBuilderImpl(graphFactory, new HashMap<String, String>());
    }

    private ALiteralObjectTripleElement createQuotedElement() {
        List<PQuotedStrand> strand = new ArrayList<PQuotedStrand>();
        strand.add(new AQuotedUnescapedQuotedStrand(new TQtext("hello")));
        TQuote tQuote = new TQuote("'");
        PLiteralValue quotedLiteralLiteral = new AQuotedLiteralLiteralValue(tQuote, strand, tQuote);
        return new ALiteralObjectTripleElement(
                new ARdfLiteralLiteral(new AUntypedLiteralRdfLiteral(quotedLiteralLiteral)));
    }

    private ALiteralObjectTripleElement createDoubleQuotedElement() {
        List<PDbQuotedStrand> strand = new ArrayList<PDbQuotedStrand>();
        strand.add(new ADbQuotedUnescapedDbQuotedStrand(new TDbqtext("hello")));
        TDbquote tDbquote = new TDbquote("\"");
        PLiteralValue quotedLiteralLiteral = new ADbQuotedLiteralLiteralValue(tDbquote, strand, tDbquote);
        final AUntypedLiteralRdfLiteral untypedLiteral = new AUntypedLiteralRdfLiteral(quotedLiteralLiteral);
        final ARdfLiteralLiteral rdfLiteral = new ARdfLiteralLiteral(untypedLiteral);
        return new ALiteralObjectTripleElement(rdfLiteral);
    }
}

