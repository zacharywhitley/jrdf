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

package org.jrdf.parser;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * An exception that can be thrown by a StatementHandler when it
 * encounters an application specific error that should cause the
 * parser to stop. If an exception is associated with the error then
 * this exception can be stored in a StatementHandlerException and
 * can later be retrieved from it when the StatementHandlerException
 * is catched, e.g.:
 * <pre>
 * try {
 *   parser.parse(myInputStream, myBaseURI);
 * }
 * catch (StatementHandlerException e) {
 *   Exception myException = e.getSource();
 *   ...
 * }
 * </pre>
 */
public class StatementHandlerException extends Exception {

    private static final long serialVersionUID = -6755497249260716209L;

    /**
     * The source of the exception, i.e. the application specific error.
     */
    private Exception source;

    private StatementHandlerException() {
    }

    /**
     * Creates a new StatementHandlerException.
     *
     * @param msg An error message.
     */
    public StatementHandlerException(String msg) {
        super(msg);
    }

    /**
     * Creates a new StatementHandlerException wrapping another exception.
     *
     * @param msg       An error message.
     * @param newSource The newSource exception.
     */
    public StatementHandlerException(String msg, Exception newSource) {
        super(msg);
        source = newSource;
    }

    /**
     * Creates a new StatementHandlerException wrapping another exception. The
     * StatementHandlerException will inherit its message from the supplied
     * newSource exception.
     *
     * @param newSource The newSource exception.
     */
    public StatementHandlerException(Exception newSource) {
        super(newSource.getMessage());
        source = newSource;
    }

    /**
     * Gets the source of this exception.
     *
     * @return The source of this exception.
     */
    public Exception getSource() {
        return source;
    }

    /**
     * Overrides <tt>Throwable.getCause()</tt> (JDK 1.4 or later).
     *
     * @return the source.
     */
    public Throwable getCause() {
        return source;
    }

    // overrides Trowable.printStackTrace()
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    // overrides Trowable.printStackTrace(PrintStream)
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);

        if (null != source) {
            ps.println("Source is:");
            source.printStackTrace(ps);
        }
    }

    // overrides Trowable.printStackTrace(PrintWriter)
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);

        if (null != source) {
            pw.println("Source is:");
            source.printStackTrace(pw);
        }
    }
}
