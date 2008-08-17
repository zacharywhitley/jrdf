package org.jrdf.server;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

public class HelloWorldResource extends Resource {

    public HelloWorldResource(Context context, Request request, Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    /**
     * Returns a full representation for a given variant.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        return new StringRepresentation("hello, world", MediaType.TEXT_PLAIN);
    }
}
