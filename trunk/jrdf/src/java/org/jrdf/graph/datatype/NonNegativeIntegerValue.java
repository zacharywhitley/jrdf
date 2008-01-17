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

package org.jrdf.graph.datatype;

import static org.jrdf.util.EqualsUtil.hasSuperClassOrInterface;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NonNegativeIntegerValue implements XSDDecimal {
    private static final long serialVersionUID = 4550600026432326665L;
    private BigInteger value;
    private boolean isPositive;
    private boolean isNegativeZero;

    protected NonNegativeIntegerValue() {
    }

    public NonNegativeIntegerValue(final BigInteger newValue) {
        this.value = newValue;
    }

    private NonNegativeIntegerValue(final String newValue) {
        this.isPositive = newValue.startsWith("+");
        this.isNegativeZero = "-0".equals(newValue);
        if (isPositive) {
            this.value = new BigInteger(newValue.substring(1));
        } else if (newValue.startsWith("-")) {
            if (isNegativeZero) {
                this.value = new BigInteger("0");
            } else {
                throw new NumberFormatException();
            }
        } else {
            this.value = new BigInteger(newValue);
        }
    }

    public DatatypeValue create(Object object) {
        return new NonNegativeIntegerValue((BigInteger) object);
    }

    public DatatypeValue create(String lexicalForm) {
        return new NonNegativeIntegerValue(lexicalForm);
    }

    public String getLexicalForm() {
        if (isPositive) {
            return "+" + value.toString();
        } else if (isNegativeZero) {
            return "-" + value.toString();
        } else {
            return value.toString();
        }
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

    public int compareTo(DatatypeValue val) {
        return value.compareTo(((NonNegativeIntegerValue) val).value);
    }

    public int equivCompareTo(DatatypeValue value) {
        return getAsBigDecimal().compareTo(((XSDDecimal) value).getAsBigDecimal());
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
        if (!hasSuperClassOrInterface(NonNegativeIntegerValue.class, obj)) {
            return false;
        }
        return value.equals(((NonNegativeIntegerValue) obj).value);
    }
}
