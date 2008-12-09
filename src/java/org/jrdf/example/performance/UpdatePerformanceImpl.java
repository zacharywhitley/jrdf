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

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.util.ClosableIterable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UpdatePerformanceImpl implements UpdatePerformance {
    private final int nodesToUpdate;
    private final String subjectPrefix;
    private int noUpdates;

    public UpdatePerformanceImpl(int nodesToUpdate, String subjectPrefix) {
        this.nodesToUpdate = nodesToUpdate;
        this.subjectPrefix = subjectPrefix;
    }

    public void updatePerformance(Graph graph, GraphPerformance performance) throws Exception {
        long startTime = System.currentTimeMillis();
        for (int index = 0; index < nodesToUpdate; index++) {
            URI subjectURI = URI.create(subjectPrefix + index);
            List<Triple> triplesToChange = addTriplesToArray(graph, subjectURI);
            for (Triple triple : triplesToChange) {
                URI subject = ((URIReference) triple.getSubject()).getURI();
                URIReference newSubject = graph.getElementFactory().createURIReference(
                    URI.create(subject.toString() + "hello"));
                graph.add(newSubject, triple.getPredicate(), triple.getObject());
                graph.remove(triple);
                noUpdates++;
            }
        }
        performance.outputResult(graph, startTime, "Testing Update Performance: " + noUpdates);
    }

    private List<Triple> addTriplesToArray(Graph graph, URI subjectURI) throws Exception {
        URIReference newSubject = graph.getElementFactory().createURIReference(subjectURI);
        ClosableIterable<Triple> triples = findAllPredicates(graph, newSubject);
        List<Triple> triplesToChange = new ArrayList<Triple>();
        try {
            for (Triple triple : triples) {
                triplesToChange.add(triple);
            }
        } finally {
            triples.iterator().close();
        }
        return triplesToChange;
    }

    private ClosableIterable<Triple> findAllPredicates(Graph graph, SubjectNode subject) throws GraphException {
        return graph.find(subject, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
    }
}
