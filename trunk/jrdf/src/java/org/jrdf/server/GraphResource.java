package org.jrdf.server;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeToText;
import org.jrdf.graph.global.molecule.MoleculeTraverser;
import org.jrdf.graph.global.molecule.mem.MoleculeTraverserImpl;
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

public class GraphResource extends Resource {
    private NodeParser parser = new NodeParserImpl();
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
        SubjectNode subjectNode = parser.getSubjectNode(elementFactory, subject);
        PredicateNode predicateNode = parser.getPredicateNode(elementFactory, predicate);
        ObjectNode objectNode = parser.getObjectNode(elementFactory, object);
        StringBuilder builder = generateResponse(graph, subjectNode, predicateNode, objectNode);
        return new StringRepresentation(builder, MediaType.TEXT_PLAIN);
    }

    private MoleculeGraph getGraph() {
        MoleculeGraph graph;
        if (graphName == null) {
            graph = ((WebInterfaceApplication) Application.getCurrent()).getGraph();
        } else {
            graph = ((WebInterfaceApplication) Application.getCurrent()).getGraph(graphName);
        }
        return graph;
    }

    private StringBuilder generateResponse(MoleculeGraph graph, SubjectNode subjectNode, PredicateNode predicateNode,
        ObjectNode objectNode) {
        StringBuilder builder = new StringBuilder();
        MoleculeTraverser traverser = new MoleculeTraverserImpl();
        MoleculeToText moleculeToText = new MoleculeToText(builder);
        ClosableIterator<Molecule> molecules = getMolecules(graph, subjectNode, predicateNode, objectNode);
        while (molecules.hasNext()) {
            traverser.traverse(molecules.next(), moleculeToText);
        }
        return builder;
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

}
