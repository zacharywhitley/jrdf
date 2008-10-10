package org.jrdf.restlet.server;

import org.jrdf.restlet.RepresentationFactory;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;

import java.util.HashMap;
import java.util.Map;

public class JsonRepresentationFactory implements RepresentationFactory {
    public Representation createRepresentation(MediaType defaultMediaType, Map<String, Object> dataModel) {
        final Map<Object, Object> model = new HashMap<Object, Object>();
        model.putAll(dataModel);
        return new JsonRepresentation(model);
    }
}
