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
package org.jrdf.query.relation.mem;

import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import static org.jrdf.util.EqualsUtil.hasSuperClassOrInterface;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;

import java.util.SortedSet;

/**
 * Implementation of an attribute name/value consists of the name (SUBJECT, PREDICATE, etc.) and value.
 *
 * @author Andrew Newman
 * @version $Id$
 */

// TODO (AN) Add a check that the attribute type is consistent with the node type - or add it into the Factory.

public final class AttributeValuePairImpl implements AttributeValuePair {
    private static final long serialVersionUID = -5045948869879997736L;
    private Attribute attribute;
    private Node value;
    private Operation operation;

    // For serialization.
    private AttributeValuePairImpl() {
    }

    public AttributeValuePairImpl(Attribute newAttribute, Node newValue) {
        this (newAttribute, newValue, new EqOperation());
    }

    public AttributeValuePairImpl(Attribute newAttribute, Node newValue, Operation newOperation) {
        attribute = newAttribute;
        value = newValue;
        operation = newOperation;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Node getValue() {
        return value;
    }

    public Operation getOperation() {
        return operation;
    }

    public boolean addAttributeValuePair(AttributeValuePairComparator avpComparator,
            SortedSet<AttributeValuePair> newAttributeValues, AttributeValuePair avp) {
        return operation.addAttributeValuePair(avpComparator, newAttributeValues, this, avp);
    }

    @Override
    public int hashCode() {
        return attribute.hashCode() ^ value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        if (hasSuperClassOrInterface(AttributeValuePair.class, obj)) {
            return determineEqualityFromFields((AttributeValuePair) obj);
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + attribute + ", " + value + "}";
    }

    private boolean determineEqualityFromFields(AttributeValuePair attributeValuePair) {
        if (attributeValuePair.getAttribute().equals(getAttribute())) {
            if (attributeValuePair.getValue().equals(getValue())) {
                return true;
            }
        }
        return false;
    }
}
