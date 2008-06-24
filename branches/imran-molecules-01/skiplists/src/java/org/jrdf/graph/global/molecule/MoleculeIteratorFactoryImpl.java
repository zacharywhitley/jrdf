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

package org.jrdf.graph.global.molecule;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.index.MoleculeIndex;
import org.jrdf.graph.global.iterator.GlobalizedGraphIterator;
import org.jrdf.graph.global.iterator.OneFixedIterator;
import org.jrdf.graph.global.iterator.ThreeFixedIterator;
import org.jrdf.graph.global.iterator.TwoFixedIterator;
import org.jrdf.util.ClosableIterator;

public class MoleculeIteratorFactoryImpl implements MoleculeIteratorFactory {
    private final MoleculeIndex[] indexes;

    public MoleculeIteratorFactoryImpl(MoleculeIndex[] indexes) {
        this.indexes = indexes;
    }

    public ClosableIterator<Molecule> globalizedGraphIterator() {
        return new GlobalizedGraphIterator(indexes);
    }

    public ClosableIterator<PredicateNode> findUniquePredicates(Resource resource) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    public ClosableIterator<PredicateNode> getUniquePredicates() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    public ClosableIterator<Resource> getResources() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    public ClosableIterator<URIReference> getURIReferences() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    public ClosableIterator<BlankNode> getBlankNodes() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    public ClosableIterator<Molecule> newThreeFixedIterator(SubjectNode subjNode, PredicateNode predNode,
                                                            ObjectNode objNode) {
        return new ThreeFixedIterator(subjNode, predNode, objNode, indexes);
    }

    public ClosableIterator<Molecule> newTwoFixedIterator(Node first, Node second, int index) {
        return new TwoFixedIterator(first, second, indexes, index);
    }

    public ClosableIterator<Molecule> newOneFixedIterator(Node first, int index) {
        return new OneFixedIterator(first, indexes, index);
    }
}