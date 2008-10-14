/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.gui.view;

import org.jrdf.gui.command.InvalidQueryCommand;
import org.jrdf.gui.command.QueryRanCommand;
import org.jrdf.gui.command.RdfFailedToLoadCommand;
import org.jrdf.gui.command.RdfLoadedCommand;
import org.jrdf.query.answer.Answer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;


/**
 * A builder that always throws exceptions.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class QueryViewImpl extends AbstractView implements ApplicationListener, QueryView {
    private static final double HALF_PANE = 0.5;
    private QueryPanelView queryPanelView;
    private ResultsPanelView resultsPanelView;

    public void setQueryPanel(QueryPanelView newQueryPanelView) {
        queryPanelView = newQueryPanelView;
    }

    public void setResultsPanel(ResultsPanelView newResultsPanelView) {
        resultsPanelView = newResultsPanelView;
    }

    // TODO (AN) Add afterPropertiesSet to check that the panels aren't null.

    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register("rdfLoadedCommand", new RdfLoadedCommand(this));
        context.register("rdfFailedToLoadCommand", new RdfFailedToLoadCommand(this));
        context.register("queryRanCommand", new QueryRanCommand(this));
        context.register("invalidQueryCommand", new InvalidQueryCommand(this));
    }

    public void setTriplesLoaded(long numberOfTriples, long timeTaken) {
        String message = getMessage("queryView.modelLoaded");
        getStatusBar().setMessage(message + " " + numberOfTriples + ", " + timeTaken + " ms");
    }

    public void setLoadErrorMessage(String errorMessage) {
        String message = getMessage("queryView.failedToLoad");
        getStatusBar().setMessage(message + errorMessage);
    }

    public void setInvalidQueryMessage(String errorMessage) {
        String message = getMessage("queryView.invalidQuery");
        getStatusBar().setMessage(message + errorMessage);
    }

    public void setResults(Answer answer) {
        resultsPanelView.setResults(answer);
        String resultsMsg = getMessage("resultsView.numResultsFound");
        String timeTakenMsg = getMessage("resultsView.timeTakenForResults");
        getStatusBar().setMessage(resultsMsg + answer.numberOfTuples() + ", " + timeTakenMsg + answer.getTimeTaken() +
            " ms");
    }

    protected JComponent createControl() {
        JPanel queryJPanel = queryPanelView.getJPanel();
        JPanel resultsJPanel = resultsPanelView.getJPanel();
        return createPane(queryJPanel, resultsJPanel);
    }

    private JSplitPane createPane(JPanel queryJPanel, JPanel resultsJPanel) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryJPanel, resultsJPanel);
        splitPane.setDividerLocation(HALF_PANE);
        splitPane.setResizeWeight(HALF_PANE);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        return splitPane;
    }

    public void onApplicationEvent(ApplicationEvent e) {
    }

    public void componentClosed() {
    }

    public void componentFocusGained() {
    }

    public void componentFocusLost() {
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    public void componentOpened() {
    }
}