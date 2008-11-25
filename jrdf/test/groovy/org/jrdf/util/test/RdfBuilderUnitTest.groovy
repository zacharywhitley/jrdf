package org.jrdf.util.test

import groovy.xml.MarkupBuilder
import org.jrdf.TestJRDFFactory
import org.jrdf.graph.Graph

class RdfBuilderUnitTest extends GroovyTestCase {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();

    void testCreateAndDoSimpleTriples() {
        Graph graph = FACTORY.getGraph()
        def rdf = new RdfBuilder(graph)
        rdf.namespace("urn", "http://this/is/not/a/urn#");
        rdf.namespace("xsd", "http://www.w3.org/2001/XMLSchema#");

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

        assertEquals(14, graph.getNumberOfTriples())
    }
}