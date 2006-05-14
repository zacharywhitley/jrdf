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
package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.type.BlankNodeType;
import org.jrdf.query.relation.type.LiteralType;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.query.relation.type.Type;
import org.jrdf.query.relation.type.URIReferenceType;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeEnum;

/**
 * Stuff goes in here.
 *
 * @author Andrew Newman
 * @version $Id: ClosableIterator.java 436 2005-12-19 13:19:55Z newmana $
 */
public final class AttributeComparatorImpl implements AttributeComparator {
    private NodeTypeComparator nodeTypeComparator;

    public AttributeComparatorImpl(NodeTypeComparator nodeTypeComparator) {
        this.nodeTypeComparator = nodeTypeComparator;
    }

    public int compare(Attribute attribute, Attribute attribute1) {
        int result;

        if (attribute == null || attribute1 == null) {
            throw new NullPointerException();
        }

        // TODO (AN) Cannot compare different types - conversions should happen at a layer above.
        if (!isComparableTypes(attribute.getType(), attribute1.getType())) {
            throw new ClassCastException("Cannot compare: " + attribute.getType() + " with: " + attribute1.getType());
        }

        // TODO (AN) Test drive me!
        if (attribute.equals(attribute1)) {
            return 0;
        }

        result = compareAttributeNames(attribute.getAttributeName(), attribute1.getAttributeName());

        if (result == 0) {
            result = compareAttributeLiterals(attribute.getAttributeName(), attribute1.getAttributeName());
        }

        if (result == 0) {
            if (isPositionNodeType(attribute.getType())) {
                result = comparePositionalNodeTypes(attribute, attribute1);
            } else {
                result = compareNodeTypes(attribute, attribute1);
            }
        }

        return result;
    }

    private int comparePositionalNodeTypes(Attribute attribute, Attribute attribute1) {
        if (attribute.getType() instanceof SubjectNodeType) {
            return -1;
        } else if (attribute.getType() instanceof PredicateNodeType) {
            if (attribute1.getType() instanceof ObjectNodeType) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    private int compareNodeTypes(Attribute attribute, Attribute attribute1) {
        int result;
        NodeTypeEnum nodeType1Enum = getNodeType(attribute.getType().getClass());
        NodeTypeEnum nodeType2Enum = getNodeType(attribute1.getType().getClass());
        result = nodeTypeComparator.compare(nodeType1Enum, nodeType2Enum);
        return result;
    }

    private boolean isComparableTypes(Type type, Type type1) {
        boolean result = false;
        if (type.equals(type1)) {
            result = true;
        }
        if ((isPositionNodeType(type) && isPositionNodeType(type1)) ||
                (!isPositionNodeType(type) && !isPositionNodeType(type1))) {
            result = true;
        }
        return result;
    }

    private boolean isPositionNodeType(Type type) {
        return type instanceof SubjectNodeType || type instanceof PredicateNodeType || type instanceof ObjectNodeType;
    }

    private int compareAttributeLiterals(AttributeName attributeName, AttributeName attributeName1) {
        String attLit1 = attributeName.getLiteral();
        String attLit2 = attributeName1.getLiteral();
        int result = attLit1.compareTo(attLit2);
        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        }
        return result;
    }

    private int compareAttributeNames(AttributeName attribute, AttributeName attribute1) {
        boolean attIsVariable = attributeIsVariableName(attribute);
        boolean att2IsVariable = attributeIsVariableName(attribute1);
        if (!attIsVariable && att2IsVariable) {
            return 1;
        } else if (isSameNameType(attIsVariable, att2IsVariable)) {
            return 0;
        } else {
            return -1;
        }
    }

    private NodeTypeEnum getNodeType(Class<? extends Type> nodeClass) {
        if (BlankNodeType.class.isAssignableFrom(nodeClass)) {
            return NodeTypeEnum.BLANK_NODE;
        } else if (URIReferenceType.class.isAssignableFrom(nodeClass)) {
            return NodeTypeEnum.URI_REFERENCE;
        } else if (LiteralType.class.isAssignableFrom(nodeClass)) {
            return NodeTypeEnum.LITERAL;
        } else {
            throw new IllegalArgumentException("Illegal node: " + nodeClass);
        }
    }

    private boolean isSameNameType(boolean attIsVariable, boolean att2IsVariable) {
        return attIsVariable && att2IsVariable || !attIsVariable && !att2IsVariable;
    }

    private boolean attributeIsVariableName(AttributeName attribute) {
        return attribute instanceof VariableName;
    }

}
