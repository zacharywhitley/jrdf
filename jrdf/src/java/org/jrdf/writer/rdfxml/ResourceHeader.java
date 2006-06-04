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
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.URIReference;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.WriteException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

/**
 * Represents an RDF/XML header for a given resource.
 *
 * @author TurnerRX
 */
public class ResourceHeader extends RdfXmlWritable {
    private static final String RESOURCE_HEADER = "<rdf:Description rdf:about=\"${resource}\">";
    private static final String BLANK_NODE_HEADER = "<rdf:Description rdf:nodeID=\"${nodeId}\">";
    private SubjectNode subject;
    private BlankNodeRegistry registry;

    public ResourceHeader(SubjectNode subject, BlankNodeRegistry registry) {
        if (subject == null) {
            throw new IllegalArgumentException("SubjectNode is null.");
        }
        if (registry == null) {
            throw new IllegalArgumentException("BlankNodeRegistry is null.");
        }
        this.subject = subject;
        this.registry = registry;
    }

    @Override
    public void write(PrintWriter writer) throws IOException, WriteException {
        if (subject instanceof URIReference) {
            write((URIReference) subject, writer);
        } else if (subject instanceof BlankNode) {
            write((BlankNode) subject, writer);
        } else {
            throw new WriteException("Unknown SubjectNode type: " + subject.getClass().getName());
        }
    }

    private void write(URIReference resource, PrintWriter writer) {
        String header = RESOURCE_HEADER;
        String node = getUri(resource);
        header = header.replaceAll("\\$\\{resource\\}", node);
        writer.println(header);
    }

    private void write(BlankNode node, PrintWriter writer) {
        String header = BLANK_NODE_HEADER;
        String nodeId = this.registry.getNodeId(node);
        header = header.replaceAll("\\$\\{nodeId\\}", nodeId);
        writer.println(header);
    }

    private String getUri(URIReference resource) {
        URI uri = resource.getURI();
        return uri.toString();
    }

}
