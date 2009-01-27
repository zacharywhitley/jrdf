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

package org.jrdf.graph.datatype;

import junit.framework.TestCase;
import org.jrdf.graph.Literal;
import org.jrdf.graph.local.LiteralImpl;
import org.jrdf.vocabulary.XSD;

public class SemanticLiteralComparatorImplUnitTest extends TestCase {
    private SemanticLiteralComparator semanticComparator;

    public void setUp() {
        semanticComparator = new SemanticLiteralComparatorImpl(new LexicalComparatorImpl());
    }

    public void testSemanticallyEqualNumbers() {
        final Literal literal1 = new LiteralImpl("4", XSD.INT);
        final Literal literal2 = new LiteralImpl("04", XSD.INTEGER);
        final Literal literal3 = new LiteralImpl("4", XSD.LONG);
        final Literal literal4 = new LiteralImpl("4", XSD.NON_NEGATIVE_INTEGER);
        final Literal literal5 = new LiteralImpl("4", XSD.FLOAT);
        final Literal literal6 = new LiteralImpl("4.0", XSD.FLOAT);
        isEqual(literal1, literal2);
        isEqual(literal1, literal3);
        isEqual(literal1, literal4);
        isEqual(literal2, literal3);
        isEqual(literal2, literal4);
        isEqual(literal5, literal6);
    }

    public void testSemanticallyEqualStrings() {
        final Literal literal1 = new LiteralImpl("foo");
        final Literal literal2 = new LiteralImpl("foo", XSD.STRING);
        isEqual(literal1, literal2);
    }

    private void isEqual(Literal literal1, Literal literal2) {
        assertEquals(0, semanticComparator.compare(literal1, literal2));
        assertEquals(0, semanticComparator.compare(literal2, literal1));
    }
}
