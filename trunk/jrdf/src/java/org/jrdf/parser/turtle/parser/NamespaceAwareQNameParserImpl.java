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

package org.jrdf.parser.turtle.parser;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.URIReference;
import org.jrdf.parser.NamespaceListener;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.ntriples.parser.NTripleUtil;
import org.jrdf.util.boundary.RegexMatcher;
import org.jrdf.util.boundary.RegexMatcherFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotEmptyString;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import static java.net.URI.create;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

public final class NamespaceAwareQNameParserImpl implements NamespaceAwareQNameParser {
    private static final int PREFIX_GROUP = 2;
    private static final int LOCAL_GROUP = 3;
    private static final Pattern REGEX = compile("((\\p{Alpha}[\\x20-\\x7E]*?):([\\x20-\\x7E]*?))");
    private final GraphElementFactory graphElementFactory;
    private final NTripleUtil nTripleUtil;
    private final NamespaceListener listener;
    private final RegexMatcherFactory matcherFactory;

    public NamespaceAwareQNameParserImpl(GraphElementFactory newGraphElementFactory, NTripleUtil newNTripleUtil,
        NamespaceListener newListener, final RegexMatcherFactory newMatcherFactory) {
        checkNotNull(newGraphElementFactory, newNTripleUtil);
        graphElementFactory = newGraphElementFactory;
        nTripleUtil = newNTripleUtil;
        listener = newListener;
        matcherFactory = newMatcherFactory;
    }

    public URIReference parseURIReference(String s) throws ParseException {
        checkNotEmptyString("s", s);
        try {
            String literal = nTripleUtil.unescapeLiteral(s);
            return graphElementFactory.createURIReference(create(literal));
        } catch (IllegalArgumentException iae) {
            throw new ParseException("Failed to create URI Reference: " + s, PREFIX_GROUP);
        } catch (GraphElementFactoryException e) {
            throw new ParseException("Failed to create URI Reference: " + s, PREFIX_GROUP);
        }
    }

    public URIReference parseURIReferenceWithNamespace(String s) throws ParseException {
        final RegexMatcher regexMatcher = matcherFactory.createMatcher(REGEX, s);
        if (regexMatcher.matches()) {
            final String fullURI = listener.getFullURI(regexMatcher.group(PREFIX_GROUP));
            final String local = regexMatcher.group(LOCAL_GROUP);
            return parseURIReference(fullURI + local);
        } else {
            return null;
        }
    }

}