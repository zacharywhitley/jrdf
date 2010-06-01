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

package org.jrdf.query.relation.attributename;

import org.jrdf.util.test.AssertThrows;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.util.test.StandardClassPropertiesTestUtil.hasClassStandardProperties;

/**
 * Test the AttributeNameComparatorImpl
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class AttributeNameComparatorImplUnitTest {
    private static final Class<AttributeNameComparator> TARGET_INTERFACE = AttributeNameComparator.class;
    private static final Class<AttributeNameComparatorImpl> TEST_CLASS = AttributeNameComparatorImpl.class;
    private static final Class[] PARAM_TYPES = new Class[]{};
    private static final String[] PARAMETER_NAMES = new String[]{};

    @Test
    public void classProperties() {
        hasClassStandardProperties(TARGET_INTERFACE, TEST_CLASS, PARAM_TYPES, PARAMETER_NAMES);
    }

    @Test
    public void methodProperties() {
        AssertThrows.assertThrows(NullPointerException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new AttributeNameComparatorImpl().compare(null, null);
            }
        });
    }

    @Test
    public void twoVariablesTheSame() {
        AttributeName v1 = new VariableName("foo");
        AttributeName v2 = new VariableName("foo");
        assertThat(new AttributeNameComparatorImpl().compare(v1, v2), equalTo(0));
    }

    @Test
    public void twoVariablesTheSameFirstComesAfter() {
        AttributeName v1 = new VariableName("foo");
        AttributeName v2 = new VariableName("bar");
        assertThat(new AttributeNameComparatorImpl().compare(v1, v2), equalTo(1));
    }

    @Test
    public void twoVariablesTheSameFirstComesBefore() {
        AttributeName v1 = new VariableName("bar");
        AttributeName v2 = new VariableName("foo");
        assertThat(new AttributeNameComparatorImpl().compare(v1, v2), equalTo(-1));
    }

    @Test
    public void onePositionOneVariable() {
        AttributeName v1 = new VariableName("bar");
        AttributeName v2 = new PositionName("foo");
        assertThat(new AttributeNameComparatorImpl().compare(v1, v2), equalTo(1));
    }

    @Test
    public void oneVariableOnePosition() {
        AttributeName v1 = new PositionName("foo");
        AttributeName v2 = new VariableName("bar");
        assertThat(new AttributeNameComparatorImpl().compare(v1, v2), equalTo(-1));
    }
}
