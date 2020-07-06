/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.util.ClosableIterable;
import static org.jrdf.util.ClosableIterators.with;
import org.jrdf.util.Function;

import java.util.Collection;

/**
 * Static class that contains utility methods for handling graphs and manipulating them.  Similar to Google's
 * iterators class.
 */
public final class Graphs {
    private Graphs() {
    }

    /**
     * Adds all the elements from the graph into the collection.
     *
     * @param addTo the collection to add it to.
     * @param graph the graph to use.
     * @return if it has been modified or not.
     */
    public static boolean addAll(java.util.Collection<Triple> addTo, Graph graph) {
        return add(addTo, graph, ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
    }

    /**
     * Adds any matching triples to the collection.
     *
     * @param addTo  the collection to add the results to.
     * @param graph  the graph to search.
     * @param triple the triple to use to search.
     * @return if any modification is made to the collection.
     */
    public static boolean add(java.util.Collection<Triple> addTo, Graph graph, Triple triple) {
        return add(addTo, graph, triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    /**
     * Adds any matching triples to the collection.
     *
     * @param addTo         the collection to add the results to.
     * @param graph         the graph to search.
     * @param subjectNode   the subject node to find.
     * @param predicateNode the predicate node to find.
     * @param objectNode    the object node to find.
     * @return if any modification is made to the collection.
     */
    public static boolean add(java.util.Collection<Triple> addTo, Graph graph, SubjectNode subjectNode,
        PredicateNode predicateNode, ObjectNode objectNode) {
        return with(graph.find(subjectNode, predicateNode, objectNode), new Add<Triple>(addTo));
    }

    private static final class Add<T> implements Function<Boolean, ClosableIterable<T>> {
        private final Collection<T> addTo;

        private Add(final java.util.Collection<T> newAddTo) {
            addTo = newAddTo;
        }

        public Boolean apply(ClosableIterable<T> from) {
            boolean modified = from.iterator().hasNext();
            for (T individual : from) {
                addTo.add(individual);
            }
            return modified;
        }
    }
}
