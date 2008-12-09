/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

package org.jrdf.query.relation.attributename;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import static org.jrdf.util.test.ComparatorTestUtil.checkNullPointerException;

/**
 * Test that position names comes before variable names and if the same by name.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class AttributeNameComparatorImplIntegrationTest extends TestCase {
    private static final int EQUAL = 0;
    private static final int BEFORE = -1;
    private static final int AFTER = 1;
    private static final AttributeName VAR_BAR = new VariableName("bar");
    private static final AttributeName VAR_FOO = new VariableName("foo");
    private static final AttributeName POS_BAR = new PositionName("bar");
    private static final AttributeName POS_FOO = new PositionName("foo");
    private AttributeNameComparator attNameComparator;

    protected void setUp() throws Exception {
        super.setUp();
        attNameComparator = TestJRDFFactory.getFactory().getNewAttributeNameComparator();
    }

    public void testNullPointerException() {
        checkNullPointerException(attNameComparator, VAR_BAR, null);
        checkNullPointerException(attNameComparator, null, VAR_BAR);
    }

    public void testIdentity() {
        assertEquals(EQUAL, attNameComparator.compare(VAR_BAR, VAR_BAR));
    }

    public void testAttributeNameTypeOrder() {
        assertEquals(BEFORE, attNameComparator.compare(VAR_BAR, VAR_FOO));
        assertEquals(BEFORE, attNameComparator.compare(POS_BAR, VAR_BAR));
        assertEquals(BEFORE, attNameComparator.compare(POS_FOO, VAR_BAR));
        assertEquals(BEFORE, attNameComparator.compare(POS_BAR, POS_FOO));
    }

    public void testAttributeNameTypeAntiCommutation() {
        assertEquals(AFTER, attNameComparator.compare(VAR_FOO, VAR_BAR));
        assertEquals(AFTER, attNameComparator.compare(VAR_BAR, POS_BAR));
        assertEquals(AFTER, attNameComparator.compare(VAR_BAR, POS_FOO));
        assertEquals(AFTER, attNameComparator.compare(POS_FOO, POS_BAR));
    }
}
