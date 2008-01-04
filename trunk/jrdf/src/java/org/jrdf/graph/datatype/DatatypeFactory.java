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

import java.net.URI;

/**
 * Creates data types.  Maps URIs to creators - which can take either a string or native object.  For a given URI
 * there is a signle string parser object and one or more native objects.
 */
public interface DatatypeFactory {

    /**
     * Add a value create for a given URI.  Enforces one value creator per data type - check with
     * hasRegisteredValueCreator.
     *
     * @param datatypeURI unique datatype uri.
     * @param creator creates datatypes based on string.
     * @throws IllegalArgumentException if datatype uri already registered with another creator.
     */
    void addValueCreator(URI datatypeURI, ValueCreator creator) throws IllegalArgumentException;

    /**
     * Add a value create for a given URI.  Enforces one value creator per data type - check with
     * hasRegisteredValueCreator.
     *
     * @param datatypeURI unique datatype uri.
     * @param aClass the Java class.
     * @param creator creates datatypes based on string.
     * @throws IllegalArgumentException if datatype uri already registered with another creator.
     */
    void addValueCreator(URI datatypeURI, Class<?> aClass, ValueCreator creator) throws IllegalArgumentException;

    /**
     * If you want to bind more than one Java type to a given URI use this for alternative bindings.  For example,
     * XSD Date can be bound to java.util.GregorianCalendar, java.util.Date or java.sql.Date.  The secondary types are
     * java.util.Date and java.sql.Date.
     *
     * @param aClass the Java class to bind to a given URI and ValueCreator.
     * @param datatypeURI the URI to bind.
     * @param creator the creator to use to convert the class to a Value.
     */
    void addSecondaryValueCreator(Class<?> aClass, URI datatypeURI, ValueCreator creator);

    /**
     * Returns true if a value creator has been registered for a given URI.
     *
     * @param datatypeURI the URI to check.
     * @return true if a value creator has been registered for a given URI.
     */
    boolean hasRegisteredValueCreator(URI datatypeURI);

    /**
     * Remove creator for a given URI.
     *
     * @param datatypeURI the URI to check.
     * @return true if the item was removed or no value creator was found.
     */
    boolean removeValueCreator(URI datatypeURI);

    /**
     * Remove creator for a given class and URI.
     *
     * @param aClass the class that's registered.
     * @param datatypeURI the URI to check.
     * @return true if the item was removed or no value creator was found.
     */
    boolean removeValueCreator(Class<?> aClass, URI datatypeURI);

    /**
     * Create an untyped/plain datatype from the lexical form.
     *
     * @param lexicalForm lexical form to use.
     * @return new datatype value.
     */
    DatatypeValue createValue(String lexicalForm);

    /**
     * Create a datatyped literal from a Java class.
     *
     * @param object Java class to use.
     * @return new datatype value.
     * @throws IllegalArgumentException if to creator is registered for the Java class.
     */
    DatatypeValue createValue(Object object) throws IllegalArgumentException;

    /**
     * Create a new datatype value based on a given lexical form and datatype.  Returns a string datatype if no creator
     * is registered for the given URI.
     *
     * @param dataTypeURI datatype to use.
     * @param lexicalForm lexical form to use.
     * @return new datatype value.
     */
    DatatypeValue createValue(URI dataTypeURI, String lexicalForm);

    /**
     * Returns the URI bound to a given Java class.
     *
     * @param object the object to use.
     * @return the URI.
     * @throws IllegalArgumentException if there is no creator bound for the Java class.
     */
    URI getObjectDatatypeURI(Object object);

    /**
     * Returns true if the class is registered.
     *
     * @param aClass the class to check.
     * @return true if the class is registered.
     */
    boolean hasClassRegistered(Class<?> aClass);

    /**
     * Allows the user to verify that the Value created is the correct type for the given URI.
     *
     * The default behaviour of the factory is to create untyped strings if there is an error parsing the string.  For
     * example, trying to parse and XSD.Date with the string "abc" will result in a Value with the lexical form "abc"
     * but be a StringValue.  This allows you to check that the Value returned is what is expected.
     *
     * @param value the value to check.
     * @param datatypeURI the URI to use to see if the value is the expected type.
     * @return true if the value is the correctly bound type of the given URI.
     */
    boolean correctValueType(DatatypeValue value, URI datatypeURI);
}
