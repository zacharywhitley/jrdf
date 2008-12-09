/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph.local.index.operation.mem;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.operation.Comparison;
import org.jrdf.util.ClosableIterable;

/**
 * Default in memory Comparison.
 * <p/>
 * Currently, only implements grounded isomorphism.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class ComparisonImpl implements Comparison {

    public boolean isGrounded(Graph g) throws GraphException {
        if (!g.isEmpty()) {
            ClosableIterable<Triple> triples = null;
            try {
                triples = g.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
                for (Triple triple : triples) {
                    if (tripleContainsBlankNode(triple)) {
                        return false;
                    }
                }
            } finally {
                if (triples != null) {
                    triples.iterator().close();
                }
            }
        }
        return true;
    }

    public boolean areIsomorphic(Graph g1, Graph g2) throws GraphException {
        if (g1 == g2) {
            return true;
        }
        if (g1 == null || g2 == null) {
            return false;
        }
        return checkGraphs(g1, g2);

    }

    private boolean checkGraphs(Graph g1, Graph g2) throws GraphException {
        // check grounding
        boolean g1Grounded = isGrounded(g1);
        boolean g2Gounded = isGrounded(g2);
        // if only one is grounded, they cannot be isomorphic
        if (g1Grounded != g2Gounded) {
            return false;
        } else if (g1Grounded) {
            return groundedGraphsAreEqual(g1, g2);
        } else {
            // Add leanify to both graphs.
            // Decompose into MSGs NaiveGraphDecomposerImpl.
            // Compare both MSGs.
            throw new UnsupportedOperationException("Ungrounded Graph Isomorphism not implemented.");
        }
    }

    public boolean groundedGraphsAreEqual(Graph g1, Graph g2) throws GraphException {
        boolean g1IsEmpty = g1.isEmpty();
        boolean g2IsEmpty = g2.isEmpty();
        if (g1IsEmpty && g2IsEmpty) {
            return true;
        } else if (!g1IsEmpty && !g2IsEmpty) {
            return compareNonEmptyGraphs(g1, g2);
        }
        return false;
    }

    private boolean tripleContainsBlankNode(Triple triple) {
        return triple.getSubject() instanceof BlankNode || triple.getPredicate() instanceof BlankNode ||
            triple.getObject() instanceof BlankNode;
    }

    private boolean compareNonEmptyGraphs(Graph g1, Graph g2) throws GraphException {
        long g1Size = g1.getNumberOfTriples();
        long g2Size = g2.getNumberOfTriples();
        if (g1Size == g2Size) {
            return compareGraphContents(g1, g2);
        }
        return false;
    }

    private boolean compareGraphContents(Graph g1, Graph g2) throws GraphException {
        ClosableIterable<Triple> triples = g1.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        try {
            for (Triple triple : triples) {
                if (!g2.contains(triple)) {
                    return false;
                }
            }
            return true;
        } finally {
            triples.iterator().close();
        }
    }
}
