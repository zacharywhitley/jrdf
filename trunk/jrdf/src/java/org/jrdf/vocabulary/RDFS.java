/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.vocabulary;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A set of constants for the standard RDFS vocabulary.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class RDFS extends Vocabulary {

    /**
     * Allow newer compiled version of the stub to operate when changes
     * have not occurred with the class.
     * NOTE : update this serialVersionUID when a method or a public member is
     * deleted.
     */
    private static final long serialVersionUID = 3344119222366953296L;

    /**
     * The URI of the RDFS name space.
     */
    public static final URI BASE_URI;

    /**
     * The class resource, everything.
     */
    public static final URI RESOURCE;

    /**
     * The class of classes.
     */
    public static final URI CLASS;

    /**
     * The class of literal values, eg. textual strings and integers.
     */
    public static final URI LITERAL;

    /**
     * The class of RDF datatypes.
     */
    public static final URI DATATYPE;

    /**
     * The class of RDF containers.
     */
    public static final URI CONTAINER;

    /**
     * The class of container membership properties, rdf:_1, rdf:_2, ...,
     * all of which are sub-properties of 'member'.
     */
    public static final URI CONTAINER_MEMBERSHIP_PROPERTY;

    /**
     * A range of the subject property.
     */
    public static final URI RANGE;

    /**
     * A domain of the subject property.
     */
    public static final URI DOMAIN;

    /**
     * The subject is a subclass of a class.
     */
    public static final URI SUB_CLASS_OF;

    /**
     * The subject is a subproperty of a property.
     */
    public static final URI SUB_PROPERTY_OF;

    /**
     * A human-readable name for the subject.
     */
    public static final URI LABEL;

    /**
     * A description of the subject resource.
     */
    public static final URI COMMENT;

    /**
     * A member of the subject resource.
     */
    public static final URI MEMBER;

    /**
     * Further information about the subject resource.
     */
    public static final URI SEE_ALSO;

    /**
     * The defininition of the subject resource.
     */
    public static final URI IS_DEFINED_BY;

    static {
        try {
            BASE_URI = new URI("http://www.w3.org/2000/01/rdf-schema#");

            // Classes
            RESOURCE = new URI(BASE_URI + "Resource");
            CLASS = new URI(BASE_URI + "Class");
            LITERAL = new URI(BASE_URI + "Literal");
            DATATYPE = new URI(BASE_URI + "Datatype");
            CONTAINER = new URI(BASE_URI + "Container");
            CONTAINER_MEMBERSHIP_PROPERTY =
                    new URI(BASE_URI + "ContainerMembershipProperty");

            // Properties
            RANGE = new URI(BASE_URI + "range");
            DOMAIN = new URI(BASE_URI + "domain");
            SUB_CLASS_OF = new URI(BASE_URI + "subClassOf");
            SUB_PROPERTY_OF = new URI(BASE_URI + "subPropertyOf");
            LABEL = new URI(BASE_URI + "label");
            COMMENT = new URI(BASE_URI + "comment");
            MEMBER = new URI(BASE_URI + "member");
            SEE_ALSO = new URI(BASE_URI + "seeAlso");
            IS_DEFINED_BY = new URI(BASE_URI + "isDefinedBy");

            // Add Classes
            RESOURCES.add(RESOURCE);
            RESOURCES.add(CLASS);
            RESOURCES.add(LITERAL);
            RESOURCES.add(DATATYPE);
            RESOURCES.add(CONTAINER);
            RESOURCES.add(CONTAINER_MEMBERSHIP_PROPERTY);

            // Add Properties
            RESOURCES.add(RANGE);
            RESOURCES.add(DOMAIN);
            RESOURCES.add(SUB_CLASS_OF);
            RESOURCES.add(SUB_PROPERTY_OF);
            RESOURCES.add(LABEL);
            RESOURCES.add(COMMENT);
            RESOURCES.add(MEMBER);
            RESOURCES.add(SEE_ALSO);
            RESOURCES.add(IS_DEFINED_BY);
        } catch (URISyntaxException use) {

            // This should never happen.
            throw new ExceptionInInitializerError("Failed to create required URIs");
        }
    }
}
