/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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

package org.jrdf.query.relation.mem;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.mem.TripleImpl;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Allows the creation of sroted AttributeValuePairs.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class SortedAttributeValuePairHelperImpl implements SortedAttributeValuePairHelper {
    private final SortedAttributeFactory sortedAttributeFactory;
    private final AttributeValuePairComparator avpComparator;
    private static final int TRIPLES = 3;

    public SortedAttributeValuePairHelperImpl(SortedAttributeFactory sortedAttributeFactory,
            AttributeValuePairComparator avpComparator) {
        this.sortedAttributeFactory = sortedAttributeFactory;
        this.avpComparator = avpComparator;
    }

    public SortedSet<AttributeValuePair> createAvp(Triple triple) {
        Attribute[] attributes = getAttributes();
        return createAttributeValuePairs(attributes, triple);
    }

    public SortedSet<AttributeValuePair> createAvp(Triple triple, Attribute[] attributes) {
        // TODO (AN) Check that there are only 3 attributes.
        return createAttributeValuePairs(attributes, triple);
    }

    public Attribute[] createAttributes(SortedSet<AttributeValuePair> nameValues) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (AttributeValuePair avp : nameValues) {
            attributes.add(avp.getAttribute());
        }
        return attributes.toArray(new Attribute[]{});
    }

    public SortedSet<AttributeValuePair> createAvp(AttributeValuePair[] attributeValuePairsArray) {
        TreeSet<AttributeValuePair> attributeValuePairs = new TreeSet<AttributeValuePair>(avpComparator);
        for (AttributeValuePair attributeValuePair : attributeValuePairsArray) {
            attributeValuePairs.add(attributeValuePair);
        }
        return attributeValuePairs;
    }

    public Triple createTriple(SortedSet<AttributeValuePair> avp) {
        throwIllegalArgumentExceptionIfNotThreeAttributeValuePairs(avp);
        return getNodes(avp);
    }

    private TreeSet<AttributeValuePair> createAttributeValuePairs(Attribute[] attributes, Triple triple) {
        AttributeValuePair subjectAv = new AttributeValuePairImpl(attributes[0], triple.getSubject());
        AttributeValuePair predicateAv = new AttributeValuePairImpl(attributes[1], triple.getPredicate());
        AttributeValuePair objectAv = new AttributeValuePairImpl(attributes[2], triple.getObject());
        TreeSet<AttributeValuePair> attributeValuePairs = new TreeSet<AttributeValuePair>(avpComparator);
        attributeValuePairs.add(subjectAv);
        attributeValuePairs.add(predicateAv);
        attributeValuePairs.add(objectAv);
        return attributeValuePairs;
    }

    private Attribute[] getAttributes() {
        SortedSet<Attribute> heading = sortedAttributeFactory.createHeading();
        Iterator<Attribute> iterator = heading.iterator();
        Attribute[] attributes = new Attribute[TRIPLES];
        attributes[0] = iterator.next();
        attributes[1] = iterator.next();
        attributes[2] = iterator.next();
        return attributes;
    }

    private void throwIllegalArgumentExceptionIfNotThreeAttributeValuePairs(Set<AttributeValuePair> nameValues) {
        if (nameValues.size() != TRIPLES) {
            throw new IllegalArgumentException("Can only get 3 tuples.");
        }
    }

    private Triple getNodes(SortedSet<AttributeValuePair> nameValues) {
        Iterator<AttributeValuePair> iterator = nameValues.iterator();
        SubjectNode subject = (SubjectNode) iterator.next().getValue();
        PredicateNode predicate = (PredicateNode) iterator.next().getValue();
        ObjectNode object = (ObjectNode) iterator.next().getValue();
        return new TripleImpl(subject, predicate, object);
    }
}
