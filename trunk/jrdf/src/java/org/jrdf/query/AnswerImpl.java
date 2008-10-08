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

package org.jrdf.query;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.xml.AnswerXMLPagenatedStreamWriter;
import org.jrdf.query.xml.AnswerXMLWriter;
import org.jrdf.util.EqualsUtil;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Default implementation of {@link Answer}.
 *
 * @version $Revision$
 */
public final class AnswerImpl implements Answer, Serializable {
    private static final long serialVersionUID = 3778815984074679718L;
    private Set<Attribute> heading;
    private transient Relation results;
    private long timeTaken;
    private boolean hasProjected;
    private transient AnswerXMLWriter xmlWriter;

    private AnswerImpl() {
    }

    public AnswerImpl(LinkedHashSet<Attribute> newHeading, Relation newResults, long timeTaken, boolean hasProjected) {
        checkNotNull(newHeading, newResults);
        this.heading = newHeading;
        this.results = newResults;
        this.timeTaken = timeTaken;
        this.hasProjected = hasProjected;
    }

    public String[] getColumnNames() {
        String[] resultColumnNames = new String[heading.size()];
        int index = 0;
        for (Attribute attribute : heading) {
            AttributeName attributeName = attribute.getAttributeName();
            resultColumnNames[index] = attributeName.toString() + " | " + attribute.getType().getName();
            index++;
        }
        return resultColumnNames;
    }

    public String[][] getColumnValues() {
        SortedSet<Tuple> sortedTuples = results.getSortedTuples();
        String table[][] = new String[sortedTuples.size()][heading.size()];
        int index = 0;
        for (Tuple sortedTuple : sortedTuples) {
            Map<Attribute, ValueOperation> avps = sortedTuple.getAttributeValues();
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String[] columnNames = getColumnNames();
        String[][] columnValues = getColumnValues();
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
        if (EqualsUtil.differentClasses(AnswerImpl.class, obj.getClass())) {
            return false;
        }
        return determineEqualityFromFields((AnswerImpl) obj);
    }

    private String[] getDataWithValues(Map<Attribute, ValueOperation> avps) {
        String[] results = new String[heading.size()];
        int index = 0;
        for (Attribute headingAttribute : heading) {
            boolean foundValue = false;
            for (Attribute attribute : avps.keySet()) {
                if (attribute.equals(headingAttribute)) {
                    results[index] = avps.get(attribute).getValue().toString();
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

    private boolean determineEqualityFromFields(AnswerImpl answer) {
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

    @SuppressWarnings({"unchecked" })
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

    public AnswerXMLWriter getXMLWriter(Writer writer) throws XMLStreamException, IOException {
        if (xmlWriter == null) {
            xmlWriter = new AnswerXMLPagenatedStreamWriter(heading, results, writer);
        } else {
            xmlWriter.setWriter(writer);
        }
        return xmlWriter;
    }
}
