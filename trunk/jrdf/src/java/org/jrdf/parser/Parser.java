/*  Sesame - Storage and Querying architecture for RDF and RDF Schema
 *  Copyright (C) 2001-2004 Aduna
 *  Copyright (C) 2005 Andrew Newman - Conversion to JRDF, bugs fixed,
 *    modified blank node handling.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * The two parser methods for parsing RDF.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public interface Parser {

    /**
     * Parses the data from the supplied InputStream, using the supplied
     * BASE_URI to resolve any relative URI references.
     *
     * @param in      The InputStream from which to read the data.
     * @param baseURI The URI associated with the data in the InputStream.
     * @throws IOException               If an I/O error occurred while data was read
     *                                   from the InputStream.
     * @throws ParseException            If the parser has found an unrecoverable
     *                                   parse error.
     * @throws StatementHandlerException If the configured statement handler
     *                                   has encountered an unrecoverable error.
     */
    void parse(InputStream in, String baseURI) throws IOException, ParseException, StatementHandlerException;

    /**
     * Parses the data from the supplied Reader, using the supplied
     * BASE_URI to resolve any relative URI references.
     *
     * @param reader  The Reader from which to read the data.
     * @param baseURI The URI associated with the data in the InputStream.
     * @throws IOException               If an I/O error occurred while data was read
     *                                   from the InputStream.
     * @throws ParseException            If the parser has found an unrecoverable
     *                                   parse error.
     * @throws StatementHandlerException If the configured statement handler
     *                                   has encountered an unrecoverable error.
     */
    void parse(Reader reader, String baseURI) throws IOException, ParseException, StatementHandlerException;
}
