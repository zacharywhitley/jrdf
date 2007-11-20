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

package org.jrdf.graph.global.iterator;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.graph.global.GlobalizedGraph;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeIteratorFactory;

/**
 * Reperesnts a facotry for creating non-empty iterators over
 * a globalized graph.
 * <p/>
 * User: imrank
 * Date: 13/09/2007
 * Time: 16:19:38
 */
public class NonEmptyIteratorFactory {
    private final MoleculeIteratorFactory iteratorFactory;

    public NonEmptyIteratorFactory(MoleculeIteratorFactory moleculeIteratorFactory) {
        this.iteratorFactory = moleculeIteratorFactory;
    }

    public ClosableIterator<Molecule> getIterator(SubjectNode subj, PredicateNode pred, ObjectNode obj) {
        ClosableIterator<Molecule> result = null;
        if (ANY_SUBJECT_NODE != subj) {
            // {s??} Get fixed subject, fixed or any predicate and object.
            result = fixedSubjectIterator(subj, pred, obj);
        } else if (ANY_PREDICATE_NODE != pred) {
            // {*p?} Get any subject, fixed predicate, fixed or any object.
            result = anySubjectFixedPredicateIterator(subj, pred, obj);
        } else if (ANY_OBJECT_NODE != obj) {
            // {**o} Get any subject and predicate, fixed object.
            result = anySubjectAndPredicateFixedObjectIterator(obj);
        } else {
            // {***} Get all.
            result = iteratorFactory.globalizedGraphIterator();
        }
        return result;
    }

    private ClosableIterator<Molecule> anySubjectAndPredicateFixedObjectIterator(Node obj) {
        // got {**o}
        return iteratorFactory.newOneFixedIterator(obj, GlobalizedGraph.OBJECT_INDEX);
    }

    private ClosableIterator<Molecule> anySubjectFixedPredicateIterator(SubjectNode subj, PredicateNode pred,
                                                                        ObjectNode obj) {
        ClosableIterator<Molecule> result = null;
        // test for {*p?}
        if (ANY_OBJECT_NODE != obj) {
            // got {*po}
            result = iteratorFactory.newTwoFixedIterator(pred, obj, GlobalizedGraph.PREDICATE_INDEX);
        } else {
            // got {*p*}.
            result = iteratorFactory.newOneFixedIterator(pred, GlobalizedGraph.PREDICATE_INDEX);
        }

        return result;
    }

    private ClosableIterator<Molecule> fixedSubjectIterator(SubjectNode subj, PredicateNode pred, ObjectNode obj) {
        ClosableIterator<Molecule> result;
        // test for {s??}
        if (ANY_PREDICATE_NODE != pred) {
            // test for {sp?}
            if (ANY_OBJECT_NODE != obj) {
                // got {spo}
                result = iteratorFactory.newThreeFixedIterator(subj, pred, obj);
            } else {
                // got {sp*}
                result = iteratorFactory.newTwoFixedIterator(subj, pred, GlobalizedGraph.SUBJECT_INDEX);
            }
        } else {
            // test for {s*?}
            if (ANY_OBJECT_NODE != obj) {
                // got s*o {}
                result = iteratorFactory.newTwoFixedIterator(obj, subj, GlobalizedGraph.OBJECT_INDEX);
            } else {
                // got {s**}
                result = iteratorFactory.newOneFixedIterator(subj, GlobalizedGraph.SUBJECT_INDEX);
            }
        }

        return result;
    }


}
