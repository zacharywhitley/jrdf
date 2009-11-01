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

package org.jrdf.query.execute;

import org.jrdf.graph.Node;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Yuan-Fang Li
 * @version $Id: $
 */

public class ConstraintTupleCacheHandlerImpl implements ConstraintTupleCacheHandler {
    private static final int VAR_PRESET = 2;
    private static final int LOAD_FACTOR = 10;
    private static final int DEFAULT_LIMIT = 1000;

    private Map<AttributeName, Set<Node>> cache;
    private long timeStamp;
    private int cacheLimit;

    public ConstraintTupleCacheHandlerImpl() {
        cache = new HashMap<AttributeName, Set<Node>>();
        timeStamp = System.currentTimeMillis();
        cacheLimit = DEFAULT_LIMIT;
    }

    public void reset(EvaluatedRelation result, int constraintListSize) {
        clear();
        long tupleSize = estimateTupleSize(result);
        this.cacheLimit = calculateCacheSize(tupleSize, constraintListSize);
    }

    private long estimateTupleSize(EvaluatedRelation result) {
        return result.getTupleSize();
    }

    public Set<Node> getCachedValues(AttributeName name) {
        return cache.get(name);
    }

    private int calculateCacheSize(long tupleSize, int constraintSize) {
        return Math.max(DEFAULT_LIMIT, (int) tupleSize / (constraintSize * VAR_PRESET * LOAD_FACTOR));
    }

    public void clear() {
        for (AttributeName name : cache.keySet()) {
            Set<Node> vo = cache.get(name);
            vo.clear();
            vo = null;
        }
        cache.clear();
    }

    private void clear(AttributeName name) {
        Set<Node> set = cache.remove(name);
        if (set != null) {
            set.clear();
            set = null;
        }
    }

    private void setTimeStamp(long time) {
        timeStamp = time;
    }

    public Attribute findOneCachedAttribute(SingleConstraint constraint) {
        Set<Attribute> attributes = constraint.getHeadings();
        Map<Integer, Attribute> map = new TreeMap<Integer, Attribute>();
        for (Attribute attribute : attributes) {
            AttributeName attributeName = attribute.getAttributeName();
            if (attributeName instanceof VariableName && getCachedValues(attributeName) != null) {
                map.put(getCachedValues(attributeName).size(), attribute);
            }
        }
        if (map.isEmpty()) {
            return null;
        } else {
            return map.entrySet().iterator().next().getValue();
        }
    }

    public  void addResultToCache(SingleConstraint constraint, EvaluatedRelation result, long time) {
        if (result.getTupleSize() < cacheLimit) {
            Set<Attribute> attributes = constraint.getHeadings();
            Set<Attribute> resultAttributes = result.getHeading();
            Set<Attribute> set = findMatchingAttributes(attributes, resultAttributes);
            for (Attribute attribute : set) {
                updateCache(result, time, attribute);
            }
        }
    }

    private void updateCache(EvaluatedRelation result, long time, Attribute attribute) {
        AttributeName attributeName = attribute.getAttributeName();
        Set<Node> voSet = getMatchingVOs(attribute, getTuples(result, attribute));
        Set<Node> cached = cache.get(attributeName);
        if (time > timeStamp) {
            timeStamp = time;
        }
        if (cached != null) {
            voSet.retainAll(cached);
        }
        clear(attributeName);
        cache.put(attributeName, voSet);
    }

    public Set<Tuple> getTuples(Relation relation, Attribute attribute) {
        Set<Tuple> set = new HashSet<Tuple>();
        for (Tuple tuple : relation) {
            if (tuple.getValue(attribute) != null) {
                set.add(tuple);
            }
        }
        return set;
    }

    private Set<Node> getMatchingVOs(Attribute attribute, Set<Tuple>tupleSet) {
        Set<Node> set = new HashSet<Node>();
        for (Tuple tuple : tupleSet) {
            set.add(tuple.getValue(attribute));
        }
        return set;
    }

    private Set<Attribute> findMatchingAttributes(Set<Attribute> source, Set<Attribute> target) {
        Set<Attribute> set = new HashSet<Attribute>();
        for (Attribute attr : target) {
            if (matches(source, attr)) {
                set.add(attr);
            }
        }
        return set;
    }

    private boolean matches(Set<Attribute> source, Attribute attr) {
        AttributeName name = attr.getAttributeName();
        for (Attribute attr1 : source) {
            AttributeName name1 = attr1.getAttributeName();
            if (name instanceof VariableName && name.equals(name1)) {
                return true;
            }
        }
        return false;
    }
}