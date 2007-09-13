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

package org.jrdf.graph.global;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.index.MoleculeIndex;
import org.jrdf.graph.global.index.OSPMoleculeIndexMem;
import org.jrdf.graph.global.index.POSMoleculeIndexMem;
import org.jrdf.graph.global.index.SPOMoleculeIndexMem;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeIteratorFactory;
import org.jrdf.graph.global.molecule.MoleculeIteratorFactoryImpl;
import org.jrdf.util.ClosableIterator;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractGlobalizedGraph implements GlobalizedGraph {
    /**
     * Position in the index for SPO.
     */
    private static final int SUBJECT_INDEX = 0;

    /**
     * Position in the index for POS.
     */
    private static final int PREDICATE_INDEX = 1;

    /**
     * Position in the index for OSP.
     */
    private static final int OBJECT_INDEX = 2;

    /**
     * The SPO index of the molecules.
     */
    private MoleculeIndex moleculeIndexSPO;

    /**
     * The POS index of the molecules.
     */
    private transient MoleculeIndex moleculeIndexPOS;

    /**
     * The OSP index of the molecules.
     */
    private transient MoleculeIndex moleculeIndexOSP;

    /**
     * Collection of the 3 indexes.
     */
    protected transient MoleculeIndex[] indexes;

    /**
     * Factory for managing the creation of various iterators.
     */
    protected MoleculeIteratorFactory iteratorFactory;

    protected final TripleComparator tripleComparator;

    public AbstractGlobalizedGraph(MoleculeIndex[] newIndexes, MoleculeIteratorFactory newIteratorFactory,
        TripleComparator newTripleComparator) {
        this.indexes = newIndexes;
        this.iteratorFactory = newIteratorFactory;
        this.tripleComparator = newTripleComparator;
        init();
    }

    protected void init() {
        initIndexes();
        if (iteratorFactory == null) {
            iteratorFactory = new MoleculeIteratorFactoryImpl();
        }
    }

    private void initIndexes() {
        // Fix up creation of SortedSet - add a Triple comparator - probably GroundedTripleComparator.
        if (null == moleculeIndexSPO) {
            moleculeIndexSPO = new SPOMoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Molecule>>>());
        }
        if (null == moleculeIndexPOS) {
            moleculeIndexPOS = new POSMoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Molecule>>>());
        }
        if (null == moleculeIndexOSP) {
            moleculeIndexOSP = new OSPMoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Molecule>>>());
        }
        indexes = new MoleculeIndex[]{moleculeIndexSPO, moleculeIndexPOS, moleculeIndexOSP};
    }

    protected ClosableIterator<Molecule> findValue(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    protected boolean containsValue(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        boolean res;
        if (ANY_SUBJECT_NODE != subject) {
            res = containsFixedSubject(subject, predicate, object);
        } else {
            res = containsAnySubject(predicate, object);
        }
        return res;
    }

    private boolean containsAnySubject(PredicateNode predicate, ObjectNode object) {
        boolean res;
        if (ANY_PREDICATE_NODE == predicate) {
            res = containsAnySubjectAnyPredicate(object);
        } else if (ANY_OBJECT_NODE == object) {
            res = containsAnySubjectAnyObject(predicate);
        } else {
            res = indexes[OBJECT_INDEX].contains(object);
        }
        return res;
    }

    private boolean containsAnySubjectAnyObject(PredicateNode predicate) {
        return indexes[PREDICATE_INDEX].contains(predicate);
    }

    private boolean containsAnySubjectAnyPredicate(ObjectNode object) {
        if (object == ANY_OBJECT_NODE) {
            return indexes[SUBJECT_INDEX].getNumberOfTriples() > 0;
        } else {
            return indexes[OBJECT_INDEX].contains(object);
        }
    }

    private boolean containsFixedSubject(SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        if (indexes[SUBJECT_INDEX].contains(subject)) {
            if (ANY_PREDICATE_NODE != predicate) {
                return containsFixedSubjectFixedPredicate(subject, predicate, object);
            } else {
                return containsFixedSubjectAnyPredicate(subject, object);
            }
        } else {
            return false;
        }
    }

    private boolean containsFixedSubjectAnyPredicate(SubjectNode subject, ObjectNode object) {
        if (ANY_OBJECT_NODE == object) {
            return true;
        } else {
            Map<Node, Map<Node, Molecule>> objIndex = indexes[OBJECT_INDEX].getSubIndex(object);
            return null != objIndex && null != objIndex.get(subject);
        }
    }

    private boolean containsFixedSubjectFixedPredicate(SubjectNode subjectNode, PredicateNode predicate,
        ObjectNode object) {
        boolean res = false;
        Map<Node, Map<Node, Molecule>> subjIndex = indexes[SUBJECT_INDEX].getSubIndex(subjectNode);
        Map<Node, Molecule> subjPredIndex = subjIndex.get(predicate);
        if (null != subjPredIndex) {
            res = ANY_OBJECT_NODE == object || subjPredIndex.containsKey(object);
        }
        return res;
    }
}
