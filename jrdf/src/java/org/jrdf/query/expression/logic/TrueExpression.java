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

import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.constants.NullaryAttribute.NULLARY_ATTRIBUTE;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.util.EqualsUtil;
import static org.jrdf.vocabulary.XSD.BOOLEAN;

import static java.util.Collections.singletonMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */
public final class TrueExpression<V extends ExpressionVisitor> implements LogicExpression<V> {
    private static final long serialVersionUID = -6113444233155098483L;
    private static final int DUMMY_HASHCODE = 47;
    private static final Map<Attribute, ValueOperation> MAP =
        singletonMap(NULLARY_ATTRIBUTE,
            (ValueOperation) new ValueOperationImpl(new LiteralImpl("true", BOOLEAN), EQUALS));

    /**
     * The singleton true expression.
     */
    public static final TrueExpression<ExpressionVisitor> TRUE_EXPRESSION = new TrueExpression<ExpressionVisitor>();

    private Map<Attribute, ValueOperation> avp;

    private TrueExpression() {
        this.avp = MAP;
    }

    public void accept(V v) {
        v.visitTrue(this);
    }

    public int size() {
        return 0;
    }

    public Map<Attribute, ValueOperation> getAVO() {
        return avp;
    }

    @Override
    public int hashCode() {
        return DUMMY_HASHCODE + avp.hashCode();
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
        return determineEqualityFromFields(this, (TrueExpression) obj);
    }

    private boolean determineEqualityFromFields(TrueExpression s1, TrueExpression s2) {
        return s1.avp.equals(s2.avp);
    }


    @Override
    public String toString() {
        return "TRUE";
    }
}
