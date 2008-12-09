package org.jrdf.query.answer.xml.parser;

import org.jrdf.query.answer.AnswerType;
import static org.jrdf.query.answer.AnswerType.ASK;
import org.jrdf.query.answer.xml.TypeValue;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// TODO AN/YF - Turn into extension of closableiterator?
public class SparqlAnswerStreamParserImpl implements SparqlAnswerStreamParser {
    private SparqlAnswerParser parser;
    private BlockingQueue<InputStream> streamQueue;
    private LinkedHashSet<String> variables;
    private boolean gotVariables;
    private boolean hasMore;
    private TypeValue[] results;
    private AnswerType answerType;
    private boolean result;

    public SparqlAnswerStreamParserImpl(InputStream... streams) throws XMLStreamException, InterruptedException {
        this.hasMore = false;
        this.variables = new LinkedHashSet<String>();
        this.streamQueue = new LinkedBlockingQueue<InputStream>();
        result = false;
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

    public AnswerType getAnswerType() throws XMLStreamException {
        return answerType;
    }

    public boolean getAskResult() throws XMLStreamException {
        if (answerType == ASK) {
            final boolean partialAnswer = parser.getAskResult();
            result = result || partialAnswer;
            return result;
        } else {
            throw new UnsupportedOperationException("Cannot answer boolean for non-ASK query: " + answerType);
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
            parser = new SparqlAnswerParserImpl(currentStream);
            answerType = parser.getAnswerType();
            parseVariables();
            if (!hasMore) {
                hasMore = hasMore();
            }
        } else {
            parser = null;
        }
    }

    private void parseVariables() {
        if (!gotVariables && answerType == AnswerType.SELECT) {
            variables = parser.getVariables();
            gotVariables = true;
        }
    }

    private boolean hasMore() {
        try {
            return getToNextStreamResult();
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
