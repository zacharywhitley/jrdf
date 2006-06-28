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

package org.jrdf.util.test;

import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import static org.jrdf.util.test.TripleTestUtil.*;

/**
 * Artefacts used in tests.
 *
 * @author Tom Adams
 * @author Andrew Newman
 * @version $Revision$
 */
public final class SparqlQueryTestUtil {

    public static final String VARIABLE_PREFIX = "?";
    public static final String VARIABLE_NAME_TITLE = "title";
    public static final String VARIABLE_NAME_SUBJECT = "subject";
    public static final String VARIABLE_TITLE = VARIABLE_PREFIX + VARIABLE_NAME_TITLE;
    private static final String SUBJECT_URI_1 = URI_BOOK_1.toString();
    private static final String SUBJECT_URI_2 = URI_BOOK_2.toString();
    private static final String PREDICATE_URI_1 = URI_DC_TITLE.toString();
    public static final String QUERY_BOOK_1_DC_TITLE =
            createQueryString(SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE);
    public static final String QUERY_BOOK_2_DC_TITLE =
            createQueryString(SUBJECT_URI_2, PREDICATE_URI_1, VARIABLE_TITLE);
    public static final String QUERY_BOOK_1_AND_2 =
            createQueryString(new String[]{SUBJECT_URI_1, PREDICATE_URI_1, VARIABLE_TITLE, SUBJECT_URI_2,
                    PREDICATE_URI_1, VARIABLE_TITLE});
    public static final Expression<ExpressionVisitor> BOOK_1_DC_TITLE = createBookDcTitleExpression(URI_BOOK_1);
    public static final Expression<ExpressionVisitor> BOOK_2_DC_TITLE = createBookDcTitleExpression(URI_BOOK_2);

    private SparqlQueryTestUtil() {
    }

    private static String createQueryString(String subjectUri, String predicateUri, String objectVariable) {
        return createQueryString(new String[]{subjectUri, predicateUri, objectVariable});
    }

    private static String createQueryString(String[] constraints) {
        StringBuffer buffer = new StringBuffer("SELECT * WHERE  { ");
        for (int i = 0; i < (constraints.length / 3); i++) {
            createConstraint(buffer, constraints, i);
            appendAnd(i, constraints, buffer);
        }
        buffer.append(" }");
        return buffer.toString();
    }

    private static void createConstraint(StringBuffer buffer, String[] constraints, int i) {
        buffer.append(delimitUri(constraints[i * 3])).append(" ").append(delimitUri(constraints[i * 3 + 1]))
                .append(" ").append(constraints[i * 3 + 2]);
    }

    private static void appendAnd(int i, String[] constraints, StringBuffer buffer) {
        if (i < (constraints.length / 3 -1)) {
            buffer.append(" . ");
        }
    }

    public static String delimitUri(String str) {
        return "<" + str + ">";
    }

}
