/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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
 */

package org.jrdf.query;

import java.net.URI;
import java.util.List;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.mem.GraphImpl;

/**
 * Remove this class!!! Not test driven. Contains duplicate code!!
 * @author Tom Adams
 * @version $Revision$
 */
public class XxxQuery implements Query {
    // FIXME TJA: Remove this class.
    // FIXME TJA: Rename, possibly to MockQuery, then delgate duplicated code to test utilities.

    private static final String URI_BOOK_1 = "http://example.org/book/book1";
    private static final String URI_DC_TITLE = "http://purl.org/dc/elements/1.1/title";
    private static final ObjectNode ANY_OBJECT_NODE = null;

    public List<? extends Variable> getProjectedVariables() {
        return Variable.ALL_VARIABLES;
    }

    public ConstraintExpression getConstraintExpression() {
        return createBook1DcTitleExpression();
    }

    private ConstraintExpression createBook1DcTitleExpression() {
        return new ConstraintTriple(createDcTitleTriple(URI_BOOK_1));
    }

    private Triple createDcTitleTriple(String bookUri) {
        return createTripleWithWildcardObject(bookUri, URI_DC_TITLE);
    }

    private Triple createTripleWithWildcardObject(String subjectUri, String predicateUri) {
        SubjectNode subject = createResource(subjectUri);
        PredicateNode predicate = createResource(predicateUri);
        return createTriple(subject, predicate, ANY_OBJECT_NODE);
    }

    private URIReference createResource(String uri) {
        try {
            return getElementFactory().createResource(new URI(uri));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Triple createTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        try {
            return getElementFactory().createTriple(subject, predicate, object);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    private GraphElementFactory getElementFactory() {
        return createGraph().getElementFactory();
    }

    private GraphImpl createGraph() {
        try {
            return new GraphImpl();
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }
}
