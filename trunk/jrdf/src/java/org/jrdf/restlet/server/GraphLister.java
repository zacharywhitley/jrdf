package org.jrdf.restlet.server;

import org.restlet.resource.ResourceException;

import java.util.Map;

public interface GraphLister {
    Map<String, String> populateIdNameMap() throws ResourceException;

    String dirName();
}
