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

package org.jrdf.graph.global.molecule.mem;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.global.molecule.MergeSubmolecules;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LocalMergeSubmoleculesImpl implements LocalMergeSubmolecules {
    private final MergeSubmolecules merger;
    private final MoleculeFactory moleculeFactory;
    private Map<BlankNode, BlankNode> map;

    public LocalMergeSubmoleculesImpl(MergeSubmolecules merger, MoleculeFactory moleculeFactory) {
        this.merger = merger;
        this.moleculeFactory = moleculeFactory;
    }

    public Molecule merge(Molecule molecule1, Molecule molecule2, Map<BlankNode, BlankNode> map) {
        if (!map.isEmpty()) {
            this.map = map;
            return merger.merge(convertMolecule(molecule1), convertMolecule(molecule2));
        } else {
            return null;
        }
    }

    private Molecule convertMolecule(Molecule molecule) {
        final Molecule newMolecule = convertRootTriples(molecule);
        final Iterator<Triple> triples = molecule.getRootTriples();
        while (triples.hasNext()) {
            final Triple triple = triples.next();
            final Set<Molecule> moleculeSet = molecule.getSubMolecules(triple);
            for (final Molecule subMolecule : moleculeSet) {
                final Molecule convertedSubMolecule = convertMolecule(subMolecule);
                newMolecule.add(convertTriple(triple), convertedSubMolecule);
            }
        }
        return newMolecule;
    }

    private Molecule convertRootTriples(Molecule molecule) {
        Set<Triple> triples = new HashSet<Triple>();
        final Iterator<Triple> iterator = molecule.getRootTriples();
        while (iterator.hasNext()) {
            final Triple triple = iterator.next();
            triples.add(convertTriple(triple));
        }
        return moleculeFactory.createMolecule(triples);
    }

    private Triple convertTriple(Triple triple) {
        SubjectNode subject = triple.getSubject();
        PredicateNode predicate = triple.getPredicate();
        ObjectNode object = triple.getObject();
        if (map.containsKey(subject)) {
            subject = map.get(subject);
        }
        if (map.containsKey(object)) {
            object = map.get(object);
        }
        return new TripleImpl(subject, predicate, object);
    }
}
