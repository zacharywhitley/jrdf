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

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.util.EqualsUtil;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;

/**
 * Default implementation of {@link Answer}.
 *
 * @version $Revision$
 */
public final class AnswerImpl implements Answer, Serializable {
    private static final long serialVersionUID = 3778815984074679718L;
    private final LinkedHashSet<Attribute> headings;
    private final Relation results;
    private final long timeTaken;

    public AnswerImpl(Query query, Relation results, long timeTaken) {
        checkNotNull(query, results);
        this.timeTaken = timeTaken;
        this.results = results;
        this.headings = getHeading(query);
    }

    private LinkedHashSet<Attribute> getHeading(Query query) {
        LinkedHashSet<Attribute> heading;
        List<Attribute> variables = query.getVariables();
        if (variables.size() == 0) {
            SortedSet<Attribute> sortedHeading = results.getSortedHeading();
            heading = new LinkedHashSet<Attribute>(sortedHeading);
        } else {
            heading = new LinkedHashSet<Attribute>(variables);
        }
        return heading;
    }

    public Relation getResults() {
        return results;
    }

    public String[] getColumnNames() {
        String[] resultColumnNames = new String[headings.size()];
        int index = 0;
        for (Attribute attribute : headings) {
            resultColumnNames[index] = attribute.getAttributeName().getLiteral() + " | " +
                    attribute.getType().getName();
            index++;
        }
        return resultColumnNames;
    }

    public String[][] getColumnValues() {
        SortedSet<Tuple> sortedTuples = results.getSortedTuples();
        String table[][] = new String[sortedTuples.size()][headings.size()];
        int index = 0;
        for (Tuple sortedTuple : sortedTuples) {
            SortedSet<AttributeValuePair> avps = sortedTuple.getSortedAttributeValues();
            table[index] = getDataWithValues(avps);
            index++;
        }
        return table;
    }

    public long numberOfTuples() {
        return results.getTuples().size();
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public int hashCode() {
        return (int) (results.hashCode() ^ timeTaken);
    }

    public boolean equals(Object obj) {
        if (EqualsUtil.isNull(obj)) {
            return false;
        }
        if (EqualsUtil.sameReference(this, obj)) {
            return true;
        }
        if (!EqualsUtil.hasSuperClassOrInterface(Answer.class, obj)) {
            return false;
        }
        return determineEqualityFromFields((Answer) obj);
    }

    private String[] getDataWithValues(SortedSet<AttributeValuePair> avps) {
        String[] results = new String[headings.size()];
        int index = 0;
        for (Attribute headingAttribute : headings) {
            boolean foundValue = false;
            for (AttributeValuePair avp : avps) {
                if (avp.getAttribute().equals(headingAttribute)) {
                    results[index] = avp.getValue().toString();
                    foundValue = true;
                }
            }
            if (!foundValue) {
                results[index] = "";
            }
            index++;
        }
        return results;
    }

    private boolean determineEqualityFromFields(Answer answer) {
        if (answer.getTimeTaken() == getTimeTaken()) {
            if (answer.getResults().equals(getResults())) {
                return true;
            }
        }
        return false;
    }
}
