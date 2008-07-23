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
import org.jrdf.graph.GraphException;
import org.jrdf.urql.parser.analysis.AnalysisAdapter;
import org.jrdf.urql.parser.node.PLiteral;
import org.jrdf.urql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.urql.parser.node.Switch;
import org.jrdf.urql.parser.node.ADbQuotedUnescapedDbQuotedStrand;
import org.jrdf.urql.parser.node.AQuotedUnescapedQuotedStrand;
import org.jrdf.urql.parser.node.Node;
import org.jrdf.urql.parser.node.AUntypedLiteralLiteral;
import org.jrdf.urql.parser.node.PLiteralValue;
import org.jrdf.urql.parser.node.ADbQuotedLiteralLiteralValue;
import org.jrdf.urql.parser.node.AQuotedLiteralLiteralValue;
import org.jrdf.urql.parser.node.ALangLiteralLiteral;
import org.jrdf.urql.parser.node.ATypedLiteralLiteral;
import org.jrdf.urql.parser.node.PDatatype;
import org.jrdf.urql.parser.node.AResourceDatatypeDatatype;
import org.jrdf.urql.parser.node.AQnameDatatypeDatatype;
import org.jrdf.urql.parser.node.AQnameQnameElement;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.net.URI;
import static java.net.URI.*;
import java.util.Map;

public final class LiteralBuilderImpl extends AnalysisAdapter implements LiteralBuilder, Switch {
    private final GraphElementFactory factory;
    private final Map<String, String> prefixMap;
    private GraphException exception;
    private Literal result;

    public LiteralBuilderImpl(GraphElementFactory newFactory, Map<String, String> newPrefixMap) {
        checkNotNull(newFactory, newPrefixMap);
        this.factory = newFactory;
        this.prefixMap = newPrefixMap;
    }

    public Literal createLiteral(ALiteralObjectTripleElement element) throws GraphException {
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
    public void caseAUntypedLiteralLiteral(AUntypedLiteralLiteral node) {
        String lexicalValue = getLexicalValue(node.getLiteralValue());
        createLiteral(lexicalValue);
    }

    public void caseALangLiteralLiteral(ALangLiteralLiteral node) {
        String languageTag = node.getLanguage().getText();
        String lexicalValue = getLexicalValue(node.getLiteralValue());
        createLiteral(lexicalValue, languageTag);
    }

    public void caseATypedLiteralLiteral(ATypedLiteralLiteral node) {
        PDatatype pDatatype = node.getDatatype();
        URI uri = getDatatype(pDatatype);
        String lexicalValue = getLexicalValue(node.getLiteralValue());
        createLiteral(lexicalValue, uri);
    }

    private URI getDatatype(PDatatype pDatatype) {
        URI uri = null;
        if (pDatatype instanceof AResourceDatatypeDatatype) {
            uri = create(((AResourceDatatypeDatatype) pDatatype).getResource().getText());
        } else if (pDatatype instanceof AQnameDatatypeDatatype) {
            AQnameQnameElement qname = (AQnameQnameElement) ((AQnameDatatypeDatatype) pDatatype).getQnameElement();
            String prefix = qname.getNcnamePrefix().getText();
            if (!prefixMap.keySet().contains(prefix)) {
                exception = new GraphException("Prefix not found: " + prefix);
            }
            uri = create(prefixMap.get(prefix) + qname.getNcName().getText());
        }
        return uri;
    }

    private String getLexicalValue(PLiteralValue pLiteralValue) {
        String lexicalValue = "";
        if (pLiteralValue instanceof ADbQuotedLiteralLiteralValue) {
            lexicalValue = getText(((ADbQuotedLiteralLiteralValue) pLiteralValue).getDbQuotedStrand().getFirst());
        } else if (pLiteralValue instanceof AQuotedLiteralLiteralValue) {
            lexicalValue = getText(((AQuotedLiteralLiteralValue) pLiteralValue).getQuotedStrand().getFirst());
        }
        return lexicalValue;
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

    private void createLiteral(String s, URI datatype) {
        try {
            result = factory.createLiteral(s, datatype);
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
