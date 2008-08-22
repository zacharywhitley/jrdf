package org.jrdf.server;

import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.util.ClosableIterator;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import static java.net.URI.create;

public class GraphResource extends Resource {
    private String graphName;
    private String subject;
    private String predicate;
    private String object;

    public GraphResource(Context context, Request request, Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
        // This would be nice to add
        //getVariants().add(new Variant(MediaType.TEXT_HTML));
        this.graphName = (String) this.getRequest().getAttributes().get("graph");
        Form form = this.getRequest().getResourceRef().getQueryAsForm();
        this.subject = form.getFirstValue("s");
        this.predicate = form.getFirstValue("p");
        this.object = form.getFirstValue("o");
    }

    @Override
    public boolean allowGet() {
        return true;
    }

    @Override
    public Representation represent(Variant variant) {
        MoleculeGraph graph = getGraph();
        GraphElementFactory elementFactory = graph.getElementFactory();
        SubjectNode subjectNode = getSubjectNode(elementFactory, subject);
        PredicateNode predicateNode = getPredicateNode(elementFactory, predicate);
        ObjectNode objectNode = getObjectNode(elementFactory, object);
        ClosableIterator<Molecule> molecules = getMolecules(graph, subjectNode, predicateNode, objectNode);
        while (molecules.hasNext()) {
            System.err.println("Got: " + molecules.next());
        }
        return new StringRepresentation("hello, world", MediaType.TEXT_PLAIN);
    }

    private ClosableIterator<Molecule> getMolecules(MoleculeGraph graph, SubjectNode subjectNode,
        PredicateNode predicateNode, ObjectNode objectNode) {
        ClosableIterator<Molecule> molecules;
        try {
            molecules = graph.findMolecules(subjectNode, predicateNode, objectNode);
        } catch (GraphException e) {
            molecules = null;
        }
        return molecules;
    }

    public WebInterfaceApplication getApplication() {
        return (WebInterfaceApplication) Application.getCurrent();
    }

    private MoleculeGraph getGraph() {
        MoleculeGraph graph;
        if (graphName == null) {
            graph = getApplication().getGraph();
        } else {
            graph = getApplication().getGraph(graphName);
        }
        return graph;
    }

    private SubjectNode getSubjectNode(GraphElementFactory elementFactory, String literalValue) {
        SubjectNode subjectNode = getNode(elementFactory, literalValue);
        if (subjectNode == null) {
            subjectNode = AnySubjectNode.ANY_SUBJECT_NODE;
        }
        return subjectNode;
    }

    private PredicateNode getPredicateNode(GraphElementFactory elementFactory, String literalValue) {
        PredicateNode predicateNode = getNode(elementFactory, literalValue);
        if (predicateNode == null) {
            predicateNode = AnyPredicateNode.ANY_PREDICATE_NODE;
        }
        return predicateNode;
    }

    private ObjectNode getObjectNode(GraphElementFactory elementFactory, String literalValue) {
        ObjectNode objectNode = getNode(elementFactory, literalValue);
        if (objectNode == null) {
            objectNode = AnyObjectNode.ANY_OBJECT_NODE;
        }
        return objectNode;
    }

    private URIReference getNode(GraphElementFactory elementFactory, String literalValue) {
        try {
            return elementFactory.createURIReference(create(literalValue.substring(1, literalValue.length() - 1)));
        } catch (Exception e) {
            return null;
        }
    }
}
