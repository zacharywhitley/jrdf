/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
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
 *
 */
package org.jrdf.graph.local;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Resource;
import org.jrdf.graph.TypedNodeVisitor;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.datatype.SemanticLiteralComparator;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeEnum;

/**
 * Currently only support simple comparison - either by node id for blank nodes or string comparisons for URIs and
 * Literals.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class NodeComparatorImpl implements NodeComparator, TypedNodeVisitor {
    private static final long serialVersionUID = 1941872400257968398L;
    private NodeTypeComparator nodeTypeComparator;
    private BlankNodeComparator blankNodeComparator;
    private SemanticLiteralComparator literalComparator;
    private NodeTypeEnum currentEnum;

    private NodeComparatorImpl() {
    }

    public NodeComparatorImpl(NodeTypeComparator nodeTypeComparator, SemanticLiteralComparator literalComparator,
        BlankNodeComparator blankNodeComparator) {
        this.nodeTypeComparator = nodeTypeComparator;
        this.literalComparator = literalComparator;
        this.blankNodeComparator = blankNodeComparator;
    }

    public int compare(Node o1, Node o2) {
        int result = 0;
        if (!o1.equals(o2)) {
            NodeTypeEnum nodeType1Enum = getNodeType(o1);
            NodeTypeEnum nodeType2Enum = getNodeType(o2);
            if (areNodesDifferentType(nodeType1Enum, nodeType2Enum)) {
                result = nodeTypeComparator.compare(nodeType1Enum, nodeType2Enum);
            } else {
                result = compareSameNodeType(o1, o2, nodeType1Enum);
            }
        }
        return result;
    }

    private int compareSameNodeType(Node n1, Node n2, NodeTypeEnum nodeTypeEnum) {
        int result;
        if (n1 == n2) {
            result = 0;
        } else {
            result = compareSameNodeTypes(n1, n2, nodeTypeEnum);
        }
        return result;
    }

    private int compareSameNodeTypes(Node n1, Node n2, NodeTypeEnum nodeTypeEnum) {
        int result;
        if (nodeTypeEnum.isBlankNode()) {
            result = blankNodeComparator.compare((BlankNode) n1, (BlankNode) n2);
        } else if (nodeTypeEnum.isURIReferenceNode()) {
            result = compareURIReference((URIReference) n1, (URIReference) n2);
        } else if (nodeTypeEnum.isLiteralNode()) {
            result = compareLiteral((Literal) n1, (Literal) n2);
        } else {
            throw new IllegalArgumentException("Could not compare: " + n1.getClass() + " and " + n2.getClass());
        }
        return result;
    }

    // TODO (AN) Move to different class.
    private boolean areNodesDifferentType(NodeTypeEnum nodeType1Enum, NodeTypeEnum nodeType2Enum) {
        return !nodeType1Enum.equals(nodeType2Enum);
    }

    private int compareURIReference(URIReference n1, URIReference n2) {
        int result = 0;
        if (!n1.equals(n2)) {
            result = compareByString(n1.toString(), n2.toString());
        }
        return result;
    }

    private int compareLiteral(Literal n1, Literal n2) {
        try {
            return literalComparator.compare(n1, n2);
        } catch (ClassCastException cce) {
            // Assume different data types.
            return -1;
        }
    }

    private int compareByString(String str1, String str2) {
        int result = str1.compareTo(str2);
        if (result > 0) {
            result = 1;
        } else if (result < 0) {
            result = -1;
        }
        return result;
    }

    private NodeTypeEnum getNodeType(Node node) {
        node.accept(this);
        return currentEnum;
    }

    public void visitBlankNode(BlankNode blankNode) {
        currentEnum = NodeTypeEnum.BLANK_NODE;
    }

    public void visitURIReference(URIReference uriReference) {
        currentEnum = NodeTypeEnum.URI_REFERENCE;
    }

    public void visitLiteral(Literal literal) {
        currentEnum = NodeTypeEnum.LITERAL;
    }

    public void visitNode(Node node) {
        throw new IllegalArgumentException("Illegal node: " + node.getClass());
    }

    public void visitResource(Resource resource) {
        throw new IllegalArgumentException("Illegal node: " + resource.getClass());
    }
}
