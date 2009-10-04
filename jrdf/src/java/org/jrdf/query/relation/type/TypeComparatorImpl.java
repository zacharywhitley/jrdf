/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.query.relation.type;

import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeEnum;

/**
 * Compares only the type of attributes.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public final class TypeComparatorImpl implements TypeComparator {
    private static final long serialVersionUID = -534102244020655491L;
    private static final int EQUAL = 0;
    private static final int AFTER = 1;
    private static final int BEFORE = -1;
    private NodeTypeComparator nodeTypeComparator;

    private TypeComparatorImpl() {
    }

    public TypeComparatorImpl(NodeTypeComparator newNodeTypeComparator) {
        this.nodeTypeComparator = newNodeTypeComparator;
    }

    public int compare(NodeType type1, NodeType type2) {
        ifNullThrowException(type1, type2);
        int result = EQUAL;
        if (!type1.equals(type2)) {
            if (!bothPositionalOrNode(type1, type2)) {
                result = compareDifferentCategoryOfTypes(type1, type2);
            } else {
                result = compareSameCategoryOfTypes(type1, type2);
            }
        }
        return result;
    }

    private boolean bothPositionalOrNode(NodeType type1, NodeType type2) {
        return (isPositionNodeType(type1) && isPositionNodeType(type2)) ||
            (!isPositionNodeType(type1) && !isPositionNodeType(type2));
    }

    private int compareDifferentCategoryOfTypes(NodeType type1, NodeType type2) {
        if (isPositionNodeType(type1) && !isPositionNodeType(type2)) {
            return AFTER;
        }
        return BEFORE;
    }

    private int compareSameCategoryOfTypes(NodeType type1, NodeType type2) {
        int result;
        if (isPositionNodeType(type1)) {
            result = comparePositionalNodeTypes(type1, type2);
        } else {
            result = compareNodeTypes(type1, type2);
        }
        return result;
    }

    private boolean isPositionNodeType(NodeType type) {
        return type instanceof SubjectNodeType || type instanceof PredicateNodeType || type instanceof ObjectNodeType;
    }

    private int comparePositionalNodeTypes(NodeType type1, NodeType type2) {
        int result;
        if (type1 instanceof SubjectNodeType) {
            result = BEFORE;
        } else if (type1 instanceof PredicateNodeType) {
            if (type2 instanceof ObjectNodeType) {
                result = BEFORE;
            } else {
                result = AFTER;
            }
        } else {
            result = AFTER;
        }
        return result;
    }

    private int compareNodeTypes(NodeType type, NodeType type2) {
        int result;
        NodeTypeEnum nodeType1Enum = NodeTypeEnum.getNodeType(type.getClass());
        NodeTypeEnum nodeType2Enum = NodeTypeEnum.getNodeType(type2.getClass());
        result = nodeTypeComparator.compare(nodeType1Enum, nodeType2Enum);
        return result;
    }

    private void ifNullThrowException(NodeType nodeType1, NodeType nodeType2) {
        if (nodeType1 == null || nodeType2 == null) {
            throw new NullPointerException();
        }
    }
}
