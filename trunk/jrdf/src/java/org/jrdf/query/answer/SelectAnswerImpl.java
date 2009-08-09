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

package org.jrdf.query.answer;

import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.util.EqualsUtil;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of {@link Answer}.
 *
 * @version $Revision$
 */
public final class SelectAnswerImpl implements SelectAnswer, Serializable {
    private static final long serialVersionUID = 3778815984074679718L;
    private Set<Attribute> heading;
    private long timeTaken;
    private boolean hasProjected;
    private LinkedHashSet<String> vars = new LinkedHashSet<String>();
    private transient EvaluatedRelation results;

    public SelectAnswerImpl(LinkedHashSet<Attribute> newHeading, EvaluatedRelation newResults,
        long timeTaken, boolean hasProjected) {
        checkNotNull(newHeading, newResults);
        this.heading = newHeading;
        this.results = newResults;
        this.timeTaken = timeTaken;
        this.hasProjected = hasProjected;
        for (final Attribute attribute : heading) {
            vars.add(attribute.getAttributeName().getLiteral());
        }
    }

    public String[] getVariableNames() {
        String[] resultColumnNames = new String[heading.size()];
        int index = 0;
        for (Attribute attribute : heading) {
            resultColumnNames[index] = attribute.getAttributeName().getLiteral();
            index++;
        }
        return resultColumnNames;
    }

    public LinkedHashSet<String> getNewVariableNames() {
        return vars;
    }

    public Iterator<TypeValue[]> columnValuesIterator() {
        return new AnswerIterator(heading, results.iterator());
    }

    public long numberOfTuples() {
        return results.getTupleSize();
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String[] columnNames = getVariableNames();
        String[][] columnValues = new TypeValueToStringImpl().convert(this);
        printColumns(builder, columnNames);
        printRows(builder, columnNames, columnValues);
        return builder.toString();
    }

    private void printColumns(StringBuilder builder, String[] columnNames) {
        builder.append("{ ");
        for (int cols = 0; cols < columnNames.length; cols++) {
            builder.append(columnNames[cols]);
            if (cols < columnNames.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(" }\n");
    }

    private void printRows(StringBuilder builder, String[] columnNames, String[][] columnValues) {
        for (int i = 0; i < numberOfTuples(); i++) {
            builder.append("{ ");
            for (int cols = 0; cols < columnNames.length; cols++) {
                builder.append(columnValues[i][cols]);
                if (cols < columnNames.length - 1) {
                    builder.append(", ");
                }
            }
            builder.append(" }\n");
        }
    }

    @Override
    public int hashCode() {
        return (int) (results.hashCode() ^ timeTaken);
    }

    @Override
    public boolean equals(Object obj) {
        if (EqualsUtil.isNull(obj)) {
            return false;
        }
        if (EqualsUtil.sameReference(this, obj)) {
            return true;
        }
        if (EqualsUtil.differentClasses(SelectAnswerImpl.class, obj.getClass())) {
            return false;
        }
        return determineEqualityFromFields((SelectAnswerImpl) obj);
    }

    private boolean determineEqualityFromFields(SelectAnswerImpl answer) {
        if (answer.getTimeTaken() == getTimeTaken()) {
            if (answer.results.equals(results)) {
                return true;
            }
        }
        return false;
    }

    private void writeObject(ObjectOutputStream output) throws IOException {
        output.writeObject(heading);
        output.writeLong(timeTaken);
        output.writeBoolean(hasProjected);
        Set<Attribute> attributes = results.getHeading();
        output.writeInt(attributes.size());
        for (Attribute attribute : attributes) {
            output.writeObject(attribute);
        }
        Set<Tuple> tuples = results.getTuples();
        output.writeInt(tuples.size());
        for (Tuple tuple : tuples) {
            output.writeObject(tuple);
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        RelationFactory relationFactory = new QueryFactoryImpl().createRelationFactory();
        Set<Attribute> newAttributes = new HashSet<Attribute>();
        Set<Tuple> newTuples = new HashSet<Tuple>();
        heading = (LinkedHashSet<Attribute>) input.readObject();
        timeTaken = input.readLong();
        hasProjected = input.readBoolean();
        int noAttributes = input.readInt();
        for (int i = 0; i < noAttributes; i++) {
            newAttributes.add((Attribute) input.readObject());
        }
        int noTuples = input.readInt();
        for (int i = 0; i < noTuples; i++) {
            newTuples.add((Tuple) input.readObject());
        }
        results = relationFactory.getRelation(newAttributes, newTuples);
    }

    public <R> R accept(AnswerVisitor<R> visitor) {
        return visitor.visitSelectAnswer(this);
    }
}
