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

import org.jrdf.graph.Node;
import org.jrdf.graph.global.index.MoleculeIndex;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.util.ClosableIterator;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Iterator over molecules in a globalized graph, where one of the nodes in a triple is fixed.
 * User: imrank
 * Date: 14/09/2007
 * Time: 15:34:50
 */
public class OneFixedIterator implements ClosableIterator<Molecule> {
    private final MoleculeIndex index;
    private boolean hasNext;
    private Iterator<Node> objectIterator;
    private Molecule currentMolecule;
    private Iterator<Node> predicateIterator;
    private Map<Node, Molecule> subIndex;
    private final Map<Node, Map<Node, Molecule>> index1;


    public OneFixedIterator(Node first, MoleculeIndex[] indexes, int searchIndex) {
        index = indexes[searchIndex];

        index1 = index.getSubIndex(first);
        predicateIterator = index1.keySet().iterator();

        while (predicateIterator.hasNext()) {
            Node predicate = predicateIterator.next();

            //map of objects
            subIndex = index1.get(predicate);
            objectIterator = subIndex.keySet().iterator();

            if (objectIterator.hasNext()) {
                hasNext = true;
                break;
            }
        }

    }

    public boolean close() {
        return true;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public Molecule next() {
        if (currentMolecule == null) {
            throw new NoSuchElementException();
        }
        if (predicateIterator.hasNext()) {
            if (objectIterator.hasNext()) {
                Node node = objectIterator.next();
                currentMolecule = subIndex.get(node);
            } else {
                if (resetIterators()) {
                    return next();
                }
            }
        } else {
            currentMolecule = null;
            return next();
        }

        return currentMolecule;
    }

    private boolean resetIterators() {
        Node predicate = predicateIterator.next();
        subIndex = index1.get(predicate);
        objectIterator = subIndex.keySet().iterator();

        //nothing exists in object iterator
        if (!objectIterator.hasNext()) {
            return true;
        } else {
            Node node = objectIterator.next();
            currentMolecule = subIndex.get(node);
        }
        return false;
    }

    public void remove() {
    }
}
