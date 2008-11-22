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

package org.jrdf.gui.command;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.gui.model.JRDFModel;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.filechooser.FileChooserUtils;

import javax.swing.JFrame;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Loads an RDF file from the file system.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class LoadRdfCommand extends ApplicationWindowAwareCommand {
    private JRDFModel jrdfModel;

    public LoadRdfCommand() {
        super("rdfCommand");
    }

    public void setJRDFModel(JRDFModel jrdfModel) {
        this.jrdfModel = jrdfModel;
    }

    protected void doExecuteCommand() {
        JFrame control = getContext().getWindow().getControl();
        // TODO N3 Changes
        File file = FileChooserUtils.showFileChooser(control, "n3", "Ok", null);
        if (file != null) {
            ApplicationWindowAwareCommand applicationWindowAwareCommand = tryLoadModel(file);
            applicationWindowAwareCommand.execute();
        }
    }

    private ApplicationWindowAwareCommand tryLoadModel(File file) {
        try {
            URL fileUrl = tryGetURL(file);
            long startTime = System.currentTimeMillis();
            Graph graph = jrdfModel.loadModel(fileUrl);
            long numberOfTriples = tryGetNumberOfTriples(graph);
            RdfLoadedCommand actionCommand = getRdfLoadedCommand();
            actionCommand.setTriplesLoaded(numberOfTriples);
            actionCommand.setTimeTaken(System.currentTimeMillis() - startTime);
            return actionCommand;
        } catch (Exception e) {
            RdfFailedToLoadCommand actionCommand = getRdfFailedToLoadCommand();
            actionCommand.setFileName(file.getName());
            return actionCommand;
        }
    }

    private URL tryGetURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private long tryGetNumberOfTriples(Graph graph) {
        try {
            return graph.getNumberOfTriples();
        } catch (GraphException ge) {
            throw new RuntimeException(ge);
        }
    }

    private RdfLoadedCommand getRdfLoadedCommand() {
        return (RdfLoadedCommand) getContext().getLocalCommandExecutor("rdfLoadedCommand");
    }

    private RdfFailedToLoadCommand getRdfFailedToLoadCommand() {
        return (RdfFailedToLoadCommand) getContext().getLocalCommandExecutor("rdfFailedToLoadCommand");
    }

    private PageComponentContext getContext() {
        PageComponent activeComponent = getApplicationWindow().getPage().getActiveComponent();
        return activeComponent.getContext();
    }
}
