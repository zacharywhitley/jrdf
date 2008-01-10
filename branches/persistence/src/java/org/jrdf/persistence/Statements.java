/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

package org.jrdf.persistence;

import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.vocabulary.XSD;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Bednar
 */
class Statements {

    static String createSelectStatement(URI uri) {
        return "select @P, Y from {X}^@P{Y} where X = &" + uri;
    }

    static String createDeleteStatement(URI uri) {
        return "delete Resource(&" + uri + ")";
    }

    static List<String> createInsertOrUpdateStatements(Object entity,
        EntityManagerImpl context,
        boolean insert)
        throws PersistenceException {

        ClassMapping mappings = RDFIntrospector.getMappings(entity);

        URI uri = mappings.getURI(entity);
        List<String> updates = new LinkedList<String>();

        if (insert) {
            insertTypeStatements(uri, mappings, context);
            List<Triple> triples = mappings.toTriples(entity, context);

            StringBuilder stmt = new StringBuilder();

            for (Triple triple : triples) {
                if (triple.getSubject() == null || triple.getObject() == null) {
                    continue;
                }

                if (stmt.length() == 0) {
                    stmt.append("insert ");
                } else {
                    stmt.append(',');
                }

                stmt.append(getLocalName(((URIReference) triple.getPredicate()).getURI()));
                stmt.append("(&");
                stmt.append(triple.getSubject());
                stmt.append(',');
                stmt.append(encodeObject(triple.getObject()));
                stmt.append(')');
            }

            if (stmt.length() != 0) {
                updates.add(stmt.toString());
            }

        } else {
            List<Triple> triples = mappings.toTriples(entity, context);

            for (PropertyTag property : getUpdatedProperties(uri, triples)) {
                String stmt = "delete ";
                String pname = getLocalName(property.uri);

                stmt += pname + "(X,Y) from {X}^" + pname;
                stmt += property.inverse ?
                    "{Y} where Y = &" + uri :
                    "{Y} where X = &" + uri;

                updates.add(stmt);
            }

            StringBuilder stmt = new StringBuilder();

            for (Triple triple : triples) {
                if (triple.getSubject() == null || triple.getObject() == null) {
                    continue;
                }

                if (stmt.length() == 0) {
                    stmt.append("insert ");
                } else {
                    stmt.append(',');
                }

                stmt.append(getLocalName(((URIReference) triple.getPredicate()).getURI()));
                stmt.append("(&");
                stmt.append(triple.getSubject());
                stmt.append(',');
                stmt.append(encodeObject(triple.getObject()));
                stmt.append(')');
            }

            if (stmt.length() != 0) {
                updates.add(stmt.toString());
            }
        }

        return updates;
    }

    static String createInversePropertyStatement(URI object, URI property) {
        return "select X from {X}" + getLocalName(property) +
            "{Y} where Y = &" + object;
    }

    private static void insertTypeStatements(URI uri, ClassMapping mappings, EntityManagerImpl manager)
        throws PersistenceException {
        StringBuilder stmt = new StringBuilder();

        for (URI type : mappings.typeURIs) {
            if (stmt.length() == 0) {
                stmt.append("insert ");
            } else {
                stmt.append(',');
            }

            stmt.append(getLocalName(type));
            stmt.append("(&");
            stmt.append(uri);
            stmt.append(')');
        }

        if (stmt.length() != 0) {
            manager.executeUpdate(stmt.toString());
        }
    }

    private static Set<PropertyTag> getUpdatedProperties(URI uri,
        List<Triple> triples) {
        Set<PropertyTag> properties = new LinkedHashSet<PropertyTag>();
        for (Triple triple : triples) {
            properties.add(new PropertyTag(uri, triple));
        }
        return properties;
    }

    private static class PropertyTag {
        boolean inverse;
        private URI uri;

        PropertyTag(URI uri, Triple triple) {
            URIReference subject = (URIReference) triple.getSubject();
            inverse = subject == null || (!uri.equals(subject.getURI()));
            this.uri = ((URIReference) triple.getPredicate()).getURI();
        }

        public int hashCode() {
            return uri.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PropertyTag)) {
                return false;
            }
            PropertyTag t = (PropertyTag) obj;
            return uri.equals(t.uri) && inverse == t.inverse;
        }

    }

    private static String getLocalName(URI uri) {
        String str = uri.toString();
        int i = str.lastIndexOf("#");
        return str.substring(i + 1, str.length());
    }

    private static String encodeObject(ObjectNode object) {
        if (!(object instanceof Literal)) {
            return "&" + ((URIReference) object).getURI();
        }

        Literal l = (Literal) object;

        String str = l.getLexicalForm();
        if (l.getDatatypeURI().equals(XSD.STRING)) {
            str = '"' + str + '"';
        }

        return str;
    }

}
