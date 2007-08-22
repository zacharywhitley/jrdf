/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

import static org.jrdf.graph.NullURI.NULL_URI;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.jrdf.vocabulary.XSD;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DatatypeFactoryImpl implements DatatypeFactory {
    private static final DatatypeFactory FACTORY_INSTANCE = new DatatypeFactoryImpl();
    private final Map<URI, ValueCreator> factoryMap = new HashMap<URI, ValueCreator>();
    private final Map<Class<?>, ValueCreator> classToCreator = new HashMap<Class<?>, ValueCreator>();
    private final Map<Class<?>, URI> classToURI = new HashMap<Class<?>, URI>();

    private DatatypeFactoryImpl() {
        // Primitive types
        final CalendarValue calendarValue = new CalendarValue();
        final StringValue stringValue = new StringValue();
        final DateTimeValue dateTimeValue = new DateTimeValue();
        addValueCreator(NULL_URI, stringValue);
        addValueCreator(String.class, XSD.STRING, stringValue);
        addValueCreator(Boolean.class, XSD.BOOLEAN, new BooleanValue());
        addValueCreator(BigDecimal.class, XSD.DECIMAL, new DecimalValue());
        addValueCreator(Float.class, XSD.FLOAT, new FloatValue());
        addValueCreator(Double.class, XSD.DOUBLE, new DoubleValue());
        addValueCreator(XSD.DURATION, new DurationValue());
        addValueCreator(GregorianCalendar.class, XSD.DATE_TIME, calendarValue);
        addSecondaryValueCreator(java.sql.Date.class, XSD.DATE_TIME, dateTimeValue);
        addSecondaryValueCreator(Date.class, XSD.DATE_TIME, dateTimeValue);
        addValueCreator(XSD.TIME, calendarValue);
        addValueCreator(XSD.DATE, calendarValue);
        addValueCreator(XSD.G_YEAR_MONTH, calendarValue);
        addValueCreator(XSD.G_YEAR, calendarValue);
        addValueCreator(XSD.G_MONTH_DAY, calendarValue);
        addValueCreator(XSD.G_DAY, calendarValue);
        addValueCreator(XSD.G_MONTH, new GMonthCalendarValue());
        addValueCreator(XSD.ANY_URI, new AnyURIValue());

        // Derived types
        addValueCreator(BigInteger.class, XSD.INTEGER, new IntegerValue());
        addValueCreator(Long.class, XSD.LONG, new LongValue());
        addValueCreator(Integer.class, XSD.INT, new IntValue());
        addValueCreator(Short.class, XSD.SHORT, new ShortValue());
        addValueCreator(Byte.class, XSD.BYTE, new ByteValue());
    }

    public static DatatypeFactory getInstance() {
        return FACTORY_INSTANCE;
    }

    public DatatypeFactoryImpl(final Map<URI, ValueCreator> newCreatorMap) {
        for (final URI datatypeURI : newCreatorMap.keySet()) {
            final ValueCreator valueCreator = newCreatorMap.get(datatypeURI);
            addValueCreator(datatypeURI, valueCreator);
        }
    }

    public boolean hasRegisteredValueCreator(final URI datatypeURI) {
        return factoryMap.containsKey(datatypeURI);
    }

    public boolean correctValueType(final Value value, final URI datatypeURI) {
        if (factoryMap.containsKey(datatypeURI)) {
            ValueCreator valueCreator = factoryMap.get(datatypeURI);
            return value.getClass().equals(valueCreator.getClass());
        } else {
            return false;
        }
    }

    public boolean hasClassRegistered(final Class<?> aClass) {
        return classToCreator.containsKey(aClass);
    }

    public void addValueCreator(final URI datatypeURI, final ValueCreator creator) throws IllegalArgumentException {
        if (!hasRegisteredValueCreator(datatypeURI)) {
            factoryMap.put(datatypeURI, creator);
        } else {
            throw new IllegalArgumentException("Value creator already registered for: " + datatypeURI);
        }
    }

    public void addValueCreator(final Class<?> aClass, final URI datatypeURI, final ValueCreator creator) {
        addValueCreator(datatypeURI, creator);
        addSecondaryValueCreator(aClass, datatypeURI, creator);
    }

    public void addSecondaryValueCreator(Class<?> aClass, final URI datatypeURI, final ValueCreator creator) {
        classToCreator.put(aClass, creator);
        classToURI.put(aClass, datatypeURI);
    }

    public boolean removeValueCreator(final URI datatypeURI) {
        return factoryMap.remove(datatypeURI) == null;
    }

    public boolean removeValueCreator(final Class<?> aClass, final URI datatypeURI) {
        final ValueCreator valueCreatorForDatatypeURI = factoryMap.get(datatypeURI);
        if (removeValueCreator(datatypeURI)) {
            final ValueCreator valueCreator = classToCreator.remove(aClass);
            if (valueCreator != null) {
                // Should always be true as CLASS_TO maps won't get out of sync.
                return classToURI.remove(aClass) != null;
            } else {
                // Add back because we failed to remove it from class creators.
                addValueCreator(datatypeURI, valueCreatorForDatatypeURI);
                return false;
            }
        }
        return false;
    }

    public Value createValue(final String newLexicalForm) {
        return factoryMap.get(NULL_URI).create(newLexicalForm);
    }

    public Value createValue(final Object newObject) {
        if (classToCreator.containsKey(newObject.getClass())) {
            final ValueCreator creator = classToCreator.get(newObject.getClass());
            return creator.create(newObject);
        } else {
            throw new IllegalArgumentException("No value creator registered for: " + newObject.getClass());
        }
    }

    public Value createValue(final String newLexicalForm, final URI dataTypeURI) {
        Value value;
        // Try and create a correctly typed value. If all else fails create a non-types/string version as RDF does not
        // require lexical values are correct.
        try {
            if (factoryMap.keySet().contains(dataTypeURI)) {
                final ValueCreator valueCreator = factoryMap.get(dataTypeURI);
                value = valueCreator.create(newLexicalForm);
            } else {
                value = createValue(newLexicalForm);
            }
        } catch (IllegalArgumentException e) {
            value = createValue(newLexicalForm);
        }
        return value;
    }

    public URI getObjectDatatypeURI(final Object object) {
        checkNotNull(object);
        if (classToURI.containsKey(object.getClass())) {
            return classToURI.get(object.getClass());
        } else {
            throw new IllegalArgumentException("No datatype URI registered for: " + object.getClass());
        }
    }
}
