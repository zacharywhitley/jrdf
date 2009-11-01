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

package org.jrdf.query.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.query.answer.AskAnswer;
import org.jrdf.query.answer.AskAnswerImpl;
import org.jrdf.query.answer.SelectAnswer;
import org.jrdf.query.answer.SelectAnswerImpl;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import static org.jrdf.util.test.MockTestUtil.createMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.modules.junit4.PowerMockRunner;
import org.restlet.data.MediaType;
import org.restlet.resource.Representation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class SparqlXmlRepresentationFactoryUnitTest {
    private SparqlXmlRepresentationFactory factory;

    @Before
    public void createFactory() {
        factory = new SparqlXmlRepresentationFactory();
    }

    @Test
    public void askAnswerCreatesAskAnswerRepresentation() throws Exception {
        final MediaType type = createMock(MediaType.class);
        final Map<String, Object> map = new HashMap<String, Object>();
        final AskAnswer answer = new AskAnswerImpl(-1, false);
        map.put("answer", answer);
        replayAll();
        final Representation representation = factory.createRepresentation(type, map);
        verifyAll();
        assertThat(representation, instanceOf(AskAnswerSparqlRepresentation.class));
    }

    @Test
    public void selectAnswer() throws Exception {
        final MediaType type = createMock(MediaType.class);
        final Map<String, Object> map = new HashMap<String, Object>();
        final SelectAnswer answer = new SelectAnswerImpl(new LinkedHashSet<Attribute>(),
            createMock(EvaluatedRelation.class), -1, true);
        map.put("answer", answer);
        replayAll();
        final Representation representation = factory.createRepresentation(type, map);
        verifyAll();
        assertThat(representation, instanceOf(SelectAnswerSparqlRepresentation.class));
    }

    @Test
    public void emptyAnswerGivesEmptyRepresentation() throws Exception {
        final MediaType mediaType = createMock(MediaType.class);
        final Representation representation = factory.createRepresentation(mediaType, new HashMap<String, Object>());
        assertThat(representation.isAvailable(), equalTo(false));
        assertThat(representation.getAvailableSize(), equalTo(0L));
        assertThat(representation.isTransient(), equalTo(true));
    }
}
