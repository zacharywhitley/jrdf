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

import java.net.URI;

/**
 * An XML attribute.
 */
class Att {

    private String namespace;
    private String localName;
    private String qName;
    private String value;
    private URI uri;

    Att(String newNamespace, String newLocalName, String newQName, String newValue) {
        this.namespace = newNamespace;
        this.localName = newLocalName;
        this.qName = newQName;
        this.value = newValue;
        this.uri = URI.create(newNamespace + newLocalName);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getLocalName() {
        return localName;
    }

    public URI getURI() {
        return uri;
    }

    public String getQName() {
        return qName;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "Namespace: " + namespace + ", locaName: " + localName + ", qName: " + qName + ", value: " + value;
    }
}
