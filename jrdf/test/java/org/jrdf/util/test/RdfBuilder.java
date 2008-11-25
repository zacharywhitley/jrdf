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

package org.jrdf.util.test;

import groovy.util.BuilderSupport;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.parser.NamespaceListener;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.mem.MemNamespaceListener;
import org.jrdf.parser.n3.parser.NamespaceAwareNodeParsersFactory;
import org.jrdf.parser.n3.parser.NamespaceAwareNodeParsersFactoryImpl;
import org.jrdf.parser.n3.parser.SingleRdfNodeParser;
import org.jrdf.parser.ntriples.parser.ObjectParser;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RdfBuilder extends BuilderSupport {
    private static final Pattern REGEX = Pattern.compile(
        "(\\<([\\x20-\\x7E]+?)\\>||((\\p{Alpha}[\\x20-\\x7E]*?):(\\p{Alpha}[\\x20-\\x7E]*?))|" +
        "_:(\\p{Alpha}[\\x20-\\x7E]*?)|(([\\x20-\\x7E]+?)))");
    private final Graph graph;
    private final NamespaceListener listener;
    private final RegexMatcherFactory matcherFactory;
    private final ObjectParser namespaceAwareObjectParser;
    private SubjectNode subject;
    private PredicateNode predicate;
    private ObjectNode object;

    public RdfBuilder(final Graph newGraph) {
        graph = newGraph;
        listener = new MemNamespaceListener();
        matcherFactory = new RegexMatcherFactoryImpl();
        final NamespaceAwareNodeParsersFactory parsersFactory = new NamespaceAwareNodeParsersFactoryImpl(
            newGraph, new MemMapFactory(), matcherFactory, listener);
        namespaceAwareObjectParser = new SingleRdfNodeParser(parsersFactory.getURIReferenceParser(),
            parsersFactory.getBlankNodeParser(), parsersFactory.getLiteralParser());
    }

    public void namespace(String prefix, String uri) {
        listener.handleNamespace(prefix, uri);
    }

    protected void setParent(Object name, Object value) {
    }

    protected void nodeCompleted(Object name, Object value) {
        if (value != null && subject != null && parseNode(value.toString()).equals(subject)) {
            subject = null;
        }
    }

    protected Object createNode(Object name) {
        final Node node = parseNode(name.toString());
        if (subject == null) {
            subject = (SubjectNode) node;
        } else if (predicate == null) {
            predicate = (PredicateNode) node;
        } else {
            object = (ObjectNode) node;
            addTriple();
            clearObject();
        }
        return name;
    }

    protected Object createNode(Object name, Object value) {
        if (value == null) {
            return createNode(name);
        } else {
            final Node node1 = parseNode(name.toString());
            final Node node2 = parseNode(value.toString());
            if (subject == null) {
                subject = (SubjectNode) node1;
                predicate = (PredicateNode) node2;
            } else if (predicate == null) {
                predicate = (PredicateNode) node1;
                object = (ObjectNode) node2;
                addTriple();
                clearPredicateObject();
            }
            return name;
        }
    }

    protected Object createNode(Object name, Map attributes) {
        return createNode(name, attributes, null);
    }

    protected Object createNode(Object name, Map attributes, Object value) {
        if (subject == null || value != null) {
            for (Object obj : attributes.entrySet()) {
                final Map.Entry entry = (Map.Entry) obj;
                subject = (SubjectNode) parseNode(name.toString());
                predicate = (PredicateNode) parseNode(entry.getKey().toString());
                addObjectValues(entry);
            }
        } else {
            throw new IllegalStateException("Cannot create triple at this level");
        }
        return name;
    }

    private void addObjectValues(Map.Entry entry) {
        final Object objValue = entry.getValue();
        if (!List.class.isAssignableFrom(objValue.getClass())) {
            addSingleObject(objValue);
        } else {
            addMultipleObjects(objValue);
        }
    }

    private void addSingleObject(Object objValue) {
        object = (ObjectNode) parseNode(objValue.toString());
        addTriple();
        clearTriple();
    }

    private void addMultipleObjects(Object objValue) {
        for (Object o : (List) objValue) {
            object = (ObjectNode) parseNode(o.toString());
            addTriple();
        }
        clearTriple();
    }

    private Node parseNode(String string) {
        try {
            final RegexMatcher matcher = matcherFactory.createMatcher(REGEX, string);
            if (matcher.matches()) {
                return namespaceAwareObjectParser.parseObject(matcher);
            } else {
                throw new IllegalArgumentException("Couldn't match string: " + string);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTriple() {
        try {
            graph.add(subject, predicate, object);
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearTriple() {
        subject = null;
        clearPredicateObject();
    }

    private void clearPredicateObject() {
        predicate = null;
        clearObject();
    }

    private void clearObject() {
        object = null;
    }
}
