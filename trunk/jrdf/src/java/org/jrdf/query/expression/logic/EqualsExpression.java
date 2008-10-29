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

package org.jrdf.query.expression.logic;

import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.util.EqualsUtil;

import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class EqualsExpression<V extends ExpressionVisitor> implements LogicExpression<V> {
    private static final long serialVersionUID = 1297973700912646394L;
    private static final int DUMMY_HASHCODE = 47;

    private Map<Attribute, ValueOperation> lhs;
    private Map<Attribute, ValueOperation> rhs;

    private EqualsExpression() {
    }

    public EqualsExpression(Map<Attribute, ValueOperation> lhs, Map<Attribute, ValueOperation> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Map<Attribute, ValueOperation> getLhs() {
        return lhs;
    }

    public Map<Attribute, ValueOperation> getRhs() {
        return rhs;
    }

    public void accept(V v) {
        v.visitEqualsExpression(this);
    }

    public int size() {
        return 2;
    }

    public int hashCode() {
        // FIXME TJA: Test drive out values of triple.hashCode()
        int hash = DUMMY_HASHCODE + lhs.hashCode();
        return hash * DUMMY_HASHCODE + rhs.hashCode();
    }

    public String toString() {
        return lhs + " = " + rhs;
    }

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
        return determineEqualityFromFields(this, (EqualsExpression) obj);
    }

    private boolean determineEqualityFromFields(EqualsExpression o1, EqualsExpression o2) {
        return lhsEqual(o1, o2) && rhsEqual(o1, o2);
    }

    private boolean rhsEqual(EqualsExpression o1, EqualsExpression o2) {
        return o1.rhs.equals(o2.rhs);
    }

    private boolean lhsEqual(EqualsExpression o1, EqualsExpression o2) {
        return o1.lhs.equals(o2.lhs);
    }
}
