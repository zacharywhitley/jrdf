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

package org.jrdf.query.expression.logic;

import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.BiOperandExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.util.EqualsUtil;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * @author Yuan-Fang Li
 * @version $Id :$
 */
public class LogicOrExpression<V extends ExpressionVisitor> implements LogicExpression<V>, BiOperandExpression<V> {
    private static final long serialVersionUID = 6637326993178600535L;
    private static final int DUMMY_HASHCODE = 47;

    private LogicExpression<V> lhs;
    private LogicExpression<V> rhs;
    protected static final String OR_STRING = "||";

    private LogicOrExpression() {
    }

    public LogicOrExpression(LogicExpression<V> lhs, LogicExpression<V> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Map<Attribute, ValueOperation> getAVO() {
        Map<Attribute, ValueOperation> map = new LinkedHashMap<Attribute, ValueOperation>();
        map.putAll(lhs.getAVO());
        map.putAll(rhs.getAVO());
        return map;
    }

    public LogicExpression<V> getLhs() {
        return lhs;
    }

    public LogicExpression<V> getRhs() {
        return rhs;
    }

    public void setLhs(LogicExpression<V> lhs) {
        this.lhs = lhs;
    }

    public void setRhs(LogicExpression<V> rhs) {
        this.rhs = rhs;
    }

    public int size() {
        return (lhs.size() + rhs.size()) / 2 + 1;
    }

    public void accept(V v) {
        v.visitLogicOr(this);
    }

    public int hashCode() {
        int hash = DUMMY_HASHCODE + lhs.hashCode();
        hash = hash * DUMMY_HASHCODE + rhs.hashCode();
        return hash * DUMMY_HASHCODE + OR_STRING.hashCode();
    }

    public String toString() {
        return "( " + lhs + " " + OR_STRING + " " + rhs + " )";
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
        return determineEqualityFromFields(this, (LogicOrExpression) obj);
    }

    private boolean determineEqualityFromFields(LogicOrExpression o1, LogicOrExpression o2) {
        return lhsEqual(o1, o2) && rhsEqual(o1, o2);
    }

    private boolean rhsEqual(LogicOrExpression o1, LogicOrExpression o2) {
        return o1.getRhs().equals(o2.getRhs());
    }

    private boolean lhsEqual(LogicOrExpression o1, LogicOrExpression o2) {
        return o1.getLhs().equals(o2.getLhs());
    }

}
