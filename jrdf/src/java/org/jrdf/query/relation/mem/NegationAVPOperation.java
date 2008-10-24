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

package org.jrdf.query.relation.mem;

import static org.jrdf.graph.AnyNode.ANY_NODE;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.constants.NullaryNode.NULLARY_NODE;

import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public final class NegationAVPOperation implements AVPOperation {
    private static final NodeComparator COMPARATOR = new ComparatorFactoryImpl().createNodeComparator();
    private static final long serialVersionUID = 6132466908097938381L;

    /**
     * The constant to indicate negation operation.
     */
    public static final NegationAVPOperation NEGATION = new NegationAVPOperation();

    private NegationAVPOperation() {
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return "!";
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public boolean addAttributeValuePair(Attribute attribute, Map<Attribute, ValueOperation> newAttributeValues,
                                         ValueOperation lhs, ValueOperation rhs) {
        final AVPOperation operation1 = lhs.getOperation();
        final AVPOperation operation2 = rhs.getOperation();
        boolean toNegate = NegationAVPOperation.class.isAssignableFrom(operation1.getClass());
        toNegate = toNegate && NegationAVPOperation.class.isAssignableFrom(operation2.getClass()) ? false : true;
        final Node node1 = lhs.getValue();
        final Node node2 = rhs.getValue();

        boolean result = compareNodes(node1, node2);
        if (toNegate) {
            result = !result;
        }
        // TODO add to value result pair
        addAttributeValues(attribute, newAttributeValues, lhs, rhs, result);
        return result;
    }

    private void addAttributeValues(Attribute attribute, Map<Attribute, ValueOperation> newAttributeValues,
                                    ValueOperation lhs, ValueOperation rhs, boolean result) {
        if (!result) {
            if (NegationAVPOperation.class.isAssignableFrom(lhs.getOperation().getClass())) {
                newAttributeValues.put(attribute, rhs);
            } else {
                newAttributeValues.put(attribute, lhs);
            }
        }
    }

    private boolean compareNodes(Node node1, Node node2) {
        boolean result;
        if (NULLARY_NODE.equals(node1) || NULLARY_NODE.equals(node2)) {
            result = false;
        } else if (ANY_NODE.equals(node1) || ANY_NODE.equals(node2)) {
            result = true;
        } else {
            result = COMPARATOR.compare(node1, node2) == 0;
        }
        return result;
    }
}
