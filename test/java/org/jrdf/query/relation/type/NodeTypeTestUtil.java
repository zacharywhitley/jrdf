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

package org.jrdf.query.relation.type;

import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.SerializationTestUtil;
import static org.jrdf.util.test.EqualsHashCodeTestUtil.assertEquality;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Set;

public class NodeTypeTestUtil extends TestCase {

    public static void checkClassProperties(Class<?> clazz) {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(NodeType.class, clazz);
        ClassPropertiesTestUtil.checkImplementationOfInterface(Serializable.class, clazz);
        ClassPropertiesTestUtil.checkConstructor(clazz, Modifier.PUBLIC);
    }

    public static void checkSerialVersionUid(Class<?> clazz, long expectedUid) {
        SerializationTestUtil.checkSerialialVersionUid(clazz, expectedUid);
    }

    public static void checkGetName(NodeType obj, String expectedName) {
        assertEquals(expectedName, obj.getName());
    }

    public static void checkComposedOf(NodeType actualNodeType, NodeType... expectedNodeTypes) {
        Set<? extends NodeType> nodeTypes = actualNodeType.composedOf();
        assertEquals(expectedNodeTypes.length, nodeTypes.size());
        for (NodeType expectedNodeType : expectedNodeTypes) {
            assertTrue(nodeTypes.contains(expectedNodeType));
        }
    }

    public static void checkEquals(NodeType nodeType, NodeType secondNodeType) {
        assertEquality(nodeType, secondNodeType, null, null);
    }
}
