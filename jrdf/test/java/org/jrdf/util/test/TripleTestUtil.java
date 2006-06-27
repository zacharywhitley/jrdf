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

import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.query.constraint.ConstraintExpression;
import org.jrdf.query.constraint.ConstraintTriple;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.SortedAttributeValuePairHelper;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.util.ClosableIterator;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;


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
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final SortedAttributeValuePairHelper AVP_HELPER = FACTORY.getNewSortedAttributeValuePairHelper();
    public static final SortedSet<AttributeValuePair> AVP_BOOK_1_DC_SUBJECT_LITERAL
            = createAvp(TRIPLE_BOOK_1_DC_SUBJECT_LITERAL);
    private static final PositionName SUBJECT_POS_NAME = new PositionName("SUBJECT");
    private static final PositionName PREDICATE_POS_NAME = new PositionName("PREDICATE");
    private static final VariableName OBJECT_POS_NAME = new VariableName("?title");
    private static final AttributeImpl ATT_1 = new AttributeImpl(SUBJECT_POS_NAME, new SubjectNodeType());
    private static final AttributeImpl ATT_2 = new AttributeImpl(PREDICATE_POS_NAME, new PredicateNodeType());
    private static final AttributeImpl ATT_3 = new AttributeImpl(OBJECT_POS_NAME, new ObjectNodeType());
    private static final Attribute[] ATTRIBUTES = new Attribute[]{ATT_1, ATT_2, ATT_3};

    public static ConstraintExpression createBookDcTitleExpression(URI bookUri) {
        Triple dcTitleTriple = createDcTitleTriple(bookUri);
        SortedSet<AttributeValuePair> avp = AVP_HELPER.createAvp(dcTitleTriple, ATTRIBUTES);
        return new ConstraintTriple(avp);
    }

    public static Triple createTripleAllSame(URI uri) {
        SubjectNode subject = NodeTestUtil.createResource(uri);
        PredicateNode predicate = NodeTestUtil.createResource(uri);
        ObjectNode object = NodeTestUtil.createResource(uri);
        return createTriple(subject, predicate, object);
    }

    public static ClosableIterator<Triple> createTripleIterator(Triple[] triples) {
        List<Triple> triplesList = Arrays.asList(triples);
        Iterator<Triple> iterator = triplesList.iterator();
        return new SimpleClosableIterator<Triple>(iterator);
    }

    public static Triple createDcSubjectTriple(URI bookUri, String literal) {
        return createTripleWithLiteralObject(bookUri, URI_DC_SUBJECT, literal);
    }

    private static Triple createDcTitleTriple(URI bookUri) {
        return createTripleWithVariableObject(bookUri, URI_DC_TITLE);
    }

    private static Triple createDcSubjectTriple(URI bookUri) {
        return createTripleWithVariableObject(bookUri, URI_DC_SUBJECT);
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

    private static SortedSet<AttributeValuePair> createAvp(Triple tripleBook1DcSubjectLiteral) {
        return AVP_HELPER.createAvp(tripleBook1DcSubjectLiteral);
    }

    private static class SimpleClosableIterator<Triple> implements ClosableIterator<Triple> {
        private Iterator<Triple> iter;

        private SimpleClosableIterator(Iterator<Triple> iter) {
            this.iter = iter;
        }

        public boolean close() {
            return true;
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public Triple next() {
            return iter.next();
        }

        public void remove() {
            iter.remove();
        }
    }
}
