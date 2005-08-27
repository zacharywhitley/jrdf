/*  Sesame - Storage and Querying architecture for RDF and RDF Schema
 *  Copyright (C) 2001-2004 Aduna
 *
 *  Contact:
 *  Aduna
 *  Prinses Julianaplein 14 b
 *  3817 CS Amersfoort
 *  The Netherlands
 *  tel. +33 (0)33 465 99 87
 *  fax. +33 (0)33 465 99 87
 *
 *  http://aduna.biz/
 *  http://www.openrdf.org/
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.jrdf.parser;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A parse exception that can be thrown by a parser when it encounters
 * an error from which it cannot or doesn't want to recover.
 **/
public class ParseException extends Exception {

    private static final long serialVersionUID = -1049335626292093759L;

    private int lineNo;

    private int columnNo;

    private Exception source;

    /**
     * Creates a new ParseException.
     *
     * @param msg An error message.
     * @param lineNo A line number associated with the message.
     * @param columnNo A column number associated with the message.
     **/
    public ParseException(String msg, int lineNo, int columnNo) {
        super(msg);
        this.lineNo = lineNo;
        this.columnNo = columnNo;
    }

    /**
     * Creates a new ParseException wrapping another exception.
     *
     * @param msg An error message.
     * @param source The source exception.
     * @param lineNo A line number associated with the message.
     * @param columnNo A column number associated with the message.
     **/
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
     * @param source The source exception.
     * @param lineNo A line number associated with the message.
     * @param columnNo A column number associated with the message.
     **/
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
     * @return A line number, or -1 if no line number is available
     * or applicable.
     **/
    public int getLineNumber() {
        return lineNo;
    }

    /**
     * Gets the column number associated with this parse exception.
     * @return A column number, or -1 if no column number is available
     * or applicable.
     **/
    public int getColumnNumber() {
        return columnNo;
    }
}
