/*  Sesame - Storage and Querying architecture for RDF and RDF Schema
 *  Copyright (C) 2001-2004 Aduna
 *
 *  Contact:
 *  	Aduna
 *  	Prinses Julianaplein 14 b
 *  	3817 CS Amersfoort
 *  	The Netherlands
 *  	tel. +33 (0)33 465 99 87
 *  	fax. +33 (0)33 465 99 87
 *
 *  	http://aduna.biz/
 *  	http://www.openrdf.org/
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

import java.io.*;

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
 **/
public class StatementHandlerException extends Exception {

  /**
   * The source of the exception, i.e. the application specific
   * error.
   **/
  private Exception _source = null;

  /**
   * Creates a new StatementHandlerException.
   *
   * @param msg An error message.
   **/
  public StatementHandlerException(String msg) {
    super(msg);
  }

  /**
   * Creates a new StatementHandlerException wrapping another exception.
   *
   * @param msg An error message.
   * @param source The source exception.
   **/
  public StatementHandlerException(String msg, Exception source) {
    super(msg);
    _source = source;
  }

  /**
   * Creates a new StatementHandlerException wrapping another exception. The
   * StatementHandlerException will inherit its message from the supplied
   * source exception.
   *
   * @param source The source exception.
   **/
  public StatementHandlerException(Exception source) {
    super(source.getMessage());
    _source = source;
  }

  /**
   * Gets the source of this exception.
   *
   * @return The source of this exception.
   **/
  public Exception getSource() {
    return _source;
  }

  /**
   * Overrides <tt>Throwable.getCause()</tt> (JDK 1.4 or later).
   **/
  public Throwable getCause() {
    return _source;
  }

  // overrides Trowable.printStackTrace()
  public void printStackTrace() {
    printStackTrace(System.err);
  }

  // overrides Trowable.printStackTrace(PrintStream)
  public void printStackTrace(PrintStream ps) {
    super.printStackTrace(ps);

    if (_source != null) {
      ps.println("Source is:");
      _source.printStackTrace(ps);
    }
  }

  // overrides Trowable.printStackTrace(PrintWriter)
  public void printStackTrace(PrintWriter pw) {
    super.printStackTrace(pw);

    if (_source != null) {
      pw.println("Source is:");
      _source.printStackTrace(pw);
    }
  }
}
