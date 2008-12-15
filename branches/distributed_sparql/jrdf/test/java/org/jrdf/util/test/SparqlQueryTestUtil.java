/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.util.test;

import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import static org.jrdf.util.test.TripleTestUtil.FOAF_MBOX;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NAME;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NICK;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_1;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_2;
import static org.jrdf.util.test.TripleTestUtil.URI_BOOK_3;
import static org.jrdf.util.test.TripleTestUtil.URI_DC_TITLE;
import static org.jrdf.util.test.TripleTestUtil.createBookDcTitleExpression;
import static org.jrdf.util.test.TripleTestUtil.createConstraintExpression;

/**
 * Artefacts used in tests.
 *
 * @author Tom Adams
 * @author Andrew Newman
 * @version $Revision$
 */
public final class SparqlQueryTestUtil {

    public static final String VARIABLE_NAME_TITLE = "?title";
    public static final String VARIABLE_NAME_SUBJECT = "?subject";
    public static final String VARIABLE_TITLE = VARIABLE_NAME_TITLE;
    private static final String SUBJECT_URI_1 = URI_BOOK_1.toString();
    private static final String SUBJECT_URI_2 = URI_BOOK_2.toString();
    private static final String SUBJECT_URI_3 = URI_BOOK_3.toString();
    private static final String PREDICATE_URI_1 = URI_DC_TITLE.toString();
    public static final String QUERY_BOOK_1_DC_TITLE =
        createQueryString(SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE);
    public static final String QUERY_BOOK_2_DC_TITLE =
        createQueryString(SUBJECT_URI_2, PREDICATE_URI_1, VARIABLE_TITLE);
    public static final String QUERY_BOOK_1_AND_2 =
        createQueryString(new String[]{SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE, SUBJECT_URI_2,
            PREDICATE_URI_1, VARIABLE_TITLE});
    public static final String QUERY_BOOK_1_AND_2_AND_3 =
        createQueryString(new String[]{SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE, SUBJECT_URI_2,
            PREDICATE_URI_1, VARIABLE_TITLE, SUBJECT_URI_3, PREDICATE_URI_1, VARIABLE_TITLE});
    public static final String QUERY_BOOK_1_UNION_2 = createSelectClause() + "\nWHERE {{ " +
        createTriple(SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE) + "} \nUNION {" +
        createTriple(SUBJECT_URI_2, PREDICATE_URI_1, VARIABLE_TITLE) + " }}";
    public static final String QUERY_BOOK_1_UNION_2_UNION_3 = createSelectClause() + "\nWHERE {{ " +
        createTriple(SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE) + "} \nUNION {" +
        createTriple(SUBJECT_URI_2, PREDICATE_URI_1, VARIABLE_TITLE) + " } \nUNION {" +
        createTriple(SUBJECT_URI_3, PREDICATE_URI_1, VARIABLE_TITLE) + " }}";
    public static final String QUERY_BOOK_1_UNION_2_UNION_EMPTY = createSelectClause() + "\nWHERE {{ " +
        createTriple(SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE) + "} \nUNION {" +
        createTriple(SUBJECT_URI_2, PREDICATE_URI_1, VARIABLE_TITLE) + " } \nUNION { }}";
    public static final String QUERY_EMPTY_UNION_BOOK_1_UNION_2 = createSelectClause() + "\nWHERE {{} UNION { " +
        createTriple(SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE) + "} \nUNION {" +
        createTriple(SUBJECT_URI_2, PREDICATE_URI_1, VARIABLE_TITLE) + " } }";
    public static final String EMPTY_UNION_EMPTY = createSelectClause() + "\nWHERE {{} UNION { }}";
    public static final String QUERY_BOOK_1_AND_2_INNER_RIGHT = createSelectClause() + "\n" +
        "WHERE { " + createTriple(SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE) + " . {" +
        createTriple(SUBJECT_URI_2, PREDICATE_URI_1, VARIABLE_TITLE) + " }}";

    public static final String QUERY_OPTIONAL_1 = createSelectClause() +
        "WHERE  { ?x <" + FOAF_NAME + "> ?name . \n" +
        "         OPTIONAL { ?x <" + FOAF_NICK + "> ?nick\n" +
        "         OPTIONAL { ?x <" + FOAF_MBOX + "> ?mbox } }\n" +
        "       }";
    public static final String QUERY_OPTIONAL_2 = createSelectClause() +
        "WHERE  { ?x <" + FOAF_NAME + "> ?name .\n" +
        "         OPTIONAL { ?x <" + FOAF_NICK + "> ?alias }\n" +
        "         OPTIONAL { ?x <" + FOAF_MBOX + "> ?alias }\n" +
        "       }";
    public static final String QUERY_OPTIONAL_3 = createSelectClause() +
        "WHERE  { { ?x <" + FOAF_NAME + "> ?name } .\n" +
        "         OPTIONAL { ?x <" + FOAF_NICK + "> ?alias }\n" +
        "         OPTIONAL { ?x <" + FOAF_MBOX + "> ?alias }\n" +
        "       }";
    public static final String QUERY_OPTION_4 = createSelectClause() +
        "WHERE  { \n" +
        "  { ?x <" + FOAF_NAME + "> ?name OPTIONAL { ?x <" + FOAF_NICK + "> ?nick }} .\n" +
        "  { ?x <" + FOAF_NAME + "> ?name OPTIONAL { ?x <" + FOAF_MBOX + "> ?mbox }}\n" +
        "}";
    public static final String QUERY_OPTIONAL_5 = createSelectClause() +
        "WHERE  { \n" +
        "  { ?x <" + FOAF_NAME + "> ?name OPTIONAL { ?x <" + FOAF_NICK + "> ?nick }} OPTIONAL\n" +
        "  { ?x <" + FOAF_NAME + "> ?name OPTIONAL { ?x <" + FOAF_MBOX + "> ?mbox }}\n" +
        "}";
    public static final String QUERY_BOOK_1_AND_2_WITH_PREFIX = "PREFIX examplebook: <http://example.org/book/> \n" +
        "PREFIX dc: <http://purl.org/dc/elements/1.1/> \n" +
        createSelectClause() +
        "WHERE { examplebook:book1 dc:title ?title . examplebook:book2 dc:title ?title }";
    public static final String QUERY_SINGLE_OPTIONAL = createSelectClause() +
        "WHERE { { ?x <" + FOAF_NAME + "> ?name } OPTIONAL { ?x <" + FOAF_NICK + "> ?nick } }";
    public static final Expression<ExpressionVisitor> BOOK_1_DC_TITLE_ID_1 = createBookDcTitleExpression(URI_BOOK_1, 1);
    public static final Expression<ExpressionVisitor> BOOK_2_DC_TITLE_ID_1 = createBookDcTitleExpression(URI_BOOK_2, 1);
    public static final Expression<ExpressionVisitor> BOOK_2_DC_TITLE_ID_2 = createBookDcTitleExpression(URI_BOOK_2, 2);
    public static final Expression<ExpressionVisitor> BOOK_3_DC_TITLE_ID_3 = createBookDcTitleExpression(URI_BOOK_3, 3);
    public static final Expression<ExpressionVisitor> ANY_SPO = createConstraintExpression("s", "p", "o");

    private SparqlQueryTestUtil() {
    }

    private static String createQueryString(String subjectUri, String predicateUri, String objectVariable) {
        return createQueryString(new String[]{subjectUri, predicateUri, objectVariable});
    }

    private static String createQueryString(String[] constraints) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(createSelectClause());
        buffer.append(createWhereClause(constraints));
        return buffer.toString();
    }

    private static String createSelectClause() {
        return "SELECT * ";
    }

    private static String createWhereClause(String[] constraints) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("WHERE  { ");
        for (int i = 0; i < (constraints.length / 3); i++) {
            buffer.append(createConstraint(constraints, i));
            appendAnd(i, constraints, buffer);
        }
        buffer.append(" }");
        return buffer.toString();
    }

    private static String createConstraint(String[] constraints, int i) {
        StringBuffer buffer = new StringBuffer();
        String uriConstant1 = constraints[i * 3];
        String uriConstant2 = constraints[i * 3 + 1];
        String variable = constraints[i * 3 + 2];
        buffer.append(createTriple(uriConstant1, uriConstant2, variable));
        return buffer.toString();
    }

    private static String createTriple(String uriConstant1, String uriConstant, String variable) {
        StringBuffer buffer = new StringBuffer();
        String str1 = delimitUri(uriConstant1);
        String str2 = delimitUri(uriConstant);
        buffer.append(str1).append(" ").append(str2).append(" ").append(variable);
        return buffer.toString();
    }

    private static String delimitUri(String str) {
        return "<" + str + ">";
    }

    private static void appendAnd(int i, String[] constraints, StringBuffer buffer) {
        if (i < (constraints.length / 3 - 1)) {
            buffer.append(" . ");
        }
    }
}
