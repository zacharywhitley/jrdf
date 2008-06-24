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

package org.jrdf.util.param;

/**
 * Utility for checking parameters to methods.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class ParameterUtil {
    /**
     * Create a null checker.
     */
    private static final ParameterChecker NULL_CHECKER = new NullChecker();

    /**
     * Create a empty string checker.
     */
    private static final ParameterChecker EMPTY_STRING_CHECKER = new EmtpyStringChecker();

    /**
     * This is a collection of static classes - cannot construct.
     */
    private ParameterUtil() {
    }

    /**
     * Checks if <var>param</var> is <code>null</code> and throws an exception if it is.
     *
     * @param name  The name of the parameter to check.
     * @param param The parameter to check.
     * @throws IllegalArgumentException If <var>param</var> is <code>null</code>.
     */
    public static void checkNotNull(String name, Object param) throws IllegalArgumentException {
        if (!NULL_CHECKER.paramAllowed(param)) {
            throw new IllegalArgumentException(name + " parameter cannot be null");
        }
    }

    /**
     * Checks if <var>param</var> is <code>null</code> or the empty string and throws an exception if it is.
     *
     * @param name  The name of the parameter to check.
     * @param param The parameter to check.
     * @throws IllegalArgumentException If <var>param</var> is <code>null</code> or the empty string.
     */
    public static void checkNotEmptyString(String name, String param) throws IllegalArgumentException {
        checkNotNull(name, param);
        if (!EMPTY_STRING_CHECKER.paramAllowed(param)) {
            throw new IllegalArgumentException(name + " parameter cannot be the empty string");
        }
    }
}