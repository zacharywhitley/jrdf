package org.jrdf.server;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

public class GraphResource extends Resource {
    public GraphResource(Context context, Request request, Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    @Override
    public boolean allowGet() {
        return true;
    }

    @Override
    public void handleGet() {
        System.err.println("Got: " + this.getRequest().getAttributes());
    }

    @Override
    public Representation represent(Variant variant) {
        return new StringRepresentation("hello, world", MediaType.TEXT_PLAIN);
    }
}
