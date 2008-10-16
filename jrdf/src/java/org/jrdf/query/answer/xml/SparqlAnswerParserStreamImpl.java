package org.jrdf.query.answer.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SparqlAnswerParserStreamImpl implements SparqlAnswerParserStream {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private SparqlAnswerParser parser;
    private BlockingQueue<InputStream> streamQueue;
    private LinkedHashSet<String> variables;
    private boolean gotVariables;
    private boolean hasMore;
    private TypeValue[] results;

    public SparqlAnswerParserStreamImpl(InputStream... streams) throws XMLStreamException, InterruptedException {
        this.hasMore = false;
        this.variables = new LinkedHashSet<String>();
        this.streamQueue = new LinkedBlockingQueue<InputStream>();
        for (InputStream stream : streams) {
            this.streamQueue.put(stream);
        }
        setupNextParser();
    }

    public void addStream(InputStream stream) throws InterruptedException, XMLStreamException {
        streamQueue.put(stream);
        if (!hasMore) {
            setupNextParser();
        }
    }

    public LinkedHashSet<String> getVariables() {
        return variables;
    }

    public boolean hasMoreResults() {
        return hasMore;
    }

    public TypeValue[] getResults() {
        if (parser.hasMoreResults()) {
            results = parser.getResults();
        }
        hasMore = hasMore();
        return results;
    }

    public void close() {
        if (parser != null) {
            parser.close();
        }
        streamQueue.clear();
    }

    private void setupNextParser() throws XMLStreamException {
        InputStream currentStream = streamQueue.poll();
        if (currentStream != null) {
            if (parser != null) {
                parser.close();
            }
            parser = new SparqlAnswerParserImpl(INPUT_FACTORY.createXMLStreamReader(currentStream));
            LinkedHashSet<String> newVariables = parser.getVariables();
            if (!gotVariables) {
                variables = newVariables;
                gotVariables = true;
            }
            if (!hasMore) {
                hasMore = hasMore();
            }
        } else {
            parser = null;
        }
    }

    private boolean hasMore() {
        try {
            return parser != null && getToNextStreamResult();
        } catch (XMLStreamException e) {
            return false;
        }
    }

    private boolean getToNextStreamResult() throws XMLStreamException {
        boolean gotNext = false;
        while (parser != null && !gotNext) {
            gotNext = parser.hasMoreResults();
            if (!gotNext) {
                setupNextParser();
            }
        }
        return gotNext;
    }
}
