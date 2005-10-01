/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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

package org.jrdf.util.test;

import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.query.ConstraintExpression;
import org.jrdf.query.ConstraintTriple;

import java.net.URI;


/**
 * Artefacts used in tests.
 *
 * @author Tom Adams
 * @author Andrew Newman
 * @version $Revision$
 */
public class TripleTestUtil {
    public static final URI URI_BOOK_1 = URI.create("http://example.org/book/book1");
    public static final URI URI_BOOK_2 = URI.create("http://example.org/book/book2");
    public static final URI URI_BOOK_3 = URI.create("http://example.org/book/book3");
    public static final URI URI_DC_TITLE = URI.create("http://purl.org/dc/elements/1.1/title");
    public static final URI URI_DC_SUBJECT = URI.create("http://purl.org/dc/elements/1.1/subject");
    public static final String LITERAL_BOOK_TITLE = "The Pragmatic Programmer";
    public static final Triple TRIPLE_BOOK_1_DC_TITLE_VARIABLE = createDcTitleTriple(URI_BOOK_1);
    public static final Triple TRIPLE_BOOK_2_DC_TITLE_VARIABLE = createDcTitleTriple(URI_BOOK_2);
    public static final Triple TRIPLE_BOOK_3_DC_TITLE_VARIABLE = createDcTitleTriple(URI_BOOK_3);
    public static final Triple TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE = createDcSubjectTriple(URI_BOOK_1);
    public static final Triple TRIPLE_BOOK_1_DC_SUBJECT_LITERAL = createDcSubjectTriple(URI_BOOK_1, LITERAL_BOOK_TITLE);

    public static ConstraintExpression createBookDcTitleExpression(URI bookUri) {
        return new ConstraintTriple(createDcTitleTriple(bookUri));
    }

    private static Triple createDcTitleTriple(URI bookUri) {
        return createTripleWithVariableObject(bookUri, URI_DC_TITLE);
    }

    private static Triple createDcSubjectTriple(URI bookUri) {
        return createTripleWithVariableObject(bookUri, URI_DC_SUBJECT);
    }

    private static Triple createDcSubjectTriple(URI bookUri, String literal) {
        return createTripleWithLiteralObject(bookUri, URI_DC_SUBJECT, literal);
    }

    private static Triple createTripleWithVariableObject(URI subjectUri, URI predicateUri) {
        return createTriple(subjectUri, predicateUri, AnyObjectNode.ANY_OBJECT_NODE);
    }

    private static Triple createTripleWithLiteralObject(URI subjectUri, URI predicateUri, String literal) {
        ObjectNode object = NodeTestUtil.createLiteral(literal);
        return createTriple(subjectUri, predicateUri, object);
    }

    private static Triple createTriple(URI subjectUri, URI predicateUri, ObjectNode object) {
        SubjectNode subject = NodeTestUtil.createResource(subjectUri);
        PredicateNode predicate = NodeTestUtil.createResource(predicateUri);
        return createTriple(subject, predicate, object);
    }

    private static Triple createTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return NodeTestUtil.createTriple(subject, predicate, object);
    }
}
