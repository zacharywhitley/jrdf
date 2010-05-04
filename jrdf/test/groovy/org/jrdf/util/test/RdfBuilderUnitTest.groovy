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

package org.jrdf.util.test

import org.jrdf.TestJRDFFactory
import org.jrdf.graph.Graph

class RdfBuilderUnitTest extends GroovyTestCase {

    void testCreateAndDoSimpleTriples() {
        Graph graph = TestJRDFFactory.getFactory().getGraph()
        def rdf = new RdfBuilder(graph)
        def urn = rdf.namespace("urn", "http://this/is/not/a/urn#");
        rdf.namespaces('xsd':'http://www.w3.org/2001/XMLSchema#', 'rdfs':'http://www.w3.org/2000/01/rdf-schema#')

        // Normal triples
        rdf.'urn:foo1' 'urn:bar':'urn:baz'

        // Blank nodes
        rdf.'_:foo' 'urn:blank':'urn:bar'
        rdf.'urn:foo' 'urn:blank':'_:foo'

        // Literal nodes
        rdf.'urn:lit1' 'urn:bar':'"foo"'
        rdf.'urn:lit2' 'urn:bar':'"le foo"@fr'
        rdf.'urn:lit3' 'urn:bar':'"12"^^xsd:int'

        // Same subject, different predicate object as attributes
        rdf.'urn:foo2' (
            'urn:bar1':'urn:baz1',
            'urn:bar2':'urn:baz2'
        )

        // Same subject and predicate, different object as attributes
        rdf.'urn:foo3'(
            'urn:bar':['urn:baz1', 'urn:baz2']
        )

        // Other variants
        // Same subject, different predicate and object
        rdf.'urn:foo4' {
            'urn:bar1' 'urn:baz1'
            'urn:bar2' 'urn:baz2'
        }

        // Same subject and predicate, different object
        rdf.'urn:foo5' {
            'urn:bar' {
                'urn:baz1'()
                'urn:baz2'()
            }
        }

        // With builtin property support
        rdf.urn.foo6 {
            urn.bar {
                urn.baz1
                urn.baz2
            }
        }

        // Mixing styles
        rdf.'_:foo2' {
            urn.blank {
                urn.very_blank
            }
        }

        assertEquals(17, graph.getNumberOfTriples())
    }
}