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

import groovy.lang.Closure;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.codehaus.groovy.runtime.InvokerInvocationException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Given a block/closure expects that the code will throw the expected exception with the expected message.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class AssertThrows {

    private AssertThrows() {
    }

    public static void assertThrows(Class exceptionClass, String message, final Closure closure) {
        assertThrows(exceptionClass, message, new Block() {
            public void execute() throws Throwable {
                try {
                    closure.call();
                } catch (InvokerInvocationException e) {
                    throw e.getCause();
                }
            }
        });
    }

    public static void assertThrows(Class exceptionClass, String message, Block block) {
        try {
            block.execute();
            Assert.fail("Failed to throw exception: " + exceptionClass + " with message: " + message);
        } catch (AssertionFailedError e) {
            throw e;
        } catch (Throwable t) {
            checkExceptionClass(exceptionClass, t);
            Assert.assertEquals(message, t.getMessage());
        }
    }

    public static void assertThrows(Class exceptionClass, final Closure closure) {
        assertThrows(exceptionClass, new Block() {
            public void execute() throws Throwable {
                try {
                    closure.call();
                } catch (InvokerInvocationException e) {
                    throw e.getCause();
                }
            }
        });
    }

    public static void assertThrows(Class exceptionClass, Block block) {
        try {
            block.execute();
            Assert.fail("Failed to throw any exception: " + exceptionClass);
        } catch (AssertionFailedError e) {
            throw e;
        } catch (Throwable t) {
            checkExceptionClass(exceptionClass, t);
        }
    }

    private static void checkExceptionClass(Class exceptionClass, Throwable t) {
        StringWriter writer = new StringWriter();
        PrintWriter s = new PrintWriter(writer);
        t.printStackTrace(s);
        Assert.assertEquals("Got exception: " + writer.toString(), exceptionClass, t.getClass());
    }

    public interface Block {
        void execute() throws Throwable;
    }
}