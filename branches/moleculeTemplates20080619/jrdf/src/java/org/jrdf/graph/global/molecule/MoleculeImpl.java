/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.graph.global.molecule;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MoleculeImpl implements Molecule {
    // TODO This should probably be a set of molecules not triples.  For blank nodes that link to another set of blank
    // nodes
    // ie. _1 a b, _1 c _2, _2 a b, _2 c d, _2 e _3, _3 f g
    private final SortedSet<Triple> triples;
    private final TripleComparator comparator;
    private final Map<PredicateNode, SubjectNode> predicateSubjectMap;
    private final Map<PredicateNode, ObjectNode> predicateObjectMap;

    public MoleculeImpl(TripleComparator newComparator) {
        checkNotNull(newComparator);
        comparator = newComparator;
        triples = new TreeSet<Triple>(newComparator);
        predicateSubjectMap = new HashMap<PredicateNode, SubjectNode>();
        predicateObjectMap = new HashMap<PredicateNode, ObjectNode>();
    }

    public MoleculeImpl(Set<Triple> newTriples, TripleComparator newComparator) {
        this(newComparator);
        triples.addAll(newTriples);
        initMaps();
    }

    private void initMaps() {
        for (Triple triple : triples) {
            PredicateNode predicate = triple.getPredicate();
            predicateSubjectMap.put(predicate, triple.getSubject());
            predicateObjectMap.put(predicate, triple.getObject());
        }
    }

    public Triple getHeadTriple() {
        return triples.last();
    }

    public Molecule remove(Triple triple) {
        SortedSet<Triple> newTriples = new TreeSet<Triple>(comparator);
        newTriples.addAll(triples);
        newTriples.remove(triple);

        return new MoleculeImpl(newTriples, comparator);
    }

    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        if (isBlankNode(subject)) {
            return hasBlankSubject(object, predicate);
        } else {
            return hasFixedSubject(object, subject, predicate);
        }
    }

    private boolean hasFixedSubject(ObjectNode object, SubjectNode subject, PredicateNode predicate) {
        if (isBlankNode(object)) {
            return predicateSubjectMap.containsValue(subject) && predicateObjectMap.containsKey(predicate);
        } else {
            return predicateSubjectMap.containsKey(predicate) && predicateSubjectMap.containsValue(subject) &&
                predicateObjectMap.containsValue(object);
        }
    }

    private boolean hasBlankSubject(ObjectNode object, PredicateNode predicate) {
        if (isBlankNode(object)) {
            return predicateSubjectMap.containsKey(predicate);
        } else {
            return predicateSubjectMap.containsKey(predicate) && predicateObjectMap.containsValue(object);
        }
    }

    public boolean contains(Triple triple) {
        return contains(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    public Iterator<Triple> iterator() {
        return triples.iterator();
    }

    public SortedSet<Triple> getTriples() {
        return triples;
    }

    public Iterator<Triple> tailTriples() {
        return getTailTriples().iterator();
    }

    public LinkedHashSet<Triple> getTailTriples() {
        LinkedHashSet<Triple> set = new LinkedHashSet<Triple>();
        Iterator<Triple> iterator = triples.iterator();
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            if (iterator.hasNext()) {
                set.add(triple);
            }
        }
        return set;
    }

    public Molecule add(Triple triple) {
        SortedSet<Triple> newTriples = new TreeSet<Triple>(comparator);
        newTriples.addAll(triples);
        newTriples.add(triple);
        return new MoleculeImpl(newTriples, comparator);
    }

    public Molecule add(Set<Triple> set) {
        Set<Triple> newTriples = new TreeSet<Triple>(comparator);
        newTriples.addAll(this.triples);
        for (Triple triple : set) {
            newTriples.add(triple);
        }
        return new MoleculeImpl(newTriples, comparator);
    }

    public int size() {
        return triples.size();
    }

    public boolean equals(Object obj) {

        // Check equal by reference
        if (this == obj) {
            return true;
        }

        // Check for null and ensure exactly the same class - not subclass.
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast and check for equality by value. (same class)
        try {
            MoleculeImpl tmpMolecule = (MoleculeImpl) obj;
            return tmpMolecule.triples.equals(triples);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    public int hashCode() {
        return triples != null ? triples.hashCode() : 0;
    }

    public String toString() {
        final StringBuilder res = new StringBuilder();
        final Iterator<Triple> allTriples = iterator();
        res.append("{\n");
        if (allTriples.hasNext()) {
            while (allTriples.hasNext()) {
                final Triple t = allTriples.next();
                printTriple(res, t);
            }
        } else {
            res.append("EMPTY");
        }
        res.append("}");
        return res.toString();
    }

    private void printTriple(StringBuilder res, Triple t) {
        res.append("[");
        res.append(appendNode(t.getSubject())).append(", ");
        res.append(appendNode(t.getPredicate())).append(", ");
        res.append(appendNode(t.getObject()));
        res.append("]");
        res.append('\n');
    }

    private String appendNode(Node node) {
        if (isBlankNode(node)) {
            //return "_";
            return node.toString();
        } else {
            return node.toString();
        }
    }

    private boolean isBlankNode(Node node) {
        return BlankNode.class.isAssignableFrom(node.getClass());
    }
}