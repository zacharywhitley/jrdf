package org.jrdf.writer.rdfxml;

import org.jrdf.writer.WriteException;

/**
 * Class description goes here.
 */
public interface RdfXmlDocument {
    void writeHeader() throws WriteException;

    void writeFooter() throws WriteException;
}
