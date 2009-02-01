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

import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Relation;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public final class RelationHelperImpl implements RelationHelper, Serializable {
    private static final long serialVersionUID = -2830411524131840319L;
    private AttributeComparator attributeComparator;
    private NodeComparator nodeComparator;

    private RelationHelperImpl() {
    }

    public RelationHelperImpl(AttributeComparator attributeComparator, NodeComparator nodeComparator) {
        checkNotNull(attributeComparator, nodeComparator);
        this.attributeComparator = attributeComparator;
        this.nodeComparator = nodeComparator;
    }

    public SortedSet<Attribute> getHeadingUnions(Relation... relations) {
        TreeSet<Attribute> attributes = new TreeSet<Attribute>(attributeComparator);
        for (Relation relation : relations) {
            attributes.addAll(relation.getHeading());
        }
        return attributes;
    }

    public SortedSet<Attribute> getHeadingIntersections(Relation... relations) {
        TreeSet<Attribute> attributes = new TreeSet<Attribute>(attributeComparator);
        attributes.addAll(relations[0].getSortedHeading());
        for (int i = 1; i < relations.length; i++) {
            Relation relation = relations[i];
            attributes.retainAll(relation.getSortedHeading());
        }
        return attributes;
    }

    public boolean processAttributeValues(Attribute attribute, Node lhs, Node rhs, 
        Map<Attribute, Node> resultantAttributeValues) {
        if (lhs.hashCode() == rhs.hashCode() && nodeComparator.compare(lhs, rhs) == 0) {
            resultantAttributeValues.put(attribute, lhs);
            return false;
        }
        return true;
    }
}
