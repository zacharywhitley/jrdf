/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

import junit.framework.TestCase;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMultiLevelMolecule;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R2B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R2;
import org.jrdf.graph.global.molecule.mem.MoleculeTraverserImpl;
import static org.jrdf.util.test.SetUtil.asSet;

import java.util.Collections;

public class MoleculeToTextUnitTest extends TestCase {
    private static final String RESULT_1 = "\n[\n" +
            "  _:a1 <urn:foo> <urn:foo> .\n" +
            "]";
    private static final String RESULT_2 = "\n[\n" +
            "  _:a1 <urn:foo> _:a2 .\n" +
            "  [\n" +
            "    _:a2 <urn:bar> _:a3 .\n" +
            "    [\n" +
            "      _:a3 <urn:bar> <urn:bar> .\n" +
            "      _:a3 <urn:bar> <urn:baz> .\n" +
            "    ]\n" +
            "    _:a2 <urn:bar> <urn:foo> .\n" +
            "    <urn:foo> <urn:bar> _:a2 .\n" +
            "  ]\n" +
            "  _:a1 <urn:bar> <urn:bar> .\n" +
            "  _:a1 <urn:foo> <urn:foo> .\n" +
            "]";
    private static final MoleculeTraverser TRAVERSER = new MoleculeTraverserImpl();
    private MoleculeHandler handler;

    public void testSingleMolcule() throws GraphException {
        Molecule molecule = createMultiLevelMolecule(asSet(B1R1R1), Collections.<Triple>emptySet(),
            Collections.<Triple>emptySet());
        checkMolecule(molecule, RESULT_1);
    }

    public void testMultlevelMolcule() throws GraphException {
        Molecule molecule = createMultiLevelMolecule(asSet(B1R1R1, B1R2R2, B1R1B2),
            asSet(R1R2B2, B2R2R1, B2R2B3), asSet(B3R2R3, B3R2R2));
        checkMolecule(molecule, RESULT_2);
    }

    private void checkMolecule(Molecule molecule, String expectedResult) {
        StringBuilder builder = new StringBuilder();
        handler = new MoleculeToText(builder);
        TRAVERSER.traverse(molecule, handler);
        assertEquals(expectedResult, builder.toString());
    }
}
