/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.gui.view;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * Display and Update results to a table.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class ResultsTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -7636712377178626351L;
    private String[] columnNames = {"Subject", "Predicate", "Object"};
    private String[][] data = {};
    private int dataIndex;

    public void setResults(Relation answer) {
        updateTableData(answer);
        fireTableDataChanged();
        fireTableStructureChanged();
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    private void updateTableData(Relation answer) {
        SortedSet<Tuple> sortedTuples = answer.getSortedTuples();
        SortedSet<Attribute> sortedHeading = answer.getSortedHeading();
        setColumnNames(sortedHeading);
        setColumnValues(sortedHeading, sortedTuples);
    }

    private void setColumnValues(SortedSet<Attribute> sortedHeading, SortedSet<Tuple> sortedTuples) {
        data = new String[sortedTuples.size()][sortedHeading.size()];
        dataIndex = 0;
        Attribute[] attributes = sortedHeading.toArray(new Attribute[]{});
        for (Tuple sortedTuple : sortedTuples) {
            AttributeValuePair[] avps = sortedTuple.getSortedAttributeValues().toArray(new AttributeValuePair[]{});
            setDataWithValues(avps, attributes);
        }
    }

    private void setDataWithValues(AttributeValuePair[] avps, Attribute[] attributes) {
        List<String> results = new ArrayList<String>();
        int returnedValues = 0;
        int headingIndex = 0;
        while (returnedValues < avps.length) {
            AttributeValuePair avp = avps[returnedValues];
            if (avp.getAttribute().equals(attributes[headingIndex])) {
                results.add(avp.getValue().toString());
                returnedValues++;
            } else {
                results.add("");
            }
            headingIndex++;
        }
        data[dataIndex++] = results.toArray(new String[]{});
    }

    private void setColumnNames(SortedSet<Attribute> sortedHeading) {
        List<String> resultColumnNames = new ArrayList<String>();
        for (Attribute attribute : sortedHeading) {
            resultColumnNames.add(attribute.getAttributeName().getLiteral() + " | " +
                    attribute.getType().getName());
        }
        columnNames = resultColumnNames.toArray(new String[]{});
    }
}
