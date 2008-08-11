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

package org.jrdf.query.expression;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.mem.AttributeImpl;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.util.EqualsUtil;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A expression expression comprising a single expression.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class SingleConstraint<V extends ExpressionVisitor> implements Constraint<V> {
    private static final long serialVersionUID = 4538228991602138679L;
    private static final int DUMMY_HASHCODE = 47;
    private LinkedHashMap<Attribute, ValueOperation> singleAvp;

    private SingleConstraint() {
    }

    public SingleConstraint(LinkedHashMap<Attribute, ValueOperation> singleAvp) {
        checkNotNull(singleAvp);
        this.singleAvp = singleAvp;
    }

    public void setAvo(Attribute existingAttribute, ValueOperation newValueOperation) {
        if (singleAvp.remove(existingAttribute) != null) {
            singleAvp.put(existingAttribute, newValueOperation);
        }
    }

    public LinkedHashMap<Attribute, ValueOperation> getAvo(Map<AttributeName, ? extends NodeType> allVariables) {
        LinkedHashMap<Attribute, ValueOperation> newAvps = new LinkedHashMap<Attribute, ValueOperation>();
        for (Attribute existingAttribute : singleAvp.keySet()) {
            Attribute newAttribute;
            if (allVariables != null) {
                newAttribute = createNewAttribute(existingAttribute, allVariables);
            } else {
                newAttribute = existingAttribute;
            }
            newAvps.put(newAttribute, new ValueOperationImpl(singleAvp.get(existingAttribute).getValue(), EQUALS));
        }
        return newAvps;
    }

    public void accept(V v) {
        v.visitConstraint(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (EqualsUtil.isNull(obj)) {
            return false;
        }
        if (EqualsUtil.sameReference(this, obj)) {
            return true;
        }
        if (EqualsUtil.differentClasses(this, obj)) {
            return false;
        }
        return determineEqualityFromFields(this, (SingleConstraint) obj);
    }

    @Override
    public int hashCode() {
        // FIXME TJA: Test drive out values of triple.hashCode()
        return DUMMY_HASHCODE;
    }

    /**
     * Delegates to <code>getAvp().toString()</code>.
     */
    @Override
    public String toString() {
        return singleAvp.toString();
    }

    private boolean determineEqualityFromFields(SingleConstraint o1, SingleConstraint o2) {
        return o1.singleAvp.equals(o2.singleAvp);
    }

    private Attribute createNewAttribute(Attribute existingAttribute,
        Map<AttributeName, ? extends NodeType> allVariables) {
        AttributeName existingAttributeName = existingAttribute.getAttributeName();
        NodeType newNodeType = allVariables.get(existingAttributeName);
        if (newNodeType == null) {
            newNodeType = existingAttribute.getType();
        }
        return new AttributeImpl(existingAttributeName, newNodeType);
    }
}