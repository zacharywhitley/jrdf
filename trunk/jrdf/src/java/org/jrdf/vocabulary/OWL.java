/*
 * $Header$
 * $Revision: 2062 $
 * $Date: 2008-05-27 11:57:23 +1000 (Tue, 27 May 2008) $
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
 * A set of constants for the standard RDFS vocabulary.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class OWL extends Vocabulary {

    /**
     * Allow newer compiled version of the stub to operate when changes
     * have not occurred with the class.
     * NOTE : update this serialVersionUID when a method or a public member is
     * deleted.
     */
    private static final long serialVersionUID = 4188228337418703356L;

    /**
     * The URI of the RDFS name space.
     */
    public static final URI BASE_URI;

    /**
     * Class.
     */
    public static final URI CLASS;

    /**
     * Thing.
     */
    public static final URI THING;

    /**
     * Nothing.
     */
    public static final URI NOTHING;

    /**
     * All Different.
     */
    public static final URI ALL_DIFFERENT;

    /**
     * Restriction.
     */
    public static final URI RESTRICTION;

    /**
     * Object property.
     */
    public static final URI OBJECT_PROPERTY;

    /**
     * Datatype property.
     */
    public static final URI DATATYPE_PROPERTY;

    /**
     * Transitive property.
     */
    public static final URI TRANSITIVE_PROPERTY;

    /**
     * Symmetric property.
     */
    public static final URI SYMMETRIC_PROPERTY;

    /**
     * Functional property.
     */
    public static final URI FUNCTIONAL_PROPERTY;

    /**
     * Inverse functional property.
     */
    public static final URI INVERSE_FUNCTIONAL_PROPERTY;

    /**
     * Annotation property.
     */
    public static final URI ANNOTATION_PROPERTY;

    /**
     * Ontology.
     */
    public static final URI ONTOLOGY;

    /**
     * Ontology Property.
     */
    public static final URI ONTOLOGY_PROPERTY;

    /**
     * Deprecated Class.
     */
    public static final URI DEPRECATED_CLASS;

    /**
     * Deprecated property.
     */
    public static final URI DEPRECATED_PROPERTY;

    /**
     * Data Range.
     */
    public static final URI DATA_RANGE;

    /**
     * Equivalent class.
     */
    public static final URI EQUIVALENT_CLASS;

    /**
     * Disjoint with.
     */
    public static final URI DISJOINT_WITH;

    /**
     * Equivalent property.
     */
    public static final URI EQUIVALENT_PROPERTY;

    /**
     * Same as.
     */
    public static final URI SAME_AS;

    /**
     * Different from.
     */
    public static final URI DIFFERENT_FROM;

    /**
     * Distinct members.
     */
    public static final URI DISTINCT_MEMBERS;

    /**
     * Union of.
     */
    public static final URI UNION_OF;

    /**
     * Intersection of.
     */
    public static final URI INTERSECTION_OF;

    /**
     * Complement of.
     */
    public static final URI COMPLEMENT_OF;

    /**
     * One of.
     */
    public static final URI ONE_OF;

    /**
     * On property.
     */
    public static final URI ON_PROPERTY;

    /**
     * All Values From.
     */
    public static final URI ALL_VALUES_FROM;

    /**
     * Has Value.
     */
    public static final URI HAS_VALUE;

    /**
     * Some values from.
     */
    public static final URI SOME_VALUES_FROM;

    /**
     * Minimum cardinality.
     */
    public static final URI MIN_CARDINALITY;

    /**
     * Maximum cardinality.
     */
    public static final URI MAX_CARDINALITY;

    /**
     * Cardinality.
     */
    public static final URI CARDINALITY;

    /**
     * Inverse of.
     */
    public static final URI INVERSE_OF;

    /**
     * Label.
     */
    public static final URI LABEL;

    /**
     * Comment.
     */
    public static final URI COMMENT;

    /**
     * Further information about the subject resource.
     */
    public static final URI SEE_ALSO;

    /**
     * The defininition of the subject resource.
     */
    public static final URI IS_DEFINED_BY;

    /**
     * Imports.
     */
    public static final URI IMPORTS;

    /**
     * Version info.
     */
    public static final URI VERSION_INFO;

    /**
     * Prior version.
     */
    public static final URI PRIOR_VERSION;

    /**
     * Backward compatible with.
     */
    public static final URI BACKWARD_COMPATIBLE_WITH;

    /**
     * Incompatible with.
     */
    public static final URI INCOMPATIBLE_WITH;

    static {
        try {
            BASE_URI = new URI("http://www.w3.org/2002/07/owl#");

            // Classes
            CLASS = new URI(BASE_URI + "Class");
            THING = new URI(BASE_URI + "Thing");
            NOTHING = new URI(BASE_URI + "Nothing");
            ALL_DIFFERENT = new URI(BASE_URI + "AllDifferent");
            RESTRICTION = new URI(BASE_URI + "Restriction");
            OBJECT_PROPERTY = new URI(BASE_URI + "ObjectProperty");
            DATATYPE_PROPERTY = new URI(BASE_URI + "DatatypeProperty");
            TRANSITIVE_PROPERTY = new URI(BASE_URI + "TransitiveProperty");
            SYMMETRIC_PROPERTY = new URI(BASE_URI + "SymmetricProperty");
            FUNCTIONAL_PROPERTY = new URI(BASE_URI + "FunctionalProperty");
            INVERSE_FUNCTIONAL_PROPERTY = new URI(BASE_URI + "InverseFunctionalProperty");
            ANNOTATION_PROPERTY = new URI(BASE_URI + "AnnotationProperty");
            ONTOLOGY = new URI(BASE_URI + "Ontology");
            ONTOLOGY_PROPERTY = new URI(BASE_URI + "OntologyProperty");
            DEPRECATED_CLASS = new URI(BASE_URI + "DeprecatedClass");
            DEPRECATED_PROPERTY = new URI(BASE_URI + "DeprecatedProperty");
            DATA_RANGE = new URI(BASE_URI + "DataRange");

            // Properties
            EQUIVALENT_CLASS = new URI(BASE_URI + "equivalentClass");
            DISJOINT_WITH = new URI(BASE_URI + "disjointWith");
            EQUIVALENT_PROPERTY = new URI(BASE_URI + "equivalentProperty");
            SAME_AS = new URI(BASE_URI + "sameAs");
            DIFFERENT_FROM = new URI(BASE_URI + "differentFrom");
            DISTINCT_MEMBERS = new URI(BASE_URI + "distinctMembers");
            UNION_OF = new URI(BASE_URI + "unionOf");
            INTERSECTION_OF = new URI(BASE_URI + "intersectionOf");
            COMPLEMENT_OF = new URI(BASE_URI + "complementOf");
            ONE_OF = new URI(BASE_URI + "oneOf");
            ON_PROPERTY = new URI(BASE_URI + "onProperty");
            ALL_VALUES_FROM = new URI(BASE_URI + "allValuesFrom");
            HAS_VALUE = new URI(BASE_URI + "hasValue");
            SOME_VALUES_FROM = new URI(BASE_URI + "someValuesFrom");
            MIN_CARDINALITY = new URI(BASE_URI + "minCardinality");
            MAX_CARDINALITY = new URI(BASE_URI + "maxCardinality");
            CARDINALITY = new URI(BASE_URI + "cardinality");
            INVERSE_OF = new URI(BASE_URI + "inverseOf");
            LABEL = new URI(BASE_URI + "label");
            COMMENT = new URI(BASE_URI + "comment");
            SEE_ALSO = new URI(BASE_URI + "seeAlso");
            IS_DEFINED_BY = new URI(BASE_URI + "isDefinedBy");
            IMPORTS = new URI(BASE_URI + "imports");
            VERSION_INFO = new URI(BASE_URI + "versionInfo");
            PRIOR_VERSION = new URI(BASE_URI + "priorVersion");
            BACKWARD_COMPATIBLE_WITH = new URI(BASE_URI + "backwardCompatibleWith");
            INCOMPATIBLE_WITH = new URI(BASE_URI + "incompatibleWith");

            // Add Classes
            RESOURCES.add(CLASS);
            RESOURCES.add(THING);
            RESOURCES.add(NOTHING);
            RESOURCES.add(ALL_DIFFERENT);
            RESOURCES.add(RESTRICTION);
            RESOURCES.add(OBJECT_PROPERTY);
            RESOURCES.add(DATATYPE_PROPERTY);
            RESOURCES.add(TRANSITIVE_PROPERTY);
            RESOURCES.add(SYMMETRIC_PROPERTY);
            RESOURCES.add(FUNCTIONAL_PROPERTY);
            RESOURCES.add(INVERSE_FUNCTIONAL_PROPERTY);
            RESOURCES.add(ANNOTATION_PROPERTY);
            RESOURCES.add(ONTOLOGY);
            RESOURCES.add(ONTOLOGY_PROPERTY);
            RESOURCES.add(DEPRECATED_CLASS);
            RESOURCES.add(DEPRECATED_PROPERTY);
            RESOURCES.add(DATA_RANGE);

            // Add Properties
            RESOURCES.add(EQUIVALENT_CLASS);
            RESOURCES.add(DISJOINT_WITH);
            RESOURCES.add(EQUIVALENT_PROPERTY);
            RESOURCES.add(SAME_AS);
            RESOURCES.add(DIFFERENT_FROM);
            RESOURCES.add(DISTINCT_MEMBERS);
            RESOURCES.add(UNION_OF);
            RESOURCES.add(INTERSECTION_OF);
            RESOURCES.add(COMPLEMENT_OF);
            RESOURCES.add(ONE_OF);
            RESOURCES.add(ON_PROPERTY);
            RESOURCES.add(ALL_VALUES_FROM);
            RESOURCES.add(HAS_VALUE);
            RESOURCES.add(SOME_VALUES_FROM);
            RESOURCES.add(MIN_CARDINALITY);
            RESOURCES.add(MAX_CARDINALITY);
            RESOURCES.add(CARDINALITY);
            RESOURCES.add(INVERSE_OF);
            RESOURCES.add(LABEL);
            RESOURCES.add(COMMENT);
            RESOURCES.add(SEE_ALSO);
            RESOURCES.add(IS_DEFINED_BY);
            RESOURCES.add(IMPORTS);
            RESOURCES.add(VERSION_INFO);
            RESOURCES.add(PRIOR_VERSION);
            RESOURCES.add(BACKWARD_COMPATIBLE_WITH);
            RESOURCES.add(INCOMPATIBLE_WITH);
        } catch (URISyntaxException use) {
            // This should never happen.
            throw new ExceptionInInitializerError("Failed to create required URIs");
        }
    }
}