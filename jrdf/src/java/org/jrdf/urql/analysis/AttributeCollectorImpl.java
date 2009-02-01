/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.urql.analysis;

import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.constants.NullaryAttribute;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.PositionalNodeType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A variable collector takes the attribute value pairs from constraints and add them to a map.  Used to construct
 * queries.
 *
 * As node types are gathered they are upgraded to compount types if they have stood in different positions in a
 * constraint.  For example ?uri <> <> . <> ?uri <> would create a attribute ?uri which of of type
 * SubjectPredicateNodeType.
 *
 * @author Andrew Newman
 * @version $Revision: 1078 $
 */
public class AttributeCollectorImpl implements VariableCollector {
    private static final long serialVersionUID = 5588873511780742278L;
    private transient Map<AttributeName, PositionalNodeType> variables
        = new HashMap<AttributeName, PositionalNodeType>();

    public void addConstraints(final Map<Attribute, Node> avps) {
        for (final Attribute attribute : avps.keySet()) {
            checkAndAddEntry(attribute);
        }
    }

    public Map<AttributeName, PositionalNodeType> getAttributes() {
        return variables;
    }

    @Override
    public String toString() {
        return variables.toString();
    }

    private void checkAndAddEntry(final Attribute attribute) {
        if (attribute.getAttributeName() instanceof VariableName) {
            if (variables.containsKey(attribute.getAttributeName())) {
                updateEntry(attribute);
            } else {
                addNewEntry(attribute);
            }
        } else if (!NullaryAttribute.isNullaryAttribute(attribute)) {
            addNewEntry(attribute);
        }
    }

    private void updateEntry(final Attribute newAttribute) {
        final AttributeName key = newAttribute.getAttributeName();
        final PositionalNodeType currentEntry = variables.get(key);
        final NodeType type = newAttribute.getType();
        if (type instanceof PositionalNodeType) {
            if (!currentEntry.getClass().equals(type.getClass())) {
                variables.put(key, currentEntry.upgrade((PositionalNodeType) type));
            }
        }
    }

    private void addNewEntry(final Attribute attribute) {
        final NodeType nodeType = attribute.getType();
        if (nodeType instanceof PositionalNodeType) {
            variables.put(attribute.getAttributeName(), (PositionalNodeType) nodeType);
        }
    }

    private void writeObject(ObjectOutputStream output) throws IOException {
        Set<Map.Entry<AttributeName, PositionalNodeType>> entries = variables.entrySet();
        output.writeInt(variables.size());
        for (Map.Entry<AttributeName, PositionalNodeType> entry : entries) {
            output.writeObject(entry.getKey());
            output.writeObject(entry.getValue());
        }
    }

    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        variables = new HashMap<AttributeName, PositionalNodeType>();
        int elements = input.readInt();
        for (int i = 0; i < elements; i++) {
            AttributeName attribute = (AttributeName) input.readObject();
            PositionalNodeType node = (PositionalNodeType) input.readObject();
            variables.put(attribute, node);
        }
    }

}
