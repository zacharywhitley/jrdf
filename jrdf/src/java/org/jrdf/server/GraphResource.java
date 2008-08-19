package org.jrdf.server;

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
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;

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
        MediaType type = variant.getMediaType();
        Graph graph = getApplication().getGraph("foo");
        try {
            System.err.println("Got: " + graph.getNumberOfTriples());
        } catch (GraphException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.err.println("Type: " + type);
        System.err.println("Values: " + graphName);
        System.err.println("Values: " + subject);
        System.err.println("Values: " + predicate);
        System.err.println("Values: " + object);
        System.err.println("Got: " + graph);
        return new StringRepresentation("hello, world", MediaType.TEXT_PLAIN);
    }

    public WebInterfaceApplication getApplication() {
        return (WebInterfaceApplication) Application.getCurrent();
    }
}
