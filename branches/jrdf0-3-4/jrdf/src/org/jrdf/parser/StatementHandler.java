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

import org.jrdf.graph.*;

/**
 * An interface defining methods for receiving RDF statements from an RDF
 * parser.
 **/
public interface StatementHandler {

  /**
   * Called by an RDF parser when it has parsed a statement. The type of the
   * subject, predicate and object is determined by the ValueFactory that
   * the parser uses.
   *
   * @param subject A URI or bNode.
   * @param predicate A URI.
   * @param object A URI, bNode or literal.
   * @exception StatementHandlerException If the statement handler has
   * encountered an unrecoverable error.
   **/
  void handleStatement(SubjectNode subject, PredicateNode predicate,
      ObjectNode object) throws StatementHandlerException;
}
