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

package org.jrdf.graph.global.molecule.mem.template;

import org.jrdf.graph.Triple;
import org.jrdf.graph.global.molecule.TriplePattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A strict matcher that attempts to match every triple pattern in the molecule template to a triple
 * in the triple iterator.
 * TODO: 1. For the moment, do not cater for repeated triple patterns (triple*)
 * TODO: 2. Need a way to capture matching nodes in triple patterns...
 *
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: May 28, 2008
 * Time: 3:48:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class MoleculeTemplateMatcherImpl implements MoleculeTemplateMatcher {
    private MoleculeTemplate template;
    private Set<Triple> set;
    private Iterator<Triple> iterator;
    private Iterator<TriplePattern> tpIterator;
    private List<Triple> list;

    public MoleculeTemplateMatcherImpl(MoleculeTemplate template, Set<Triple> triples) {
        this.template = template;
        this.set = triples;
        this.tpIterator = this.template.iterator();
        list = new ArrayList<Triple>();
    }

    /**
     * Return a list of triples in depth-first manner.
     * If cannot find a match, return null list.
     * @return a list of triples in depth-first manner.
     */
    public List<Triple> matches() {
        while (tpIterator.hasNext()) {
            iterator = set.iterator();
            TriplePattern pattern = tpIterator.next();
            boolean matches = matchATriplePattern(pattern);
            if (!matches) {
                return null;
            }
        }
        return list;
    }

    private boolean matchATriplePattern(TriplePattern pattern) {
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            if (pattern.matches(triple)) {
                addToList(triple);
                return true;
            }
        }
        return false;
    }

    private void addToList(Triple triple) {
        list.add(triple);
        set.remove(triple);
        tpIterator.remove();
    }
}