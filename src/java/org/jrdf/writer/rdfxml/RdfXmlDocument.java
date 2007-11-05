package org.jrdf.writer.rdfxml;

import org.jrdf.writer.WriteException;

public interface RdfXmlDocument {
    void writeHeader() throws WriteException;

    void writeFooter() throws WriteException;
}
