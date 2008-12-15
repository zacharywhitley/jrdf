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

package org.jrdf.parser.rdfxml;

import org.jrdf.collection.MapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.parser.ConfigurableParser;
import org.jrdf.parser.GraphStatementHandler;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.Parser;
import static org.jrdf.parser.ParserConfiguration.DT_IGNORE;
import org.jrdf.parser.StatementHandlerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

// TODO (AN) Can this be made more generic by parsing in a configurable parser instead?

/**
 * An RDF/XML parser that adds every triple encountered to a JRDF Graph.  Uses the default parser configuration.
 *
 * @author Andrew Newman
 * @version $Revision: 544 $
 */
public class GraphRdfXmlParser implements Parser {
    private final MapFactory mapFactory;
    private ConfigurableParser parser;

    public GraphRdfXmlParser(final Graph graph, final MapFactory newMapFactory) {
        mapFactory = newMapFactory;
        parser = new RdfXmlParser(graph.getElementFactory(), mapFactory);
        parser.setStatementHandler(new GraphStatementHandler(graph));
        parser.setParseStandAloneDocuments(true);
        parser.setVerifyData(true);
        parser.setDatatypeHandling(DT_IGNORE);
    }

    public void parse(InputStream in, String baseURI) throws IOException, ParseException, StatementHandlerException {
        parser.parse(in, baseURI);
    }

    public void parse(Reader reader, String baseURI) throws IOException, ParseException, StatementHandlerException {
        parser.parse(reader, baseURI);
    }
}
