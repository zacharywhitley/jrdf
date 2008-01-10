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

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * A Collection all the statements of a particular group.
 *
 * @author Imran Khan
 * @version $Revision: 1226 $
 */
public interface Molecule {

    /**
     * Checks to see if the given triple exists within the molecule.
     *
     * @param triple the triple to search for - does not currently support ANY_SUBJECT, etc.
     *
     * @return true if found.
     */
    boolean contains(Triple triple);

    /**
     * Checks to see if the given triple exists within the molecule.
     *
     * @param subject the subject to search for - does not currently support ANY_SUBJECT.
     * @param predicate the predicate to search for - does not currently support ANY_PREDICATE.
     * @param object the object to search for - does not currently support ANY_OBJECT.
     *
     * @return true if found.
     */
    boolean contains(SubjectNode subject, PredicateNode predicate, ObjectNode object);

    /**
     * Returns an iterator for the set of triples which make up this molecule.
     *
     * @return all the triples in the molecule.
     */
    Iterator<Triple> iterator();

    /**
     * Adds the given triple to the molecule.
     *
     * @param triple the triple to add.
     * @return a new molecule based on the current one plus the new triple.
     */
    Molecule add(Triple triple);

    /**
     * Adds a set of triples to this molecule.
     *
     * @param triples the set of triples.
     * @return a new molecule based on the current one plus the new triples.
     */
    Molecule add(Set<Triple> triples);

    /**
     * Number of triples contained in the molecule.
     *
     * @return the number of triples contains in the molecule.
     */
    int size();

    /**
     * Returns the head triple of the molecule.
     *
     * @return the head triple of the molecule.
     */
    Triple getHeadTriple();


    /**
     * An iterator that contains tail triples i.e. all triples except head triple.
     *
     * @return the iterator of tail triples.
     */
    Iterator<Triple> tailTriples();

    /**
     * Removes a triple from the molecule.
     *
     * @param triple the triple to remove.
     * @return a new Molecule that contains all triples except the one removed.
     */
    Molecule remove(Triple triple);

    /**
     * Returns all triples in the molecule.
     *
     * @return all triples in the molecule.
     */
    SortedSet<Triple> getTriples();
}
