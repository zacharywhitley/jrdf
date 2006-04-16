/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
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
package org.jrdf.graph.mem;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.URIReference;

/**
 * Stuff goes in here.
 *
 * @author Andrew Newman
 * @version $Id: ClosableIterator.java 436 2005-12-19 13:19:55Z newmana $
 */
public final class NodeComparatorImpl implements NodeComparator {

    public int compare(Object o1, Object o2) {
        checkIsNode(o1);
        checkIsNode(o2);

        NodeType nodeType1 = getNodeType(o1.getClass());
        NodeType nodeType2 = getNodeType(o2.getClass());

        if (nodesDifferentType(nodeType1, nodeType2)) {
            return compareDifferentNodeTypes(nodeType1, nodeType2);
        } else {
            if (o1 == o2) {
                return 0;
            } else if (nodeType1.equals(NodeType.BLANK_NODE)) {
                return compareBlankNodes((BlankNode) o1, (BlankNode) o2);
            } else if (nodeType1.equals(NodeType.URI_REFERENCE)) {
                return compareByString(o1.toString(), o2.toString());
            } else if (nodeType1.equals(NodeType.LITERAL)) {
                return compareByString(o1.toString(), o2.toString());
            }
            throw new IllegalArgumentException("Could not compare: " + o1.getClass() + " and " + o2.getClass());
        }
    }

    // TODO (AN) Move to different class - NodeTypeComparator
    private int compareDifferentNodeTypes(NodeType nodeType1, NodeType nodeType2) {
        int result = 0;
        if (nodeType1.isBlankNode()) {
            result = -1;
        } else if (nodeType1.isURIReferenceNode() && nodeType2.isLiteralNode()) {
            result = -1;
        } else if (nodeType1.isURIReferenceNode() && nodeType2.isBlankNode()) {
            result = 1;
        } else if (nodeType1.isLiteralNode()) {
            result = 1;
        }
        return result;
    }

    // TODO (AN) Move to different class - BNodeComparator
    private int compareBlankNodes(BlankNode blankNode1, BlankNode blankNode2) {
        int result;

        if ((blankNode1 instanceof MemNode) && (blankNode2 instanceof MemNode)) {
            result = compareByMemNode((MemNode) blankNode1, (MemNode) blankNode2);
        } else {
            result = compareByString(blankNode1.toString(), blankNode2.toString());
        }
        return result;
    }

    // TODO (AN) Move to different class - BNodeComparator
    private int compareByMemNode(MemNode memNode1, MemNode memNode2) {
        int result = 0;
        if (memNode1.getId() > memNode2.getId()) {
            result = 1;
        } else if (memNode1.getId() < memNode2.getId()) {
            result = -1;
        }
        return result;
    }

    // TODO (AN) Move to different class - StringComparator
    private int compareByString(String str1, String str2) {
        int result = 0;
        if (str1.compareTo(str2) > 0) {
            result = 1;
        } else if (str1.compareTo(str2) < 0) {
            result = -1;
        }
        return result;
    }

    // TODO (AN) Move to different class.
    private boolean nodesDifferentType(NodeType nodeType1, NodeType nodeType2) {
        return !nodeType1.equals(nodeType2);
    }

    // TODO (AN) Move to different class.
    private NodeType getNodeType(Class nodeClass) {
        if (BlankNode.class.isAssignableFrom(nodeClass)) {
            return NodeType.BLANK_NODE;
        } else if (URIReference.class.isAssignableFrom(nodeClass)) {
            return NodeType.URI_REFERENCE;
        } else if (Literal.class.isAssignableFrom(nodeClass)) {
            return NodeType.LITERAL;
        } else {
            throw new IllegalArgumentException("Illegal node: " + nodeClass);
        }
    }

    private void checkIsNode(Object o) {
        if (!(Node.class.isAssignableFrom(o.getClass()))) {
            throw new ClassCastException(o.getClass() + " is not a JRDF Node.");
        }
    }
}
