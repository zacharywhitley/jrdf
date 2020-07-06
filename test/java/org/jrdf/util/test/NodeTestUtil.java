/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.util.test;

import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;

import java.net.URI;

/**
 * Utility for creating nodes, etc.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class NodeTestUtil {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();

    private NodeTestUtil() {
    }

    public static URIReference createResource(URI uri) {
        try {
            return getElementFactory().createURIReference(uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Literal createLiteral(String literal) {
        try {
            return getElementFactory().createLiteral(literal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Literal createLiteral(String literal, String language) {
        try {
            return getElementFactory().createLiteral(literal, language);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    public static Literal createLiteral(String literal, URI datatype) {
        try {
            return getElementFactory().createLiteral(literal, datatype);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    // FIXME TJA: Remove dependence on GraphImpl. Should be able to Mock this out.
    public static Triple createTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return getTripleFactory().createTriple(subject, predicate, object);
    }

    public static Triple createTriple(URI subject, URI predicate, URI object) {
        try {
            return getTripleFactory().addTriple(subject, predicate, object);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    public static Triple createTriple(URI subject, URI predicate, String object) {
        try {
            return getTripleFactory().addTriple(subject, predicate, object);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    public static Triple createTriple(URI subject, URI predicate, String object, String language) {
        try {
            return getTripleFactory().addTriple(subject, predicate, object, language);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    public static Triple createTriple(URI subject, URI predicate, String object, URI datatype) {
        try {
            return getTripleFactory().addTriple(subject, predicate, object, datatype);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    // FIXME TJA: Remove dependence on GraphImpl. Should be able to Mock this out.
    private static GraphElementFactory getElementFactory() {
        return createGraph().getElementFactory();
    }

    private static TripleFactory getTripleFactory() {
        return createGraph().getTripleFactory();
    }

    // FIXME TJA: Remove dependence on GraphImpl. Should be able to Mock this out.
    private static Graph createGraph() {
        return FACTORY.getGraph();
    }
}
