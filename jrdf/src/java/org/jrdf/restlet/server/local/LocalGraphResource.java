package org.jrdf.restlet.server.local;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.Answer;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.restlet.server.BaseGraphResource;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.urql.UrqlConnectionImpl;
import org.jrdf.urql.builder.QueryBuilder;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Request;
import org.restlet.data.Response;
import static org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;
import static org.restlet.data.Status.SUCCESS_OK;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;

import javax.xml.stream.XMLStreamException;
import java.io.StringWriter;

public class LocalGraphResource extends BaseGraphResource {
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryEngine QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();

    private MoleculeGraph graph;
    private UrqlConnection urqlConnection;

    public LocalGraphResource(Context context, Request request, Response response) throws GraphException {
        super(context, request, response);
        this.graph = getGraph();
        System.err.println("graph name = " + graphName + ", # = " + graph.getNumberOfTriples());
    }

    @Override
    public void acceptRepresentation(Representation representation) throws ResourceException {
        try {
            checkNotNull(representation);
            Form form = new Form(representation);
            String queryString = form.getFirstValue(QUERY_STRING);
            final String newFormat = form.getFirstValue("format");
            format = (newFormat == null) ? FORMAT_XML : newFormat;
            String answerString = processQuery(queryString, format);
            constructAnswerRepresentation(format, answerString);
            getResponse().setStatus(SUCCESS_OK);
        } catch (IllegalArgumentException e) {
            getResponse().setStatus(CLIENT_ERROR_BAD_REQUEST, e);
        } catch (Exception e) {
            getResponse().setStatus(SERVER_ERROR_INTERNAL, e);
        }
    }

    private String processQuery(String queryString, String format) throws InvalidQuerySyntaxException,
        GraphException, XMLStreamException {
        checkNotNull(queryString, format);
        Answer answer = urqlConnection.executeQuery(graph, queryString);
        StringWriter writer = new StringWriter();
        answer.writeXML(writer);
        return writer.toString();
    }

    private MoleculeGraph getGraph() {
        MoleculeGraph graph;
        if (graphName == null) {
            graph = ((WebInterfaceGraphApplication) Application.getCurrent()).getGraph();
        } else {
            graph = ((WebInterfaceGraphApplication) Application.getCurrent()).getGraph(graphName);
        }
        this.urqlConnection = new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
        return graph;
    }
}
