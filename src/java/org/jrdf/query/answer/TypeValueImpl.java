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

package org.jrdf.query.answer;

import static org.jrdf.query.answer.DatatypeType.DATATYPE;
import static org.jrdf.query.answer.DatatypeType.NONE;
import static org.jrdf.query.answer.DatatypeType.XML_LANG;
import static org.jrdf.query.answer.SparqlResultType.UNBOUND;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;

public class TypeValueImpl implements TypeValue {
    private static final int PRIME = 31;
    private SparqlResultType type;
    private String value;
    private String suffix;
    private DatatypeType suffixType;

    public TypeValueImpl() {
        setValues(UNBOUND, "", NONE, "");
    }

    public TypeValueImpl(SparqlResultType newType, String newValue) {
        setValues(newType, newValue, NONE, "");
    }

    // TODO AN Make this a enum for isDatatype instead of a boolean.
    public TypeValueImpl(final SparqlResultType newType, final String newValue, final boolean newIsDataType,
        final String newSuffix) {
        if (newIsDataType) {
            setValues(newType, newValue, DATATYPE, newSuffix);
        } else {
            setValues(newType, newValue, XML_LANG, newSuffix);
        }
    }

    private void setValues(final SparqlResultType newType, final String newValue, final DatatypeType newSuffixType,
        final String newSuffix) {
        this.type = newType;
        this.value = newValue;
        this.suffixType = newSuffixType;
        this.suffix = newSuffix;
    }

    public SparqlResultType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public DatatypeType getSuffixType() {
        return suffixType;
    }

    public String getSuffix() {
        return suffix;
    }

    public int hashCode() {
        int result;
        result = (type != null ? type.hashCode() : 0);
        result = PRIME * result + (value != null ? value.hashCode() : 0);
        result = PRIME * result + (suffix != null ? suffix.hashCode() : 0);
        result = PRIME * result + (suffixType != null ? suffixType.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        try {
            TypeValue typeValue = (TypeValue) obj;
            return compareTypeAndValue(typeValue) && compareSuffixAndSuffixType(typeValue);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    private boolean compareTypeAndValue(TypeValue typeValue) {
        return type.equals(typeValue.getType()) && value.equals(typeValue.getValue());
    }

    private boolean compareSuffixAndSuffixType(TypeValue typeValue) {
        // TODO AN Create null datatype.
        final DatatypeType suffixTypeToCompare = typeValue.getSuffixType();
        final String suffixToCompare = typeValue.getSuffix();
        return suffixTypeToCompare.equals(suffixType) && suffixToCompare.equals(suffix);
    }

    @Override
    public String toString() {
        return "Type: " + type + " Value: " + value + " Suffix Type: " + suffixType + " Suffix: " + suffix;
    }
}
