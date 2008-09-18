package org.jrdf.restlet.server;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.query.Answer;
import static org.jrdf.query.AnswerXMLWriter.XSLT_URL_STRING;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
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
import static org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
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
    private static final TransformerFactory TRANSFORM_FACTORY = TransformerFactory.newInstance();

    public GraphResource(Context context, Request request, Response response) throws GraphException {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.valueOf(SPARQL_XML_RESULT_MEDIA_TYPE_STRING)));
        getVariants().add(new Variant(MediaType.TEXT_HTML));
        this.graphName = (String) this.getRequest().getAttributes().get("graph");
        this.graph = getGraph();
        System.err.println("graph name = " + graphName + ", # = " + graph.getNumberOfTriples());
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
            final String format = form.getFirstValue("format");
            if (queryString != null) {
                try {
                    processQuery(queryString, format);
                    return;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        getResponse().setStatus(CLIENT_ERROR_BAD_REQUEST);
    }

    private void processQuery(String queryString, String format) throws InvalidQuerySyntaxException,
        GraphException, XMLStreamException, TransformerException {
        final Answer answer = urqlConnection.executeQuery(graph, queryString);
        StringWriter writer = new StringWriter();
        answer.writeXML(writer);
        final String xmlString = writer.toString();
        Representation rep = renderResult(format, xmlString);
        getResponse().setEntity(rep);
        getResponse().setStatus(Status.SUCCESS_OK);
    }

    private Representation renderResult(String format, String xmlString) throws TransformerException {
        Representation rep;
        if ("html".equalsIgnoreCase(format)) {
            String transformedXMLString = doXSLTTransformation(xmlString);
            rep = new StringRepresentation(transformedXMLString, MediaType.TEXT_HTML);
        } else {
            rep = new StringRepresentation(xmlString, MediaType.TEXT_XML);
        }
        return rep;
    }

    private String doXSLTTransformation(String xmlString) throws TransformerException {
        StringWriter writer;
        Source xmlSource = new StreamSource(new StringReader(xmlString));
        Source xsltSource = new StreamSource(XSLT_URL_STRING);
        writer = new StringWriter();
        Result result = new StreamResult(writer);
        Transformer transformer = TRANSFORM_FACTORY.newTransformer(xsltSource);
        transformer.transform(xmlSource, result);
        final String transformedXMLString = writer.toString();
        return transformedXMLString;
    }

    @Override
    public Representation represent(Variant variant) {
        StringBuilder builder = new StringBuilder();
        builder.append("<form id=\"sparql\" name=\"sparqlForm\" method=\"post\" action=\"" + graphName + "\">\n");
        System.err.println("form string=" + builder.toString());
        builder.append("<textarea id=\"sparqlText\" name=\"queryString\" rows=\"6\" cols=\"70\"></textarea>");
        builder.append("<p/>");
        builder.append("HTML: <input type=\"radio\" name=\"format\" value=\"html\" checked=\"checked\"/>");
        builder.append("&nbsp;&nbsp;&nbsp;");
        builder.append("Raw XML: <input type=\"radio\" name=\"format\" value=\"xml\">");
        builder.append("<p/>");
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
