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

package org.jrdf.util.test;

import org.jrdf.TestJRDFFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.Literal;
import org.jrdf.query.expression.Constraint;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.graph.local.iterator.ClosableIterator;
import static org.jrdf.util.test.NodeTestUtil.createResource;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


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
    public static final URI FOAF_NAME = URI.create("http://xmlns.com/foaf/0.1/name");
    public static final URI FOAF_NICK = URI.create("http://xmlns.com/foaf/0.1/nick");
    public static final URI FOAF_MBOX = URI.create("http://xmlns.com/foaf/0.1/mbox");
    public static final String LITERAL_BOOK_TITLE = "The Pragmatic Programmer";
    public static final Triple TRIPLE_BOOK_1_DC_TITLE_VARIABLE = createDcTitleTriple(URI_BOOK_1);
    public static final Triple TRIPLE_BOOK_2_DC_TITLE_VARIABLE = createDcTitleTriple(URI_BOOK_2);
    public static final Triple TRIPLE_BOOK_3_DC_TITLE_VARIABLE = createDcTitleTriple(URI_BOOK_3);
    public static final Triple TRIPLE_BOOK_1_DC_SUBJECT_VARIABLE = createDcSubjectTriple(URI_BOOK_1);
    public static final Triple TRIPLE_BOOK_1_DC_SUBJECT_LITERAL = createDcSubjectTriple(URI_BOOK_1, LITERAL_BOOK_TITLE);
    public static final Triple TRIPLE_VARIABLE_VARIABLE_SUBJECT = createVariableSubjectTriple(URI_DC_SUBJECT);

    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();

    private static final AttributeValuePairHelper AVP_HELPER = FACTORY.getNewAttributeValuePairHelper();

    public static final List<AttributeValuePair> AVP_BOOK_1_DC_SUBJECT_LITERAL =
            createAvp(TRIPLE_BOOK_1_DC_SUBJECT_LITERAL);

    public static Expression<ExpressionVisitor> createBookDcTitleExpression(URI bookUri, long suffix) {
        Triple dcTitleTriple = createDcTitleTriple(bookUri);
        List<AttributeValuePair> avp = AVP_HELPER.createAvp(dcTitleTriple, createAttributes(suffix));
        return new Constraint<ExpressionVisitor>(avp);
    }

    public static Expression<ExpressionVisitor> createConstraintExpression(String varSubject, URI predicate,
            String varObject, long suffix) {
        Triple triple = createTriple(ANY_SUBJECT_NODE, NodeTestUtil.createResource(predicate), ANY_OBJECT_NODE);
        List<AttributeValuePair> avp = AVP_HELPER.createAvp(triple, createSubjectObjectVariableAttributes(varSubject,
                varObject, suffix));
        return new Constraint<ExpressionVisitor>(avp);
    }

    public static Expression<ExpressionVisitor> createConstraintExpression(String varSubject, String varPredicate,
            Literal constLiteral, long suffix) {
        Triple triple = createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, constLiteral);
        List<AttributeValuePair> avp = AVP_HELPER.createAvp(triple, createSubjectPredicateVariableAttributes(
                varSubject, varPredicate, suffix));
        return new Constraint<ExpressionVisitor>(avp);
    }

    public static Triple createTripleAllSame(URI uri) {
        SubjectNode subject = createResource(uri);
        PredicateNode predicate = createResource(uri);
        ObjectNode object = createResource(uri);
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
        return createTriple(subjectUri, predicateUri, ANY_OBJECT_NODE);
    }

    private static Triple createTripleWithLiteralObject(URI subjectUri, URI predicateUri, String literal) {
        ObjectNode object = NodeTestUtil.createLiteral(literal);
        return createTriple(subjectUri, predicateUri, object);
    }

    private static Triple createVariableSubjectTriple(URI uriDcSubject) {
        ObjectNode object = NodeTestUtil.createResource(uriDcSubject);
        return createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, object);
    }

    private static Triple createTriple(URI subjectUri, URI predicateUri, ObjectNode object) {
        SubjectNode subject = NodeTestUtil.createResource(subjectUri);
        PredicateNode predicate = NodeTestUtil.createResource(predicateUri);
        return createTriple(subject, predicate, object);
    }

    private static Triple createTriple(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        return NodeTestUtil.createTriple(subject, predicate, object);
    }

    private static List<AttributeValuePair> createAvp(Triple tripleBook1DcSubjectLiteral) {
        return AVP_HELPER.createAvp(tripleBook1DcSubjectLiteral);
    }

    private static Attribute[] createAttributes(long suffix) {
        AttributeName subjectName = new PositionName("SUBJECT" + suffix);
        AttributeName predicateName = new PositionName("PREDICATE" + suffix);
        AttributeName objectName = new VariableName("title");
        return createAttributes(subjectName, predicateName, objectName);
    }

    private static Attribute[] createSubjectObjectVariableAttributes(String subject, String object, long suffix) {
        AttributeName subjectName = new VariableName(subject);
        AttributeName predicateName = new PositionName("PREDICATE" + suffix);
        AttributeName objectName = new VariableName(object);
        return createAttributes(subjectName, predicateName, objectName);
    }

    private static Attribute[] createSubjectPredicateVariableAttributes(String subject, String predicate, long suffix) {
        AttributeName subjectName = new VariableName(subject);
        AttributeName predicateName = new VariableName(predicate);
        AttributeName objectName = new PositionName("OBJECT" + suffix);
        return createAttributes(subjectName, predicateName, objectName);
    }

    private static Attribute[] createAttributes(AttributeName subject, AttributeName predicate, AttributeName object) {
        Attribute ATT_1 = new AttributeImpl(subject, new SubjectNodeType());
        Attribute ATT_2 = new AttributeImpl(predicate, new PredicateNodeType());
        Attribute ATT_3 = new AttributeImpl(object, new ObjectNodeType());
        return new Attribute[]{ATT_1, ATT_2, ATT_3};
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
