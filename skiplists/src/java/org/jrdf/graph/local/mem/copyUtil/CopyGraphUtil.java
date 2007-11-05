/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.local.mem.copyUtil;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.Triple;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.Node;
import org.jrdf.graph.Literal;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.util.ClosableIterator;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Oct 31, 2007
 * Time: 2:16:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyGraphUtil {
    private HashMap<Integer, BlankNode> newBNodeMap;

    private Graph sourceGraph;
    private Graph targetGraph;
    private GraphElementFactory elemFactory;
    private TripleFactory tripleFactory;

    public CopyGraphUtil(Graph sg, Graph tg) {
        newBNodeMap = new HashMap<Integer, BlankNode>();

        sourceGraph = sg;
        targetGraph = tg;
        elemFactory = tg.getElementFactory();
        tripleFactory = tg.getTripleFactory();
    }

    // a triple with blank nodes is only added once to the hash map
    public Graph copyBNodes() throws GraphException, GraphElementFactoryException {
        ClosableIterator<Triple> triples = null;
        try {
            triples = sourceGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);

            readSourceGraph(triples);
            createNewTriples();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            triples.close();
        }
        return targetGraph;
    }

    private void readSourceGraph(ClosableIterator<Triple> triples) throws GraphElementFactoryException, GraphException {
        Triple triple;
        while (triples.hasNext()) {
            triple = triples.next();
            if (triple.isGrounded()) {
                addTripleToGraph(triple);
                //sourceGraph.remove(triple);
            } else {
                updateBlankNodes(triple);
            }
        }
    }

    private void updateBlankNodes(Triple triple) throws GraphElementFactoryException {
        if (isBlankNode(triple.getSubject())) {
            int bnHash = triple.getSubject().hashCode();
            newBNodeMap.put(bnHash, elemFactory.createBlankNode());
        }
        if (isBlankNode(triple.getObject())) {
            int bnHash = triple.getObject().hashCode();
            newBNodeMap.put(bnHash, elemFactory.createBlankNode());
        }
    }

    private Graph createNewTriples() throws GraphException, GraphElementFactoryException {
        Triple newTriple;
        ClosableIterator<Triple> it = null;
        try {
            it = sourceGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
            while (it.hasNext()) {
                Triple triple = it.next();
                if (!triple.isGrounded()) {
                    newTriple = createNewTriple(triple);
                    targetGraph.add(newTriple);
                }
            }
        } finally {
            it.close();
        }
        return targetGraph;
    }

    private Triple createNewTriple(Triple triple) throws GraphElementFactoryException {
        SubjectNode newsn;
        PredicateNode newpn;
        ObjectNode newon;
        Triple newTriple;
        SubjectNode sn = triple.getSubject();
        PredicateNode pn = triple.getPredicate();
        ObjectNode on = triple.getObject();

        newsn = (SubjectNode) createNewNode(sn);
        newpn = elemFactory.createURIReference(((URIReference) pn).getURI());
        newon = (ObjectNode) createNewNode(on);

        newTriple = tripleFactory.createTriple(newsn, newpn, newon);
        return newTriple;
    }

    private Node createNewNode(Node node) throws GraphElementFactoryException {
        Node newNode;
        if (CopyGraphUtil.isBlankNode(node)) {
            newNode = newBNodeMap.get(new Integer(node.hashCode()));
        } else {
            newNode = createLiteralOrURI(node);
            //newNode = elemFactory.createURIReference(((URIReference) node).getURI());
        }
        return newNode;
    }

    private void addTripleToGraph(Triple triple) throws GraphElementFactoryException, GraphException {
        SubjectNode sN = (SubjectNode)
                elemFactory.createURIReference(((URIReference) triple.getSubject()).getURI());
        PredicateNode pN = (PredicateNode)
                elemFactory.createURIReference(((URIReference) triple.getPredicate()).getURI());

        ObjectNode oON = triple.getObject();
        ObjectNode oN = createLiteralOrURI(oON);
        targetGraph.add(tripleFactory.createTriple(sN, pN, oN));
    }

    private ObjectNode createLiteralOrURI(Node oON) throws GraphElementFactoryException {
        ObjectNode oN;

        if (Literal.class.isAssignableFrom(oON.getClass())) {
            Literal lit = (Literal) oON;
            if (lit.isDatatypedLiteral()) {
                oN = elemFactory.createLiteral(lit.getValue().toString(), lit.getDatatypeURI());
            } else {
                oN = elemFactory.createLiteral(lit.getValue().toString());
            }
        } else {
            oN = elemFactory.createURIReference(((URIReference) oON).getURI());
        }
        return oN;
    }

    public static boolean isBlankNode(Node node) {
        return BlankNode.class.isAssignableFrom(node.getClass());
    }
}