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

package org.jrdf.graph.datatype;

import static org.jrdf.util.EqualsUtil.hasSuperClassOrInterface;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NonPositiveIntegerValue implements XSDDecimal {
    private static final long serialVersionUID = 21516359310292990L;
    private BigInteger value;
    private boolean isNegative;
    private boolean isZero;
    private boolean isNegativeZero;
    private boolean isPositiveZero;
    private static final String POSITIVE_ZERO = "+0";
    private static final String NEGATIVE_ZERO = "-0";

    protected NonPositiveIntegerValue() {
    }

    public NonPositiveIntegerValue(final BigInteger newValue) {
        this.value = newValue;
    }

    private NonPositiveIntegerValue(final String newValue) {
        this.isNegative = newValue.startsWith("-");
        this.isZero = newValue.matches("[+-]?0");
        if (isZero) {
            this.isPositiveZero = POSITIVE_ZERO.equals(newValue);
            this.isNegativeZero = NEGATIVE_ZERO.equals(newValue);
            this.value = new BigInteger("0");
        } else if (isNegative) {
            this.value = new BigInteger(newValue);
        } else {
            throw new NumberFormatException();
        }
    }

    public DatatypeValue create(final Object object) {
        return new NonPositiveIntegerValue((BigInteger) object);
    }

    public DatatypeValue create(final String lexicalForm) {
        return new NonPositiveIntegerValue(lexicalForm);
    }

    public String getLexicalForm() {
        final StringBuilder newValue = new StringBuilder(value.toString());
        if (isZero) {
            if (isPositiveZero) {
                newValue.insert(0, "+");
            } else if (isNegativeZero) {
                newValue.insert(0, "-");
            }
        }
        return newValue.toString();
    }

    public Object getValue() {
        return value;
    }

    public boolean isWellFormedXML() {
        return false;
    }

    public BigDecimal getAsBigDecimal() {
        return new BigDecimal(value);
    }

    public int compareTo(final DatatypeValue val) {
        return value.compareTo(((NonPositiveIntegerValue) val).value);
    }

    public int equivCompareTo(final DatatypeValue val) {
        return getAsBigDecimal().compareTo(((XSDDecimal) val).getAsBigDecimal());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        if (!hasSuperClassOrInterface(NonPositiveIntegerValue.class, obj)) {
            return false;
        }
        return value.equals(((NonPositiveIntegerValue) obj).value);
    }
}
