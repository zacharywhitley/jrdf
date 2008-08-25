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

package org.jrdf.example.performance;

import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterator;

import java.net.URI;

public class FindPerformanceImpl implements FindPerformance {
    private final int nodesToFind;
    private final String subjectPrefix;
    private final String predicatePrefix;
    private final String objectPrefix;
    private int noFinds;

    public FindPerformanceImpl(int nodesToFind, String subjectPrefix, String predicatePrefix,
        String objectPrefix) {
        this.nodesToFind = nodesToFind;
        this.subjectPrefix = subjectPrefix;
        this.predicatePrefix = predicatePrefix;
        this.objectPrefix = objectPrefix;
    }

    public void findPerformance(Graph graph, GraphPerformance performance) throws Exception {
        long startTime = System.currentTimeMillis();
        for (int index = 0; index < nodesToFind; index++) {
            URI subjectURI = URI.create(subjectPrefix + index);
            find1(graph, subjectURI);
        }
        performance.outputResult(graph, startTime, "Testing Find Performance: " + noFinds);
    }

    private void find1(Graph graph, URI subjectURI) throws GraphException {
        URIReference predicate = graph.getElementFactory().createURIReference(subjectURI);
        ClosableIterator<Triple> itr = findAllPredicates(graph, predicate);
        try {
            while (itr.hasNext()) {
                Triple triple = itr.next();
                ObjectNode object = triple.getObject();
                find2(graph, object);
            }
        } finally {
            itr.close();
        }
    }

    private void find2(Graph graph, ObjectNode object1) throws GraphException {
        ClosableIterator<Triple> itr2 = findAllPredicates(graph, (SubjectNode) object1);
        try {
            while (itr2.hasNext()) {
                Triple triple2 = itr2.next();
                ObjectNode object2 = triple2.getObject();
                if (!(object2 instanceof SubjectNode)) {
                    continue;
                }
                find3(graph, object2);
            }
        } finally {
            itr2.close();
        }
    }

    private void find3(Graph graph, ObjectNode object2) throws GraphException {
        ClosableIterator<Triple> itr3 = findAllPredicates(graph, (SubjectNode) object2);
        try {
            while (itr3.hasNext()) {
                Triple triple3 = itr3.next();
                ObjectNode object3 = triple3.getObject();
                if (!(object3 instanceof SubjectNode)) {
                    continue;
                }
            }
        } finally {
            itr3.close();
        }
    }

    private ClosableIterator<Triple> findAllPredicates(Graph graph, SubjectNode subject) throws GraphException {
        noFinds++;
        return graph.find(subject, AnyPredicateNode.ANY_PREDICATE_NODE, AnyObjectNode.ANY_OBJECT_NODE);
    }
}