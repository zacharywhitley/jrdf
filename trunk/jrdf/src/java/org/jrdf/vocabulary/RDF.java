/*
 * $Header$
 * $Revision: 2097 $
 * $Date: 2008-06-23 15:07:25 +1000 (Mon, 23 Jun 2008) $
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

package org.jrdf.vocabulary;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A set of constants for the standard RDF vocabulary.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class RDF extends Vocabulary {

    /**
     * Allow newer compiled version of the stub to operate when changes
     * have not occurred with the class.
     * NOTE : update this serialVersionUID when a method or a public member is
     * deleted.
     */
    private static final long serialVersionUID = 5974585938932893808L;

    /**
     * The URI of the RDF name space.
     */
    public static final URI BASE_URI;

    /**
     * The class of unordered containers.
     */
    public static final URI BAG;

    /**
     * The class of ordered containers.
     */
    public static final URI SEQ;

    /**
     * The class of containers of alternatives.
     */
    public static final URI ALT;

    /**
     * The class of RDF statements.
     */
    public static final URI STATEMENT;

    /**
     * The class of RDF properties.
     */
    public static final URI PROPERTY;

    /**
     * The class of XML literal values.
     */
    public static final URI XML_LITERAL;

    /**
     * The class of RDF Lists.
     */
    public static final URI LIST;

    /**
     * A special property element that is equivalent to rdf:_1, rdf:_2 in order.
     * Only used in RDF/XML as inserting members of containers using LI normally
     * will result in duplicate instances not being recorded.
     */
    public static final URI LI;

    /**
     * The empty list, with no items in it. If the rest of a list is nil then
     * the list has no more items in it.
     */
    public static final URI NIL;

    /**
     * The subject of the subject RDF statement.
     */
    public static final URI SUBJECT;

    /**
     * The predicate of the subject RDF statement.
     */
    public static final URI PREDICATE;

    /**
     * The object of the subject RDF statement.
     */
    public static final URI OBJECT;

    /**
     * The subject is an instance of a class.
     */
    public static final URI TYPE;

    /**
     * Idiomatic property used for structured values.
     */
    public static final URI VALUE;

    /**
     * The first item in the subject RDF list.
     */
    public static final URI FIRST;

    /**
     * The rest of the subject RDF list after the first item.
     */
    public static final URI REST;

    static {
        try {
            BASE_URI = new URI("http://www.w3.org/1999/02/22-rdf-syntax-ns#");

            // Classes
            SEQ = new URI(BASE_URI + "Seq");
            BAG = new URI(BASE_URI + "Bag");
            ALT = new URI(BASE_URI + "Alt");
            STATEMENT = new URI(BASE_URI + "Statement");
            PROPERTY = new URI(BASE_URI + "Property");
            XML_LITERAL = new URI(BASE_URI + "XMLLiteral");
            LIST = new URI(BASE_URI + "List");
            NIL = new URI(BASE_URI + "nil");

            LI = new URI(BASE_URI + "li");

            // Properties
            SUBJECT = new URI(BASE_URI + "subject");
            PREDICATE = new URI(BASE_URI + "predicate");
            OBJECT = new URI(BASE_URI + "object");
            TYPE = new URI(BASE_URI + "type");
            VALUE = new URI(BASE_URI + "value");
            FIRST = new URI(BASE_URI + "first");
            REST = new URI(BASE_URI + "rest");

            // Add Classes
            addClasses();

            // Add Properties
            addProperties();

        } catch (URISyntaxException use) {

            // This should never happen.
            throw new ExceptionInInitializerError("Failed to create required URIs");
        }
    }

    private static void addProperties() {
        RESOURCES.add(SUBJECT);
        RESOURCES.add(PREDICATE);
        RESOURCES.add(OBJECT);
        RESOURCES.add(TYPE);
        RESOURCES.add(VALUE);
        RESOURCES.add(FIRST);
        RESOURCES.add(REST);
    }

    private static void addClasses() {
        RESOURCES.add(SEQ);
        RESOURCES.add(BAG);
        RESOURCES.add(ALT);
        RESOURCES.add(STATEMENT);
        RESOURCES.add(PROPERTY);
        RESOURCES.add(XML_LITERAL);
        RESOURCES.add(LIST);
        RESOURCES.add(NIL);

        RESOURCES.add(LI);
    }
}
