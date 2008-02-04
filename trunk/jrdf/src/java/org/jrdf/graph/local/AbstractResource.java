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

package org.jrdf.graph.local;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.mem.iterator.ObjectNodeIterator;
import org.jrdf.graph.local.mem.iterator.SubjectNodeIterator;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.net.URI;

public abstract class AbstractResource implements Resource, LocalizedNode {
    private static final long serialVersionUID = 3641740111800858628L;
    private Graph graph;
    private LocalizedNode node;
    private GraphElementFactory elementFactory;

    protected AbstractResource() {
    }

    public AbstractResource(Graph newGraph, LocalizedNode newNode) {
        checkNotNull(newGraph, newNode);
        this.graph = newGraph;
        this.elementFactory = graph.getElementFactory();
        this.node = newNode;
    }

    public Long getId() {
        return node.getId();
    }

    public void addValue(PredicateNode predicate, ObjectNode object) throws GraphException {
        graph.add((SubjectNode) node, predicate, object);
    }

    public void addValue(URI predicate, URI object) throws GraphException {
        addValue(elementFactory.createURIReference(predicate), elementFactory.createURIReference(object));
    }

    public void addValue(URI predicate, String lexicalValue) throws GraphException {
        addValue(elementFactory.createURIReference(predicate), elementFactory.createLiteral(lexicalValue));
    }

    public void addValue(URI predicate, Object object) throws GraphException {
        addValue(elementFactory.createURIReference(predicate), elementFactory.createLiteral(object));
    }

    public void addValue(URI predicate, String lexicalValue, String language) throws GraphException {
        addValue(elementFactory.createURIReference(predicate), elementFactory.createLiteral(lexicalValue, language));
    }

    public void addValue(URI predicate, String lexicalValue, URI dataType) throws GraphException {
        addValue(elementFactory.createURIReference(predicate), elementFactory.createLiteral(lexicalValue, dataType));
    }

    public void setValue(PredicateNode predicate, ObjectNode object) throws GraphException {
        removeValues(predicate);
        addValue(predicate, object);
    }

    public void setValue(URI predicate, URI object) throws GraphException {
        removeValues(predicate);
        addValue(predicate, object);
    }

    public void setValue(URI predicate, String lexicalValue) throws GraphException {
        removeValues(predicate);
        addValue(predicate, lexicalValue);
    }

    public void setValue(URI predicate, Object object) throws GraphException {
        removeValues(predicate);
        addValue(predicate, object);
    }

    public void setValue(URI predicate, String lexicalValue, String language) throws GraphException {
        removeValues(predicate);
        addValue(predicate, lexicalValue, language);
    }

    public void setValue(URI predicate, String lexicalValue, URI dataType) throws GraphException {
        removeValues(predicate);
        addValue(predicate, lexicalValue, dataType);
    }

    public void removeValue(PredicateNode predicate, ObjectNode object) throws GraphException {
        graph.remove((SubjectNode) node, predicate, object);
    }

    public void removeValue(URI predicate, URI object) throws GraphException {
        removeValue(elementFactory.createURIReference(predicate), elementFactory.createURIReference(object));
    }

    public void removeValue(URI predicate, String lexicalValue) throws GraphException {
        removeValue(elementFactory.createURIReference(predicate), elementFactory.createLiteral(lexicalValue));
    }

    public void removeValue(URI predicate, Object object) throws GraphException {
        removeValue(elementFactory.createURIReference(predicate), elementFactory.createLiteral(object));
    }

    public void removeValue(URI predicate, String lexicalValue, String language) throws GraphException {
        removeValue(elementFactory.createURIReference(predicate), elementFactory.createLiteral(lexicalValue, language));
    }

    public void removeValue(URI predicate, String lexicalValue, URI dataType) throws GraphException {
        removeValue(elementFactory.createURIReference(predicate), elementFactory.createLiteral(lexicalValue, dataType));
    }

    public void removeValues(PredicateNode predicate) throws GraphException {
        ClosableIterator<Triple> iterator = graph.find((SubjectNode) node, predicate, ANY_OBJECT_NODE);
        graph.remove(iterator);
    }

    public void removeValues(URI predicate) throws GraphException {
        removeValues(elementFactory.createURIReference(predicate));
    }

    public void removeSubject(SubjectNode subject, PredicateNode predicate) throws GraphException {
        while (graph.contains(subject, predicate, this)) {
            graph.remove(subject, predicate, this);
        }
    }

    public void removeSubject(URI subject, URI predicate) throws GraphException {
        removeSubject(elementFactory.createURIReference(subject), elementFactory.createURIReference(predicate));
    }

    public ClosableIterator<ObjectNode> getObjects(PredicateNode predicate) throws GraphException {
        ClosableIterator<Triple> closableIterator = graph.find((SubjectNode) node, predicate, ANY_OBJECT_NODE);
        return new ObjectNodeIterator(closableIterator);
    }

    public ClosableIterator<ObjectNode> getObjects(URI predicate) throws GraphException {
        return getObjects(elementFactory.createURIReference(predicate));
    }

    public ClosableIterator<SubjectNode> getSubjects(PredicateNode predicate) throws GraphException {
        ClosableIterator<Triple> closableIterator = graph.find(ANY_SUBJECT_NODE, predicate, (ObjectNode) node);
        return new SubjectNodeIterator(closableIterator);
    }

    public ClosableIterator<SubjectNode> getSubjects(URI predicate) throws GraphException {
        return getSubjects(elementFactory.createURIReference(predicate));
    }
}
