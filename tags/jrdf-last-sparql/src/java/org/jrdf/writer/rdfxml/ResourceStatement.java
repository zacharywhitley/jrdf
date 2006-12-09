/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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
 */
package org.jrdf.writer.rdfxml;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfNamespaceMap;
import org.jrdf.writer.WriteException;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Represents a statement about a resource.
 *
 * @author TurnerRX
 */
public class ResourceStatement implements RdfXmlWritable {

    private static final String URI_LIT = "\t<${pred}${lang}${type}>${lit}</${pred}>";
    private static final String URI_URI = "\t<${pred} rdf:resource=\"${resource}\"/>";
    private static final String URI_BLANK = "\t<${pred} rdf:nodeID=\"${blank}\"/>";
    private static final String LANGUAGE = " xml:lang=\"${language}\"";
    private static final String DATATYPE = " rdf:datatype=\"${datatype}\"";

    private Triple triple;
    private RdfNamespaceMap names;
    private BlankNodeRegistry registry;

    public ResourceStatement(RdfNamespaceMap names, BlankNodeRegistry registry) {
        if (names == null) {
            throw new IllegalArgumentException("RdfNamespaceMap is null.");
        }
        if (registry == null) {
            throw new IllegalArgumentException("BlankNodeRegistryImpl is null.");
        }
        this.names = names;
        this.registry = registry;
    }

    public Triple getTriple() {
        return triple;
    }

    public void setTriple(Triple triple) {
        this.triple = triple;
    }

    public void write(PrintWriter writer) throws IOException, WriteException {
        PredicateNode predicate = triple.getPredicate();
        ObjectNode object = triple.getObject();
        write(predicate, object, writer);
    }

    private void write(PredicateNode predicate, ObjectNode object, PrintWriter writer) throws WriteException {
        if (!(predicate instanceof URIReference)) {
            throw new WriteException("Unknown predicate node type: " + predicate.getClass().getName());
        }
        if (object instanceof URIReference) {
            write((URIReference) predicate, (URIReference) object, writer);
        } else if (object instanceof Literal) {
            write((URIReference) predicate, (Literal) object, writer);
        } else if (object instanceof BlankNode) {
            write((URIReference) predicate, (BlankNode) object, writer);
        } else {
            throw new WriteException("Unknown object node type: " + object.getClass().getName());
        }
    }

    private void write(URIReference predicate, URIReference object, PrintWriter writer) throws WriteException {
        String statement = URI_URI;
        // replace predicate
        String uri = names.replaceNamespace(predicate);
        statement = statement.replaceAll("\\$\\{pred\\}", uri);
        // replace resource
        String resource = object.getURI().toString();
        statement = statement.replaceAll("\\$\\{resource\\}", resource);
        //output
        writer.println(statement);
    }

    private void write(URIReference predicate, Literal object, PrintWriter writer) throws WriteException {
        String statement = URI_LIT;
        // replace predicate
        String uri = names.replaceNamespace(predicate);
        statement = statement.replaceAll("\\$\\{pred\\}", uri);
        // replace literal
        String literal = object.getEscapedLexicalForm();
        statement = statement.replaceAll("\\$\\{lit\\}", literal);
        // replace any language or datatype
        String lang = "";
        String type = "";
        // one or the other - not both
        if (object.getDatatypeURI() != null) {
            type = DATATYPE;
            type = type.replaceAll("\\$\\{datatype\\}", object.getDatatypeURI().toString());
        } else if (object.getLanguage() != null && !"".equals(object.getLanguage())) {
            lang = LANGUAGE;
            lang = lang.replaceAll("\\$\\{language\\}", object.getLanguage());
        }
        statement = statement.replaceAll("\\$\\{lang\\}", lang);
        statement = statement.replaceAll("\\$\\{type\\}", type);
        // output
        writer.println(statement);
    }

    private void write(URIReference predicate, BlankNode object, PrintWriter writer) throws WriteException {
        String statement = URI_BLANK;
        // replace predicate
        String uri = names.replaceNamespace(predicate);
        statement = statement.replaceAll("\\$\\{pred\\}", uri);
        // replace resource
        String nodeId = registry.getNodeId(object);
        statement = statement.replaceAll("\\$\\{blank\\}", nodeId);
        //output
        writer.println(statement);
    }
}
