package org.jrdf.restlet.server;

import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.GraphException;
import org.jrdf.query.Answer;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.execute.QueryEngine;
import static org.jrdf.restlet.server.Server.SPARQL_XML_RESULT_MEDIA_TYPE_STRING;
import org.jrdf.urql.UrqlConnectionImpl;
import org.jrdf.urql.builder.QueryBuilder;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import javax.xml.stream.XMLStreamException;
import java.io.StringWriter;

public class GraphResource extends Resource {
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryEngine QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();
    /**
     * The form name for the query string.
     */
    public static final String QUERY_STRING = "queryString";

    private String graphName;
    protected MoleculeGraph graph;
    protected UrqlConnectionImpl urqlConnection;

    public GraphResource(Context context, Request request, Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.valueOf(SPARQL_XML_RESULT_MEDIA_TYPE_STRING)));
        getVariants().add(new Variant(MediaType.TEXT_HTML));
        this.graphName = (String) this.getRequest().getAttributes().get("graph");
        this.graph = getGraph();
        //System.err.println("graph name = " + graphName + ", # = " + graph.getNumberOfTriples());
        /*final ClosableIterable<Triple> iterable = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        for (Triple triple : iterable) {
            System.err.println(triple.toString());
        }*/
    }

    @Override
    public boolean allowGet() {
        return true;
    }

    @Override
    public boolean allowPost() {
        return true;
    }

    @Override
    public void acceptRepresentation(Representation representation) throws ResourceException {
        if (representation != null) {
            Form form = new Form(representation);
            String queryString = form.getFirstValue(QUERY_STRING);
            if (queryString != null) {
                try {
                    processQuery(queryString);
                    return;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    private void processQuery(String queryString) throws InvalidQuerySyntaxException,
        GraphException, XMLStreamException {
        final Answer answer = urqlConnection.executeQuery(graph, queryString);
        StringWriter writer = new StringWriter();
        answer.asXML(writer);
        final String xmlString = writer.toString();
        Representation rep = new StringRepresentation(xmlString,
                MediaType.valueOf(SPARQL_XML_RESULT_MEDIA_TYPE_STRING));
        getResponse().setEntity(rep);
        getResponse().setStatus(Status.SUCCESS_OK);
    }

    @Override
    public Representation represent(Variant variant) {
        StringBuilder builder = new StringBuilder();
        builder.append("<form id=\"sparql\" name=\"sparqlForm\" method=\"post\" action=\"" + graphName + "\">\n");
        System.err.println("form string=" + builder.toString());
        builder.append("<textarea id=\"sparqlText\" name=\"queryString\" rows=\"6\" cols=\"70\"></textarea>");
        builder.append("<input type=\"submit\" value=\"Submit\" />\n");
        builder.append("</form>\n");
        return new StringRepresentation(builder, MediaType.TEXT_HTML);
    }

    private MoleculeGraph getGraph() {
        MoleculeGraph graph;
        if (graphName == null) {
            graph = ((WebInterfaceApplication) Application.getCurrent()).getGraph();
        } else {
            graph = ((WebInterfaceApplication) Application.getCurrent()).getGraph(graphName);
        }
        this.urqlConnection = new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
        return graph;
    }
}
