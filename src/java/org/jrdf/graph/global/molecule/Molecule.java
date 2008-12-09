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

import org.jrdf.graph.Triple;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * A molecule is a container of a subgraph of RDF triples.  It contains a collection of triples that share some common
 * chain of blank nodes.  Molecules consist of a set of root triples.  These share a blank node - typically the
 * subject but it can be the object as well.  For example, _1 p o, s p2 _1 is a simple molecule.
 *
 * The highest in this set of root triples (as defined by some ordering) is the head triple.  The typical ordering is
 * most grounded (fewest blank nodes), followed by URIs, Literals and then alphabetical ordering.
 *
 *  The chain of blank nodes creates a molecule that contains submolecules.  If a triple in the root has a blank node
 * as its object it is a linking triple.  This is how the chains to submolecules are created.  From one molecule to
 * the next the object and subject nodes are swapped.  For example, the linking triple _1 p _2 will link to a triple
 * in a submolecule _2 p o.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public interface Molecule {
    /**
     * Add a triple to the root set of triples.
     *
     * The molecule may not be returned in future versions (and current implementations just mutate as well as return
     * a copy).
     *
     * @param triple the triple to add.
     * @return a new molecule with the added triple.
     */
    Molecule add(Triple triple);

    /**
     * Add a submolecule to a molecule using the given root triple.
     *
     * The molecule may not be returned in future versions (and current implementations just mutate as well as return
     * a copy).
     *
     * @param linkingTriple the root triple to hang the submolecule off of.
     * @param subMolecule the submolecule to add.
     * @return a new molecule with the submolecule added.
     */
    Molecule add(Triple linkingTriple, Molecule subMolecule);

    /**
     * Add a triple to a submolecule which has the given linking triple.
     *
     * The molecule may not be returned in future versions (and current implementations just mutate as well as return
     * a copy).
     *
     * @param linkingTriple the linking triple.
     * @param subMoleculeTriple the triple of the submolecule.
     * @return a new molecule with the submolecule added.
     */
    Molecule add(Triple linkingTriple, Triple subMoleculeTriple);

    /**
     * Checks for existence of a triple in the root triples.
     *
     * @param triple the triple to check for existance.
     * @return if the triple exists in the root triple.
     */
    boolean contains(Triple triple);

    /**
     * Returns the highest triple (as defined by some ordering).  Typically, this is the most grounded (least number
     * of blank nodes) and alphabetically highest triple in the root triples.
     *
     * @return the highest, by order, triple.
     */
    Triple getHeadTriple();

    /**
     * Returns an iterator of all the triples in the root set.
     *
     * @return an iterator of all the triples in the root set.
     */
    Iterator<Triple> getRootTriples();

    /**
     * Creates an (usually) in memory version of the root set of triples.
     *
     * @return the root set of triples.
     */
    Set<Triple> getRootTriplesAsSet();

    /**
     * Returns a set of submolecules for a given root triple.
     *
     * @param rootTriple the root triple to use to get the submolecules.
     * @return a set of submolecules for a given root triple.
     */
    SortedSet<Molecule> getSubMolecules(Triple rootTriple);

    /**
     * Remove a triple from the root set of triples.
     *
     * @param triple the triple to remove.
     */
    void remove(Triple triple);

    /**
     * Returns the number of triples in the molecule.
     *
     * @return the number of triples in the molecule.
     */
    int size();

    /**
     * Returns true if this molecule has no parent molecule (it's not a submolecule).
     *
     * @return if this molecule has no parent molecule (it's not a submolecule).
     */
    boolean isTopLevelMolecule();

    /**
     * Adds a molecule based on head triple matching.  If the head triples of this molecule and the one to be added
     * matches it takes the submolecules and adds it to the current head triple's submolecule.  Otherwise, if the head
     * triples don't match, it just adds the submolecule to the head triple's submolecule with no merging.
     *
     * This maybe removed in the future.
     *
     * @param merger the code which merges the two submolecules together.
     * @param subMolecule the submolecule to add.
     * @return a new molecule with the submolecule added.
     */
    Molecule add(MergeSubmolecules merger, Molecule subMolecule);

    /**
     * Copies the top level (from the root triples) of the given molecule into this molecule.
     *
     * This maybe removed in the future.
     *
     * @param molecule the molecule to add to this one.
     */
    void specialAdd(Molecule molecule);

    /**
     * Removes the submolecule from the triple.  The set of submolecules must contain be equal to the given molecule.
     *
     * This maybe removed in the future.
     *
     * @param triple The triple which contains the submolecules.
     * @param subMolecule The submolecule to remove.
     * @return if the submolecule was removed.
     */
    boolean removeMolecule(Triple triple, Molecule subMolecule);

    Iterator<Triple> iterator();

    Iterator<Triple> find(Triple triple);

    Iterator<Triple> find(SubjectNode subject, PredicateNode predicateNode, ObjectNode object);
}
