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

package org.jrdf.urql.builder;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.urql.parser.analysis.AnalysisAdapter;
import org.jrdf.urql.parser.node.PLiteral;
import org.jrdf.urql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.urql.parser.node.Switch;
import org.jrdf.urql.parser.node.AQuotedLiteralLiteral;
import org.jrdf.urql.parser.node.PQuotedStrand;
import org.jrdf.urql.parser.node.ADbQuotedLiteralLiteral;
import org.jrdf.urql.parser.node.PDbQuotedStrand;
import org.jrdf.urql.parser.node.ADbQuotedUnescapedDbQuotedStrand;
import org.jrdf.urql.parser.node.ALangQuotedLiteralLiteral;
import org.jrdf.urql.parser.node.AQuotedUnescapedQuotedStrand;
import org.jrdf.urql.parser.node.ALangDbQuotedLiteralLiteral;
import org.jrdf.urql.parser.node.Node;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.LinkedList;

public final class LiteralBuilderImpl extends AnalysisAdapter implements LiteralBuilder, Switch {
    private final GraphElementFactory factory;
    private GraphElementFactoryException exception;
    private Literal result;

    public LiteralBuilderImpl(GraphElementFactory newFactory) {
        checkNotNull(newFactory);
        factory = newFactory;
    }

    public Literal createLiteral(ALiteralObjectTripleElement element) throws GraphElementFactoryException {
        checkNotNull(element);
        exception = null;
        PLiteral pLiteral = element.getLiteral();
        pLiteral.apply(this);
        if (exception == null) {
            return result;
        } else {
            throw exception;
        }
    }

    @Override
    public void caseAQuotedLiteralLiteral(AQuotedLiteralLiteral node) {
        LinkedList<PQuotedStrand> list = node.getQuotedStrand();
        PQuotedStrand tmpStrand = list.getFirst();
        createLiteral(getText(tmpStrand));
    }

    @Override
    public void caseADbQuotedLiteralLiteral(ADbQuotedLiteralLiteral node) {
        LinkedList<PDbQuotedStrand> list = node.getDbQuotedStrand();
        PDbQuotedStrand tmpStrand = list.get(0);
        createLiteral(getText(tmpStrand));
    }

    @Override
    public void caseALangQuotedLiteralLiteral(ALangQuotedLiteralLiteral node) {
        String languageTag = node.getLanguage().getText();
        PQuotedStrand tmpStrand = node.getQuotedStrand().getFirst();
        createLiteral(getText(tmpStrand), languageTag);
    }

    public void caseALangDbQuotedLiteralLiteral(ALangDbQuotedLiteralLiteral node) {
        String languageTag = node.getLanguage().getText();
        PDbQuotedStrand tmpStrand = node.getDbQuotedStrand().getFirst();
        createLiteral(getText(tmpStrand), languageTag);
    }

    private void createLiteral(String s) {
        try {
            result = factory.createLiteral(s);
        } catch (GraphElementFactoryException e) {
            exception = e;
        }
    }

    private void createLiteral(String s, String language) {
        try {
            result = factory.createLiteral(s, language);
        } catch (GraphElementFactoryException e) {
            exception = e;
        }
    }

    private String getText(Node tmpStrand) {
        if (tmpStrand instanceof AQuotedUnescapedQuotedStrand) {
            AQuotedUnescapedQuotedStrand strand = (AQuotedUnescapedQuotedStrand) tmpStrand;
            return strand.getQtext().getText();
        } else if (tmpStrand instanceof ADbQuotedUnescapedDbQuotedStrand) {
            ADbQuotedUnescapedDbQuotedStrand strand = (ADbQuotedUnescapedDbQuotedStrand) tmpStrand;
            return strand.getDbqtext().getText();
        }
        return "";
    }
}
