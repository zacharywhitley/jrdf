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
 * A parse exception that can be thrown by a parser when it encounters
 * an error from which it cannot or doesn't want to recover.
 */
public class ParseException extends Exception {

    private static final long serialVersionUID = -1049335626292093759L;

    private int lineNo;

    private int columnNo;

    private Exception source;

    private ParseException() {
    }

    /**
     * Creates a new ParseException.
     *
     * @param msg      An error message.
     * @param columnNo A column number associated with the message.
     */
    public ParseException(String msg, int columnNo) {
        super(msg);
        this.columnNo = columnNo;
    }

    /**
     * Creates a new ParseException.
     *
     * @param msg      An error message.
     * @param lineNo   A line number associated with the message.
     * @param columnNo A column number associated with the message.
     */
    public ParseException(String msg, int lineNo, int columnNo) {
        super(msg);
        this.lineNo = lineNo;
        this.columnNo = columnNo;
    }

    /**
     * Creates a new ParseException wrapping another exception.
     *
     * @param msg      An error message.
     * @param source   The source exception.
     * @param lineNo   A line number associated with the message.
     * @param columnNo A column number associated with the message.
     */
    public ParseException(String msg, Exception source, int lineNo, int columnNo) {
        super(msg);
        this.source = source;
        this.lineNo = lineNo;
        this.columnNo = columnNo;
    }

    /**
     * Creates a new ParseException wrapping another exception. The
     * ParseException will inherit its message from the supplied
     * source exception.
     *
     * @param source   The source exception.
     * @param lineNo   A line number associated with the message.
     * @param columnNo A column number associated with the message.
     */
    public ParseException(Exception source, int lineNo, int columnNo) {
        super(source.getMessage());
        this.source = source;
        this.lineNo = lineNo;
        this.columnNo = columnNo;
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);

        if (null != source) {
            ps.println("Source is:");
            source.printStackTrace(ps);
        }
    }

    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);

        if (null != source) {
            pw.println("Source is:");
            source.printStackTrace(pw);
        }
    }

    /**
     * Gets the line number associated with this parse exception.
     *
     * @return A line number, or -1 if no line number is available
     *         or applicable.
     */
    public int getLineNumber() {
        return lineNo;
    }

    /**
     * Gets the column number associated with this parse exception.
     *
     * @return A column number, or -1 if no column number is available
     *         or applicable.
     */
    public int getColumnNumber() {
        return columnNo;
    }
}
