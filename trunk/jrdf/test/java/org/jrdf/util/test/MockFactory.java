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
package org.jrdf.util.test;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.util.test.instantiate.ArnoldTheInstantiator;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A utility that allows you to treat a collection of MockControls.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class MockFactory {
    private static final ArnoldTheInstantiator INSTANTIATOR = new ArnoldTheInstantiator();
    private List<IMocksControl> controls = new ArrayList<IMocksControl>();

    @SuppressWarnings({ "unchecked" })
    public <T> T createMock(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz.equals(Long.TYPE)) {
                return (T) new Long(1L);
            } else if (clazz.equals(Boolean.TYPE)) {
                return (T) Boolean.TRUE;
            } else if (clazz.equals(Integer.TYPE)) {
                return (T) new Integer(123);
            } else {
                throw new UnsupportedOperationException("Cannot create type: " + clazz);
            }
        } else if (isStubClass(clazz)) {
            return (T) createStubClass(clazz);
        } else {
            IMocksControl control = EasyMock.createControl();
            controls.add(control);
            return control.createMock(clazz);
        }
    }

    public <T> T createNiceMock(Class<T> clazz) {
        IMocksControl control = EasyMock.createNiceControl();
        controls.add(control);
        return control.createMock(clazz);
    }

    public <T> T createStrictMock(Class<T> clazz) {
        IMocksControl control = EasyMock.createStrictControl();
        controls.add(control);
        return control.createMock(clazz);
    }

    /**
     * Creates mocked implementations of the parameter type given.
     *
     * @param parameterTypes the types to create.
     * @param index the index to use in which to create a null Object - can be -1 and will not create any nulls.
     * @return an array of created types.
     */
    @SuppressWarnings({ "unchecked" })
    public Object[] createArgs(Class[] parameterTypes, int index) {
        Object[] objects = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i != index) {
                objects[i] = createMock(parameterTypes[i]);
            }
        }
        return objects;
    }

    public IMocksControl createControl() {
        IMocksControl control = EasyMock.createControl();
        controls.add(control);
        return control;
    }

    public IMocksControl createNiceControl() {
        IMocksControl niceControl = EasyMock.createNiceControl();
        controls.add(niceControl);
        return niceControl;
    }

    public IMocksControl createStrictControl() {
        IMocksControl strictControl = EasyMock.createStrictControl();
        controls.add(strictControl);
        return strictControl;
    }

    public void replay() {
        for (IMocksControl control : controls) {
            control.replay();
        }
    }

    public void verify() {
        for (IMocksControl control : controls) {
            control.verify();
        }
    }

    public void reset() {
        for (IMocksControl control : controls) {
            control.reset();
        }
    }

    private static boolean isStubClass(Class clazz) {
        return (clazz.equals(URL.class) || clazz.equals(URI.class) ||
            clazz.equals(String.class)) || clazz.equals(ATriple.class) ||
            clazz.equals(Set.class) || clazz.equals(Long.class);
    }

    private static Object createStubClass(Class clazz) {
        return INSTANTIATOR.instantiate(clazz);
    }
}
