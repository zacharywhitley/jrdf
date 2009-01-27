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

package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Allows the creation of headings.
 *
 * @author Andrew Newman
 */
// TODO (AN) 23 June - Test Drive me!!!
// TODO (AN) Make me final.
public class SortedAttributeFactoryImpl implements SortedAttributeFactory {
    private static final SubjectNodeType SUBJECT_TYPE = new SubjectNodeType();
    private static final PredicateNodeType PREDICATE_TYPE = new PredicateNodeType();
    private static final ObjectNodeType OBJECT_TYPE = new ObjectNodeType();
    private static final int THREE_TRIPLES = 3;
    private AttributeComparator attributeComparator;
    private long nameCounter;

    public SortedAttributeFactoryImpl(AttributeComparator attributeComparator, long nameCounter) {
        this.attributeComparator = attributeComparator;
        this.nameCounter = nameCounter;
    }

    public SortedSet<Attribute> createHeading() {
        return doCreateHeading(SUBJECT_TYPE, PREDICATE_TYPE, OBJECT_TYPE);
    }

    public List<Attribute> createHeading(List<NodeType> types) throws IllegalArgumentException {
        checkIsATriple(types);
        SortedSet<Attribute> attributes = doCreateHeading(types.get(0), types.get(1), types.get(2));
        return new ArrayList<Attribute>(attributes);
    }

    private SortedSet<Attribute> doCreateHeading(NodeType subjectType, NodeType predicateType, NodeType objectType) {
        SortedSet<Attribute> attributes = new TreeSet<Attribute>(attributeComparator);
        attributes.add(createAttribute(DEFAULT_SUBJECT_NAME, subjectType));
        attributes.add(createAttribute(DEFAULT_PREDICATE_NAME, predicateType));
        attributes.add(createAttribute(DEFAULT_OBJECT_NAME, objectType));
        nameCounter++;
        return attributes;
    }

    private void checkIsATriple(List<NodeType> types) {
        if (types.size() != THREE_TRIPLES) {
            throw new IllegalArgumentException("Must supply three node types");
        }
    }

    private Attribute createAttribute(String name, NodeType type) {
        PositionName positionName = new PositionName(name + nameCounter);
        return new AttributeImpl(positionName, type);
    }
}
