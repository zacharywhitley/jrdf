/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query;

import junit.framework.TestCase;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;

import java.lang.reflect.Modifier;

/**
 * Unit test for {@link SelectQueryImpl}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class SelectQueryImplUnitTest extends TestCase {

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Query.class, SelectQueryImpl.class);
        checkConstructor(SelectQueryImpl.class, Modifier.PUBLIC, Expression.class, GraphRelationFactory.class);
    }

    public void testNullsInConstructorThrowException() {
        assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SelectQueryImpl(null, null);
            }
        });
    }

    /*public static void testPerstMoleculeGraph() throws InvalidQuerySyntaxException, GraphException {
        DirectoryHandler handler = new TempDirectoryHandler("perstMoleculeGraph");
        final PersistentGlobalJRDFFactory factory = PersistentGlobalJRDFFactoryImpl.getFactory(handler);
        MoleculeGraph graph = factory.getGraph("perstMoleculeGraph");
        System.err.println("graph # = " + graph.getNumberOfTriples());
        UrqlConnection connection = factory.getNewUrqlConnection();
        String query =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX biopax: <http://www.biopax.org/release/biopax-level2.owl#> \n" +
                "PREFIX biomanta: <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#> \n" +
                "PREFIX ncbi: <http://biomanta.sourceforge.net/2007/10/ncbi_taxo.owl#> \n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
                "SELECT ?name ?id \n" +
                "WHERE { { ?x rdf:type biopax:physicalEntity . \n" +
                "        ?x biomanta:fromNCBISpecies ncbi:ncbi_taxo_4932_ind . \n" +
                "        ?x biomanta:hasPrimaryRef ?y . \n" +
                "        ?y biopax:DB ?db \n" +
                "        FILTER (str(?db) = \"uniprotkb\"^^xsd:string) } . \n" +
                "        \n" +
                "        { ?y biopax:ID ?id . \n" +
                "        { ?y biopax:ID ?id . \n" +
                "        FILTER (str(?id) = \"o13516\"^^xsd:string) } . \n" +
                "        ?x biomanta:hasFullName ?name }";
        Answer answer = connection.executeQuery(graph, query);
        System.err.println("answer time taken = " + answer.getTimeTaken());
        System.err.println("answer # = " + answer.numberOfTuples());
        System.err.println(answer);
        graph.close();
        factory.close();
    }*/
}
