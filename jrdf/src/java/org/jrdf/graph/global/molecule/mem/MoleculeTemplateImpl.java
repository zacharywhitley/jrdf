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

import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.molecule.TriplePattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: May 27, 2008
 * Time: 3:23:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MoleculeTemplateImpl implements MoleculeTemplate {
    private Graph graph;
    private TripleComparator comparator;
    private Map<TriplePattern, List<MoleculeTemplate>> subMolecules;

    public MoleculeTemplateImpl(Graph g, TripleComparator comparator) {
        graph = g;
        this.comparator = comparator;
        subMolecules = new LinkedHashMap<TriplePattern, List<MoleculeTemplate>>();
    }

    public void setHeadTriple(TriplePattern triplePattern) throws Exception {
        TriplePattern currentHead = this.getHeadTriple();
        if (currentHead != null) {
            throw new Exception("Head triple already inserted: " + currentHead);
        }
        List<MoleculeTemplate> subs = subMolecules.get(triplePattern);
        subs = createEmptySubMoleculeTemplate(subs);
        addATriplePatternWithSub(triplePattern, subs);
    }

    private void addATriplePatternWithSub(TriplePattern triplePattern, List<MoleculeTemplate> subs) throws Exception {
        if (!TriplePattern.checkTriplesNotNull(triplePattern)) {
            throw new Exception("New triple cannot be null.");
        }
        subMolecules.put(triplePattern, subs);
    }

    private List<MoleculeTemplate> createEmptySubMoleculeTemplate(List<MoleculeTemplate> subs) {
        if (subs == null) {
            subs = new ArrayList<MoleculeTemplate>();
        }
        return subs;
    }

    /**
     * Respecting the order of the triple patterns, adding them as root triples of the molecule template.
     *
     * @param triplePatterns
     */
    public void addRootTriple(TriplePattern... triplePatterns) throws Exception {
        for (TriplePattern triplePattern : triplePatterns) {
            List<MoleculeTemplate> subs = subMolecules.get(triplePattern);
            subs = createEmptySubMoleculeTemplate(subs);
            addATriplePatternWithSub(triplePattern, subs);
        }
    }

    public TriplePattern getHeadTriple() {
        if (subMolecules.size() > 0) {
            return subMolecules.keySet().iterator().next();
        } else {
            return null;
        }
    }

    public Set<TriplePattern> getRootTriples() {
        if (subMolecules.size() > 0) {
            return subMolecules.keySet();
        } else {
            return null;
        }
    }

    public List<MoleculeTemplate> getSubMoleculeTemplate(TriplePattern headTriple) {
        return subMolecules.get(headTriple);
    }

    public void add(TriplePattern triplePattern, MoleculeTemplate subMolecule) throws Exception {
        List<MoleculeTemplate> subs = subMolecules.get(triplePattern);
        subs = createEmptySubMoleculeTemplate(subs);
        subs.add(subMolecule);
        addATriplePatternWithSub(triplePattern, subs);
    }

    public void remove(TriplePattern triplePattern) {
        if (triplePattern == null) {
            return;
        }
        Set<TriplePattern> roots = subMolecules.keySet();
        for (TriplePattern root : roots) {
            if (root.equals(triplePattern)) {
                subMolecules.remove(root);
                break;
            }
        }
    }

    public MoleculeTemplateMatcher matcher(Iterator<Triple> triples) {
        MoleculeTemplateMatcher matcher = new MoleculeTemplateMatcherImpl(this, triples);
        return matcher;
    }

    public boolean hasSubMolecules() {
        if (subMolecules == null || subMolecules.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Return an iterator of triple patterns in a depth-first manner.
     * @return
     */
    public Iterator<TriplePattern> iterator() {
        List<TriplePattern> patterns = new Vector<TriplePattern>();
        patterns = addTriplePatternsForMolecule(patterns, this);
        return patterns.iterator();
    }

    private List<TriplePattern> addTriplePatternsForMolecule(List<TriplePattern> patterns, MoleculeTemplate mt) {
        final Set<TriplePattern> roots = mt.getRootTriples();
        if (roots == null) {
            return patterns;
        }
        for (TriplePattern tp : roots) {
            patterns.add(tp);
            final List<MoleculeTemplate> subMolecules = mt.getSubMoleculeTemplate(tp);
            if (subMolecules != null) {
                for (MoleculeTemplate subMT : subMolecules) {
                    patterns = addTriplePatternsForMolecule(patterns, subMT);
                }
            }
        }
        return patterns;
    }
}
