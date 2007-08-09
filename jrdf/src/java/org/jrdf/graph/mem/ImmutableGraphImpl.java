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

package org.jrdf.graph.mem;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.mem.iterator.EmptyClosableIterator;
import org.jrdf.graph.mem.iterator.IteratorFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.util.ClosableIterator;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Set;
import java.util.Map;

public class ImmutableGraphImpl implements ImmutableGraph {
    private NodePool nodePool;
    private LongIndex longIndex012;
    private LongIndex longIndex120;
    private LongIndex longIndex201;
    private IteratorFactory iteratorFactory;

    public ImmutableGraphImpl(NodePool nodePool, LongIndex longIndex012, LongIndex longIndex120,
                              LongIndex longIndex201, IteratorFactory newIteratorFactory) {
        checkNotNull(nodePool, longIndex012, longIndex120, longIndex201, newIteratorFactory);
        this.nodePool = nodePool;
        this.longIndex012 = longIndex012;
        this.longIndex120 = longIndex120;
        this.longIndex201 = longIndex201;
        this.iteratorFactory = newIteratorFactory;
    }
    public boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        try {
            // Get local node values
            Long[] values = nodePool.localize(subject, predicate, object);
            return containsValues(values, subject, predicate, object);
        } catch (GraphException ge) {
            // Graph exception on localize implies that the subject, predicate or
            // object did not exist in the graph.
            return false;
        }
    }

    public ClosableIterator<Triple> find(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        // Get local node values
        Long[] values;
        try {
            values = nodePool.localize(subject, predicate, object);
        } catch (GraphException ge) {
            // A graph exception implies that the subject, predicate or object does
            // not exist in the graph.
            return new EmptyClosableIterator();
        }

        return findNonEmptyIterator(subject, predicate, object, values);
    }

    public long getSize() {
        return longIndex012.getSize();
    }

    private boolean containsValues(Long[] values, SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        if (ANY_SUBJECT_NODE != subject) {
            // subj, *, *
            return containsFixedSubject(values, predicate, object);
        } else {
            // AnySubjectNode, *, *
            return containsAnySubject(values, predicate, object);
        }
    }

    private boolean containsFixedSubject(Long[] values, PredicateNode predicate, ObjectNode object) {
        if (longIndex012.contains(values[0])) {
            if (ANY_PREDICATE_NODE != predicate) {
                // subj, pred, AnyObjectNode or subj, pred, obj
                return containsFixedSubjectFixedPredicate(values, object);
            } else {
                // subj, AnyPredicateNode, AnyObjectNode or subj, AnyPredicateNode, obj.
                return containsFixedSubjectAnyPredicate(values, object);
            }
        } else {
            // If subject not found return false.
            return false;
        }
    }

    private boolean containsFixedSubjectFixedPredicate(Long[] values, ObjectNode object) {
        Map<Long, Set<Long>> subjIndex = longIndex012.getSubIndex(values[0]);
        Set<Long> subjPredIndex = subjIndex.get(values[1]);
        if (null != subjPredIndex) {
            if (ANY_OBJECT_NODE != object) {
                // Must be subj, pred, obj.
                return subjPredIndex.contains(values[2]);
            } else {
                // Was subj, pred, AnyObjectNode - must be true if we get this far.
                return true;
            }
        } else {
            // subj, pred not found.
            return false;
        }
    }

    private boolean containsFixedSubjectAnyPredicate(Long[] values, ObjectNode object) {
        if (ANY_OBJECT_NODE != object) {
            // Was subj, AnyPredicateNode, obj
            // Use 201 index to find object and then subject.
            Map<Long, Set<Long>> objIndex = longIndex201.getSubIndex(values[2]);
            if (null != objIndex) {
                // Find object.
                return null != objIndex.get(values[0]);
            } else {
                // Didn't find subject.
                return false;
            }
        } else {
            // Was subj, AnyPredicate, AnyObject
            // If its AnyObjectNode then we've found all we need to find.
            return true;
        }
    }

    private boolean containsAnySubject(Long[] values, PredicateNode predicate, ObjectNode object) {
        if (ANY_PREDICATE_NODE != predicate) {
            return containsAnySubjectAnyPredicate(values, object);
        } else {
            // AnySubjectNode, AnyPredicateNode, obj.
            return longIndex201.contains(values[2]);
        }
    }

    private boolean containsAnySubjectAnyPredicate(Long[] values, ObjectNode object) {
        // AnySubjectNode, pred, AnyObjectNode or AnySubjectNode, pred, obj.
        Map<Long, Set<Long>> predIndex = longIndex120.getSubIndex(values[1]);
        if (null != predIndex) {
            if (ANY_OBJECT_NODE != object) {
                // Was AnySubjectNode, pred, obj
                return null != predIndex.get(values[2]);
            } else {
                // If the object is any object node and we found the predicate return true.
                return true;
            }
        } else {
            // If predicate not found return false.
            return false;
        }
    }

    private ClosableIterator<Triple> findNonEmptyIterator(SubjectNode subject, PredicateNode predicate,
        ObjectNode object, Long[] values) {
        ClosableIterator<Triple> result;
        if (ANY_SUBJECT_NODE != subject) {
            // {s??} Get fixed subject, fixed or any predicate and object.
            result = fixedSubjectIterator(values, predicate, object);
        } else if (ANY_PREDICATE_NODE != predicate) {
            // {*p?} Get any subject, fixed predicate, fixed or any object.
            result = anySubjectFixedPredicateIterator(values, object);
        } else if (ANY_OBJECT_NODE != object) {
            // {**o} Get any subject and predicate, fixed object.
            result = anySubjectAndPredicateFixedObjectIterator(values);
        } else {
            // {***} Get all.
            result = iteratorFactory.newGraphIterator();
        }
        return result;
    }

    private ClosableIterator<Triple> fixedSubjectIterator(Long[] values, PredicateNode predicate, ObjectNode object) {
        ClosableIterator<Triple> result;
        // test for {s??}
        if (ANY_PREDICATE_NODE != predicate) {
            // test for {sp?}
            if (ANY_OBJECT_NODE != object) {
                // got {spo}
                result = iteratorFactory.newThreeFixedIterator(values);
            } else {
                // got {sp*}
                result = iteratorFactory.newTwoFixedIterator(values[0], values[1], 0);
            }
        } else {
            // test for {s*?}
            if (ANY_OBJECT_NODE != object) {
                // got s*o {}
                result = iteratorFactory.newTwoFixedIterator(values[2], values[0], 2);
            } else {
                // got {s**}
                result = iteratorFactory.newOneFixedIterator(values[0], 0);
            }
        }

        return result;
    }

    private ClosableIterator<Triple> anySubjectFixedPredicateIterator(Long[] values, ObjectNode object) {
        ClosableIterator<Triple> result;
        // test for {*p?}
        if (ANY_OBJECT_NODE != object) {
            // got {*po}
            result = iteratorFactory.newTwoFixedIterator(values[1], values[2], 1);
        } else {
            // got {*p*}.
            result = iteratorFactory.newOneFixedIterator(values[1], 1);
        }

        return result;
    }

    private ClosableIterator<Triple> anySubjectAndPredicateFixedObjectIterator(Long[] values) {
        // got {**o}
        return iteratorFactory.newOneFixedIterator(values[2], 2);
    }
}
