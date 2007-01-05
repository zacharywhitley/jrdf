/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.sparql.analysis;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.PredicateObjectNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.query.relation.type.SubjectObjectNodeType;
import org.jrdf.query.relation.type.SubjectPredicateNodeType;
import org.jrdf.query.relation.type.SubjectPredicateObjectNodeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeCollectorImpl implements VariableCollector {
    private Map<AttributeName, NodeType> variables = new HashMap<AttributeName, NodeType>();

    public void addConstraints(List<AttributeValuePair> avps) {
        for (AttributeValuePair avp : avps) {
            Attribute attribute = avp.getAttribute();
            if (attribute.getAttributeName() instanceof VariableName) {
                if (variables.containsKey(attribute.getAttributeName())) {
                    updateEntry(attribute);
                } else {
                    addNewEntry(attribute);
                }
            } else {
                addNewEntry(attribute);
            }
        }
    }

    public Map<AttributeName, NodeType> getAttributes() {
        return variables;
    }

    private void updateEntry(Attribute newAttribute) {
        AttributeName key = newAttribute.getAttributeName();
        NodeType currentEntry = variables.get(key);
        Class<? extends NodeType> currentClazz = currentEntry.getClass();
        Class<? extends NodeType> newClazz = newAttribute.getType().getClass();
        if (!currentClazz.equals(newClazz)) {
            upgradeNodeType(currentClazz, newClazz, key);
        }
    }

    private void upgradeNodeType(Class<? extends NodeType> currentClazz, Class<? extends NodeType> newClazz,
            AttributeName key) {
        if (currentClazz.equals(SubjectNodeType.class)) {
            upgradeSubjectNodeType(newClazz, key);
        } else if (currentClazz.equals(PredicateNodeType.class)) {
            upgradePredicateNodeType(newClazz, key);
        } else if (currentClazz.equals(ObjectNodeType.class)) {
            upgradeObjectNodeType(newClazz, key);
        } else if (currentClazz.equals(SubjectPredicateNodeType.class)) {
            upgradeSubjectPredicateNodeType(newClazz, key);
        } else if (currentClazz.equals(SubjectObjectNodeType.class)) {
            upgradeSubjectObjectNodeType(newClazz, key);
        } else if (currentClazz.equals(PredicateObjectNodeType.class)) {
            upgradePredicateObjectNodeType(newClazz, key);
        }
    }

    private void upgradePredicateObjectNodeType(Class<? extends NodeType> newClazz, AttributeName key) {
        if (newClazz.equals(SubjectNodeType.class)) {
            variables.put(key, new SubjectPredicateObjectNodeType());
        }
    }

    private void upgradeSubjectObjectNodeType(Class<? extends NodeType> newClazz, AttributeName key) {
        if (newClazz.equals(PredicateNodeType.class)) {
            variables.put(key, new SubjectPredicateObjectNodeType());
        }
    }

    private void upgradeSubjectPredicateNodeType(Class<? extends NodeType> newClazz, AttributeName key) {
        if (newClazz.equals(ObjectNodeType.class)) {
            variables.put(key, new SubjectPredicateObjectNodeType());
        }
    }

    private void upgradeObjectNodeType(Class<? extends NodeType> newClazz, AttributeName key) {
        if (newClazz.equals(SubjectNodeType.class)) {
            variables.put(key, new SubjectObjectNodeType());
        } else if (newClazz.equals(PredicateNodeType.class)) {
            variables.put(key, new PredicateObjectNodeType());
        }
    }

    private void upgradePredicateNodeType(Class<? extends NodeType> newClazz, AttributeName key) {
        if (newClazz.equals(SubjectNodeType.class)) {
            variables.put(key, new SubjectPredicateNodeType());
        } else if (newClazz.equals(ObjectNodeType.class)) {
            variables.put(key, new SubjectObjectNodeType());
        }
    }

    private void upgradeSubjectNodeType(Class<? extends NodeType> newClazz, AttributeName key) {
        if (newClazz.equals(PredicateNodeType.class)) {
            variables.put(key, new SubjectPredicateNodeType());
        } else if (newClazz.equals(ObjectNodeType.class)) {
            variables.put(key, new SubjectObjectNodeType());
        }
    }

    private void addNewEntry(Attribute attribute) {
        variables.put(attribute.getAttributeName(), attribute.getType());
    }
}
