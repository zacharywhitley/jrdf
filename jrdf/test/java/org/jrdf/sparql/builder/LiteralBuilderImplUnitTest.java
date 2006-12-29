/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.sparql.builder;

import junit.framework.TestCase;
import org.easymock.classextension.IMocksControl;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.sparql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.sparql.parser.node.AQuotedLiteralLiteral;
import org.jrdf.sparql.parser.node.AQuotedUnescapedQuotedStrand;
import org.jrdf.sparql.parser.node.PQuotedStrand;
import org.jrdf.sparql.parser.node.TQtext;
import org.jrdf.sparql.parser.node.TQuote;
import org.jrdf.sparql.parser.node.ADbQuotedLiteralLiteral;
import org.jrdf.sparql.parser.node.TDbquote;
import org.jrdf.sparql.parser.node.PDbQuotedStrand;
import org.jrdf.sparql.parser.node.ADbQuotedUnescapedDbQuotedStrand;
import org.jrdf.sparql.parser.node.TDbqtext;
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
import java.util.List;

@SuppressWarnings({ "unchecked" })
public class LiteralBuilderImplUnitTest extends TestCase {
    private static final MockFactory factory = new MockFactory();
    private static final Class[] CONSTRUCTOR_PARAM_TYPES = new Class[] {GraphElementFactory.class};
    private static final String[] PARAM_NAMES = {"element"};
    private static final Class[] PARAM_TYPES = {ALiteralObjectTripleElement.class};
    private static final ParameterDefinition BUILD_PARAM_DEFINITION = new ParameterDefinition(PARAM_NAMES, PARAM_TYPES);
    private static final LiteralBuilder BUILDER = new LiteralBuilderImpl(factory.createMock(GraphElementFactory.class));

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(LiteralBuilder.class, LiteralBuilderImpl.class);
        checkConstructor(LiteralBuilderImpl.class, Modifier.PUBLIC, GraphElementFactory.class);
    }

    public void testBadParams() throws Exception {
        checkConstructorSetsFieldsAndFieldsPrivateFinal(LiteralBuilderImpl.class,
                CONSTRUCTOR_PARAM_TYPES, new String[] {"factory"});
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
        factory.reset();
        IMocksControl mocksControl = factory.createControl();
        final LiteralBuilder builder = createBuilder(mocksControl);
        mocksControl.andThrow(new GraphElementFactoryException("foo"));

        factory.replay();
        checkThrowsException(builder);
        factory.verify();
    }

    private void checkLiteralCreation(ALiteralObjectTripleElement element) throws GraphElementFactoryException {
        factory.reset();
        Literal literal = factory.createMock(Literal.class);
        IMocksControl mocksControl = factory.createControl();
        LiteralBuilder builder = createBuilder(mocksControl);
        mocksControl.andReturn(literal);

        factory.replay();
        checkReturnsLiteral(builder, element, literal);
        factory.verify();
    }

    private void checkReturnsLiteral(LiteralBuilder builder, ALiteralObjectTripleElement element, Literal expectedLiteral) throws GraphElementFactoryException {
        Literal actualLiteral = builder.createLiteral(element);
        assertNotNull(actualLiteral);
        assertEquals(expectedLiteral, actualLiteral);
    }

    private void checkThrowsException(final LiteralBuilder builder) {
        AssertThrows.assertThrows(GraphElementFactoryException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                builder.createLiteral(createQuotedElement());
            }
        });
    }

    private LiteralBuilder createBuilder(IMocksControl mocksControl) throws GraphElementFactoryException {
        GraphElementFactory graphFactory = mocksControl.createMock(GraphElementFactory.class);
        graphFactory.createLiteral("hello");
        return new LiteralBuilderImpl(graphFactory);
    }

    private ALiteralObjectTripleElement createQuotedElement() {
        List<PQuotedStrand> strand = new ArrayList<PQuotedStrand>();
        strand.add(new AQuotedUnescapedQuotedStrand(new TQtext("hello")));
        TQuote tQuote = new TQuote("'");
        AQuotedLiteralLiteral quotedLiteralLiteral = new AQuotedLiteralLiteral(tQuote, strand, tQuote);
        return new ALiteralObjectTripleElement(quotedLiteralLiteral);
    }

    private ALiteralObjectTripleElement createDoubleQuotedElement() {
        List<PDbQuotedStrand> strand = new ArrayList<PDbQuotedStrand>();
        strand.add(new ADbQuotedUnescapedDbQuotedStrand(new TDbqtext("hello")));
        TDbquote tDbquote = new TDbquote("\"");
        ADbQuotedLiteralLiteral quotedLiteralLiteral = new ADbQuotedLiteralLiteral(tDbquote, strand, tDbquote);
        return new ALiteralObjectTripleElement(quotedLiteralLiteral);
    }
}

