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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeValue implements DatatypeValue {
    private static final long serialVersionUID = 3135119000595034019L;
    private static final DatatypeFactory FACTORY;
    private XMLGregorianCalendar value;

    static {
        try {
            FACTORY = javax.xml.datatype.DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    protected DateTimeValue() {
    }

    /**
     * Creates a XML Gregorian calendar based on the current locale and time zone.
     *
     * @param value the Date to convert.
     */
    private DateTimeValue(final Date value) {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(value);
        this.value = FACTORY.newXMLGregorianCalendar(calendar);
    }

    private DateTimeValue(final String newValue) {
        this.value = FACTORY.newXMLGregorianCalendar(newValue);
    }

    public DatatypeValue create(final Object object) {
        return new DateTimeValue((Date) object);
    }

    public DatatypeValue create(final String lexicalForm) {
        return new DateTimeValue(lexicalForm);
    }

    public String getLexicalForm() {
        return value.toString();
    }

    public Object getValue() {
        return value;
    }

    public boolean isWellFormedXML() {
        return false;
    }

    public int compareTo(DatatypeValue val) {
        return value.compare(((DateTimeValue) val).value);
    }

    public int equivCompareTo(DatatypeValue val) {
        return compareTo(val);
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
        if (!hasSuperClassOrInterface(DateTimeValue.class, obj)) {
            return false;
        }
        return value.equals(((DateTimeValue) obj).value);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (value == null || value instanceof Serializable) {
            out.writeBoolean(true);
            out.defaultWriteObject();
        } else {
            out.writeBoolean(false);
            out.writeUTF(getLexicalForm());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        boolean isDefault = in.readBoolean();
        if (isDefault) {
            in.defaultReadObject();
        } else {
            String lexicalForm = in.readUTF();
            value = FACTORY.newXMLGregorianCalendar(lexicalForm);
        }
    }
}
