package org.jrdf.query.answer;

public interface AnswerVisitable {
    /**
     * Accept a call from a AnswerVisitor.
     *
     * @param visitor the object doing the visiting.
     */
    void accept(AnswerVisitor visitor);
}
