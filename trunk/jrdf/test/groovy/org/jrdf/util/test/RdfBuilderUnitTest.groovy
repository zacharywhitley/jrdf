package org.jrdf.util.test

import groovy.xml.MarkupBuilder
import org.jrdf.TestJRDFFactory
import org.jrdf.graph.Graph

class RdfBuilderUnitTest extends GroovyTestCase {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();

    void testCreateAndDoSimpleTriples() {
        Graph graph = FACTORY.getGraph()
        def rdf = new RdfBuilder(graph)

        // Normal triple
        rdf.'<urn:foo>' '<urn:bar>':'<urn:baz>'

        // Same subject, different predicate and object
        rdf.'<urn:foo1>' {
            '<urn:bar1>' '<urn:baz1>'
            '<urn:bar2>' '<urn:baz2>'
        }

        // Same subject, different predicate object as attributes
        rdf.'<urn:foo2>' (
            '<urn:bar1>':'<urn:baz1>',
            '<urn:bar2>':'<urn:baz2>'
        )

        // Same subject and predicate, different object
        rdf.'<urn:foo3>' {
            '<urn:bar>' {
                '<urn:baz1>'()
                '<urn:baz2>'()
            }
        }

        // Same subject and predicate, different object as attributes
        rdf.'<urn:foo4>'(
            '<urn:bar>':['<urn:baz1>', '<urn:baz2>']
        )

        // Blank nodes
        rdf.'_foo' '<urn:blank>':'<urn:bar>'
        rdf.'<urn:foo>' '<urn:blank>':'_foo'

        assertEquals(11, graph.getNumberOfTriples())
        println("Graph: " + graph)
    }
}