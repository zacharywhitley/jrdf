/*  Sesame - Storage and Querying architecture for RDF and RDF Schema
 *  Copyright (C) 2001-2004 Aduna
 *  Copyright (C) 2005 Andrew Newman - Conversion to JRDF.
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

/**
 * A listener interface for listening to the parser's progress.
 */
public interface ParseLocationListener {

    /**
     * Signals an update of a parser's progress, indicated by a line
     * and column number. Both line and column number start with value 1
     * for the first line or column.
     *
     * @param lineNo   The line number, or -1 if none is available.
     * @param columnNo The column number, or -1 if none is available.
     */
    void parseLocationUpdate(int lineNo, int columnNo);
}