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

package org.jrdf.graph.molecule;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 7/09/2007
 * Time: 15:27:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractGlobalizedGraph implements GlobalizedGraph {
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
    static final int SUBJECT_INDEX = 0;
    static final int PREDICATE_INDEX = 1;
    static final int OBJECT_INDEX = 2;

    public AbstractGlobalizedGraph(MoleculeIndex[] newIndexes, MoleculeIteratorFactory newIteratorFactory) {
        this.indexes = newIndexes;
        this.iteratorFactory = newIteratorFactory;

        init();
    }

    protected void init() {
        initIndexes();
        if (iteratorFactory == null) {
            iteratorFactory = new MoleculeIteratorFactoryImpl();
        }
    }

    private void initIndexes() {
        if (null == moleculeIndexSPO) {
            moleculeIndexSPO = new MoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Set<Triple>>>>());
        }
        if (null == moleculeIndexPOS) {
            moleculeIndexPOS = new MoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Set<Triple>>>>());
        }
        if (null == moleculeIndexOSP) {
            moleculeIndexOSP = new MoleculeIndexMem(new HashMap<Node, Map<Node, Map<Node, Set<Triple>>>>());
        }

        indexes = new MoleculeIndex[]{moleculeIndexSPO, moleculeIndexPOS, moleculeIndexOSP};
    }

    protected boolean containsValue(Triple triple) {
        boolean res;
        if (ANY_SUBJECT_NODE != triple.getSubject()) {
            res = containsFixedSubject(triple);
        } else {
            res = containsAnySubject(triple);
        }
        return res;
    }

    private boolean containsAnySubject(Triple triple) {
        boolean res;
        if (ANY_PREDICATE_NODE != triple.getPredicate()) {
            res = containsAnySubjectAnyPredicate(triple);
        } else {
            res = indexes[OBJECT_INDEX].contains(triple.getObject());
        }
        return res;
    }

    private boolean containsAnySubjectAnyPredicate(Triple triple) {
        boolean res = false;
        PredicateNode predicate = triple.getPredicate();
        ObjectNode object = triple.getObject();
        Map<Node, Map<Node, Set<Triple>>> predIndex = indexes[PREDICATE_INDEX].getSubIndex(predicate);
        if (null != predIndex) {
            if (ANY_OBJECT_NODE != object) {
                res = (null != predIndex.get(object));
            } else {
                res = true;
            }
        }
        return res;
    }

    private boolean containsFixedSubject(Triple triple) {
        boolean res = false;
        if (indexes[SUBJECT_INDEX].contains(triple.getSubject())) {
            if (ANY_PREDICATE_NODE != triple.getPredicate()) {
                res = containsFixedSubjectFixedPredicate(triple);
            } else {
                res = containsFixedSubjectAnyPredicate(triple);
            }
        }
        return res;
    }

    private boolean containsFixedSubjectAnyPredicate(Triple triple) {
        boolean res;
        ObjectNode object = triple.getObject();
        SubjectNode subject = triple.getSubject();

        if (ANY_OBJECT_NODE != object) {
            Map<Node, Map<Node, Set<Triple>>> objIndex = indexes[OBJECT_INDEX].getSubIndex(object);
            if (null != objIndex) {
                res = (null != objIndex.get(subject));
            } else {
                res = false;
            }
        } else {
            res = true;
        }
        return res;
    }

    private boolean containsFixedSubjectFixedPredicate(Triple triple) {
        boolean res = false;
        SubjectNode first = triple.getSubject();
        PredicateNode predicate = triple.getPredicate();
        ObjectNode object = triple.getObject();
        Map<Node, Map<Node, Set<Triple>>> subjIndex = indexes[SUBJECT_INDEX].getSubIndex(first);
        Map<Node, Set<Triple>> subjPredIndex = subjIndex.get(predicate);

        if (null != subjPredIndex) {
            if (ANY_OBJECT_NODE != object) {
                res = subjPredIndex.containsKey(object);
            } else {
                res = true;
            }
        }
        return res;
    }
}
