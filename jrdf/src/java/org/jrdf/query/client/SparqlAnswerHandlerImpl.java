/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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
 */

package org.jrdf.query.client;

import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.SparqlAnswerFactory;
import org.jrdf.query.answer.SparqlParserFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Request;
import org.restlet.resource.Representation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SparqlAnswerHandlerImpl implements SparqlAnswerHandler {
    private final SparqlAnswerFactory answerFactory;
    private final Map<MediaType, SparqlParserFactory> typeToParser;

    public SparqlAnswerHandlerImpl(final SparqlAnswerFactory newAnswerFactory,
        final Map<MediaType, SparqlParserFactory> newTypeToParser) {
        checkNotNull(newAnswerFactory, newTypeToParser);
        this.answerFactory = newAnswerFactory;
        this.typeToParser = newTypeToParser;
    }

    public Answer getAnswer(Representation output) {
        checkNotNull(output);
        try {
            SparqlParserFactory parserFactory = tryGetParserFactory(output.getMediaType());
            return answerFactory.createStreamingAnswer(output.getStream(), parserFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setAcceptedMediaTypes(Request request) {
        checkNotNull(request);
        ClientInfo clientInfo = request.getClientInfo();
        List<Preference<MediaType>> preferenceList = new ArrayList<Preference<MediaType>>();
        for (MediaType mediaType : typeToParser.keySet()) {
            preferenceList.add(new Preference<MediaType>(mediaType));
        }
        clientInfo.setAcceptedMediaTypes(preferenceList);
    }

    private SparqlParserFactory tryGetParserFactory(MediaType mediaType) {
        final SparqlParserFactory parserFactory = typeToParser.get(mediaType);
        if (parserFactory == null) {
            throw new RuntimeException("Unknown Media Type: " + mediaType);
        } else {
            return parserFactory;
        }
    }
}
