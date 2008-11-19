package org.jrdf.query.answer;

/**
 * An object which can visit different kinds of answers.
 *
 * @author Andrew Newman
 * @version $Revision: 2003 $
 */
public interface AnswerVisitor {
    /**
     * Visit ask answer.
     *
     * @param askAnswer to visit.
     */
    void visitAskAnswer(AskAnswer askAnswer);

    /**
     * Visit ask answer.
     *
     * @param selectAnswer to visit.
     */
    void visitSelectAnswer(SelectAnswer selectAnswer);
}
