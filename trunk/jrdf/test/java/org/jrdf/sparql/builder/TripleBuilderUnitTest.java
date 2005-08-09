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

package org.jrdf.sparql.builder;

import junit.framework.TestCase;
import org.jrdf.graph.Triple;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.PTripleElement;
import org.jrdf.sparql.parser.node.AResourceTripleElement;
import org.jrdf.sparql.parser.node.TResource;
import org.jrdf.sparql.parser.node.AVariableTripleElement;
import org.jrdf.sparql.parser.node.AVariable;
import org.jrdf.sparql.parser.node.TVariableprefix;
import org.jrdf.sparql.parser.node.TIdentifier;
import org.jrdf.sparql.SparqlQueryTestUtil;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Unit test for {@link TripleBuilder}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class TripleBuilderUnitTest extends TestCase {

    private static final String URI_DC_TITLE = SparqlQueryTestUtil.URI_DC_TITLE;
    private static final Triple TRIPLE_BOOK_1_DC_TITLE = SparqlQueryTestUtil.TRIPLE_BOOK_1_DC_TITLE;
    private static final Triple TRIPLE_BOOK_2_DC_TITLE = SparqlQueryTestUtil.TRIPLE_BOOK_2_DC_TITLE;
    private static final String URI_BOOK_1 = SparqlQueryTestUtil.URI_BOOK_1;
    private static final String URI_BOOK_2 = SparqlQueryTestUtil.URI_BOOK_2;
    private static final String VARIABLE_PREFIX = SparqlQueryTestUtil.VARIABLE_PREFIX;
    private static final String VARIABLE_NAME_TITLE = SparqlQueryTestUtil.VARIABLE_NAME_TITLE;;
    private static final TripleBuilder BUILDER = new TripleBuilder();

    public void testClassProperties() {
        // FIXME TJA: Reenable this if we figure out how to do it generically.
//        ClassPropertiesTestUtil.checkExtensionOf(LocalObjectBuilder.class, TripleBuilder.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(TripleBuilder.class, TripleBuilder.class);
    }

    public void testBuildTripleFromParserNode() {
        checkBuiltTriple(URI_BOOK_1, TRIPLE_BOOK_1_DC_TITLE);
        checkBuiltTriple(URI_BOOK_2, TRIPLE_BOOK_2_DC_TITLE);
    }

    public void testNullThrowsException() {
        try {
            BUILDER.build(null);
            fail("build(null) should have thrown IllegalArgumentException");
        } catch (Exception expected) { }
    }

    private void checkBuiltTriple(String predicateUri, Triple expectedTriple) {
        Triple triple = BUILDER.build(createTripleNode(predicateUri));
        assertEquals(expectedTriple, triple);
    }

    private ATriple createTripleNode(String subjectUri) {
        PTripleElement subject = createResourceNode(subjectUri);
        PTripleElement predicate = createResourceNode(URI_DC_TITLE);
        PTripleElement object = createVariableNode(VARIABLE_NAME_TITLE);
        return new ATriple(subject, predicate, object);
    }

    private AResourceTripleElement createResourceNode(String subjectUri) {
        return new AResourceTripleElement(new TResource(subjectUri));
    }

    private AVariableTripleElement createVariableNode(String variableName) {
        AVariable variable = new AVariable(new TVariableprefix(VARIABLE_PREFIX), new TIdentifier(variableName));
        return new AVariableTripleElement(variable);
    }

}
