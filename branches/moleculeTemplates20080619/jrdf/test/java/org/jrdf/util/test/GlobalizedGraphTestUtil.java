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

package org.jrdf.util.test;

import org.jrdf.graph.Literal;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.GlobalizedGraph;
import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.graph.global.URIReferenceImpl;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeImpl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * User: imrank
 * Date: 13/09/2007
 * Time: 14:59:19
 */
public class GlobalizedGraphTestUtil {
    public static final int NUMBER_OF_MOLECULES = 10;
    private static final String BASE_URL = "http://example.org/";
    private static final String URL1 = BASE_URL + "1";
    private static final String URL2 = BASE_URL + "2";
    private static final String LITERAL1 = "xyz";


    public static List<Triple> getHeadTriples() {
        List<Triple> headTriples = new ArrayList<Triple>();
        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            headTriples.add(createHeadTriple());
        }
        return headTriples;
    }

    public static Triple createHeadTriple() {
        double random = Math.random();

        return createTriple(URI.create(URL1 + random), URI.create(URL2), LITERAL1);
    }

    public static void addMolecules(List<Triple> headTriples, GlobalizedGraph globalizedGraph,
        TripleComparator comparator) {
        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            Molecule m = createMolecule(headTriples.get(i), comparator);
            globalizedGraph.add(m);
        }
    }

    public static Molecule createMolecule(Triple headTriple, TripleComparator comparator) {
        Molecule m = new MoleculeImpl(comparator);
        URIReference uriReference = (URIReference) headTriple.getSubject();

        Triple triple2 = createTriple(uriReference.getURI(), URI.create(URL1), LITERAL1);
        m = m.add(headTriple);
        m = m.add(triple2);
        return m;
    }

    public static Triple createTriple(URI subjURI, URI predURI, String lit) {
        URIReference subj = new URIReferenceImpl(subjURI);
        URIReferenceImpl pred = new URIReferenceImpl(predURI);
        Literal obj = new LiteralImpl(lit);

        Triple triple2 = new TripleImpl(subj, pred, obj);
        return triple2;
    }
}
