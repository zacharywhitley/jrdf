/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
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
 *
 */

package org.jrdf.urql.builder;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import static org.jrdf.urql.builder.TokenHelper.getResource;
import org.jrdf.urql.parser.analysis.AnalysisAdapter;
import org.jrdf.urql.parser.node.ABooleanLiteralLiteral;
import org.jrdf.urql.parser.node.ADbQuotedLiteralLiteralValue;
import org.jrdf.urql.parser.node.ADbQuotedUnescapedDbQuotedStrand;
import org.jrdf.urql.parser.node.ADecimalUnsignedNumericLiteral;
import org.jrdf.urql.parser.node.ADoubleUnsignedNumericLiteral;
import org.jrdf.urql.parser.node.AIntegerUnsignedNumericLiteral;
import org.jrdf.urql.parser.node.ALangLiteralRdfLiteral;
import org.jrdf.urql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.urql.parser.node.ANegativeNumericLiteralNumericLiteral;
import org.jrdf.urql.parser.node.ANumericLiteralLiteral;
import org.jrdf.urql.parser.node.APositiveNumericLiteralNumericLiteral;
import org.jrdf.urql.parser.node.AQnameDatatypeDatatype;
import org.jrdf.urql.parser.node.AQnameQnameElement;
import org.jrdf.urql.parser.node.AQuotedLiteralLiteralValue;
import org.jrdf.urql.parser.node.AQuotedUnescapedQuotedStrand;
import org.jrdf.urql.parser.node.ARdfLiteralLiteral;
import org.jrdf.urql.parser.node.ARdfLiteralPrimaryExpression;
import org.jrdf.urql.parser.node.AResourceDatatypeDatatype;
import org.jrdf.urql.parser.node.ATypedLiteralRdfLiteral;
import org.jrdf.urql.parser.node.AUnsignedNumericLiteralNumericLiteral;
import org.jrdf.urql.parser.node.AUntypedLiteralRdfLiteral;
import org.jrdf.urql.parser.node.Node;
import org.jrdf.urql.parser.node.Token;
import org.jrdf.urql.parser.node.ATrueBooleanLiteral;
import org.jrdf.urql.parser.node.AFalseBooleanLiteral;
import org.jrdf.urql.parser.parser.ParserException;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;
import org.jrdf.vocabulary.XSD;

import java.net.URI;
import static java.net.URI.create;
import java.util.Map;

public final class LiteralBuilderImpl extends AnalysisAdapter implements LiteralBuilder {
    private final GraphElementFactory factory;
    private final Map<String, String> prefixMap;
    private ParserException exception;
    private Literal result;
    private URI uri;
    private String lexicalValue;
    private String currentSign;
    private Token currentToken;

    public LiteralBuilderImpl(GraphElementFactory newFactory, Map<String, String> newPrefixMap) {
        checkNotNull(newFactory, newPrefixMap);
        this.factory = newFactory;
        this.prefixMap = newPrefixMap;
    }

    public Literal createLiteral(ALiteralObjectTripleElement element) throws ParserException {
        checkNotNull(element);
        resetState();
        element.getLiteral().apply(this);
        return getResult(element);
    }

    public Literal createLiteral(ARdfLiteralPrimaryExpression element) throws ParserException {
        checkNotNull(element);
        resetState();
        element.getRdfLiteral().apply(this);
        return getResult(element);
    }

    public Literal createLiteral(ATrueBooleanLiteral element) throws ParserException {
        createLiteral("true", XSD.BOOLEAN);
        return getResult(element);
    }

    public Literal createLiteral(AFalseBooleanLiteral element) throws ParserException {
        createLiteral("false", XSD.BOOLEAN);
        return getResult(element);
    }

    private void resetState() {
        result = null;
        exception = null;
        uri = null;
        lexicalValue = null;
        currentSign = "";
    }

    private Literal getResult(Node element) throws ParserException {
        if (exception == null) {
            if (result == null) {
                throw new IllegalStateException("Unable to parse element: " + element);
            } else {
                return result;
            }
        } else {
            throw exception;
        }
    }

    @Override
    public void caseARdfLiteralLiteral(ARdfLiteralLiteral node) {
        node.getRdfLiteral().apply(this);
    }

    @Override
    public void caseANumericLiteralLiteral(ANumericLiteralLiteral node) {
        node.getNumericLiteral().apply(this);
    }

    @Override
    public void caseAUnsignedNumericLiteralNumericLiteral(AUnsignedNumericLiteralNumericLiteral node) {
        node.getUnsignedNumericLiteral().apply(this);
    }

    @Override
    public void caseAPositiveNumericLiteralNumericLiteral(APositiveNumericLiteralNumericLiteral node) {
        currentSign = "+";
        node.getUnsignedNumericLiteral().apply(this);
    }

    @Override
    public void caseANegativeNumericLiteralNumericLiteral(ANegativeNumericLiteralNumericLiteral node) {
        currentSign = "-";
        node.getUnsignedNumericLiteral().apply(this);
    }

    @Override
    public void caseAIntegerUnsignedNumericLiteral(AIntegerUnsignedNumericLiteral node) {
        createLiteral(currentSign + node.getInteger().getText(), XSD.INTEGER);
    }

    @Override
    public void caseADecimalUnsignedNumericLiteral(ADecimalUnsignedNumericLiteral node) {
        createLiteral(currentSign + node.getDecimal().getText(), XSD.DECIMAL);
    }

    @Override
    public void caseADoubleUnsignedNumericLiteral(ADoubleUnsignedNumericLiteral node) {
        createLiteral(currentSign + node.getDouble().getText(), XSD.DOUBLE);
    }

    @Override
    public void caseABooleanLiteralLiteral(ABooleanLiteralLiteral node) {
        node.getBooleanLiteral().apply(this);
    }

    @Override
    public void caseATrueBooleanLiteral(ATrueBooleanLiteral node) {
        createLiteral("true", XSD.BOOLEAN);
    }

    @Override
    public void caseAFalseBooleanLiteral(AFalseBooleanLiteral node) {
        createLiteral("false", XSD.BOOLEAN);
    }

    @Override
    public void caseAUntypedLiteralRdfLiteral(AUntypedLiteralRdfLiteral node) {
        node.getLiteralValue().apply(this);
        if (lexicalValue != null) {
            createLiteral(lexicalValue);
        }
    }

    @Override
    public void caseALangLiteralRdfLiteral(ALangLiteralRdfLiteral node) {
        String languageTag = node.getLanguage().getText();
        node.getLiteralValue().apply(this);
        if (lexicalValue != null && languageTag != null) {
            createLiteral(lexicalValue, languageTag);
        }
    }

    @Override
    public void caseATypedLiteralRdfLiteral(ATypedLiteralRdfLiteral node) {
        node.getDatatype().apply(this);
        node.getLiteralValue().apply(this);
        if (lexicalValue != null && uri != null) {
            createLiteral(lexicalValue, uri);
        }
    }

    @Override
    public void caseAResourceDatatypeDatatype(AResourceDatatypeDatatype node) {
        uri = getResource(node.getResource());
    }

    @Override
    public void caseAQnameDatatypeDatatype(AQnameDatatypeDatatype node) {
        AQnameQnameElement qname = (AQnameQnameElement) node.getQnameElement();
        String prefix = qname.getNcnamePrefix().getText();
        if (!prefixMap.keySet().contains(prefix)) {
            exception = new ParserException(qname.getNcnamePrefix(), "Prefix not found: " + prefix);
        } else {
            uri = create(prefixMap.get(prefix) + qname.getNcName().getText());
        }
    }

    @Override
    public void caseADbQuotedLiteralLiteralValue(ADbQuotedLiteralLiteralValue node) {
        node.getDbQuotedStrand().getFirst().apply(this);
    }

    @Override
    public void caseAQuotedLiteralLiteralValue(AQuotedLiteralLiteralValue node) {
        node.getQuotedStrand().getFirst().apply(this);
    }

    @Override
    public void caseAQuotedUnescapedQuotedStrand(AQuotedUnescapedQuotedStrand node) {
        currentToken = node.getQtext();
        lexicalValue = node.getQtext().getText();
    }

    @Override
    public void caseADbQuotedUnescapedDbQuotedStrand(ADbQuotedUnescapedDbQuotedStrand node) {
        currentToken = node.getDbqtext();
        lexicalValue = node.getDbqtext().getText();
    }

    private void createLiteral(String s) {
        try {
            result = factory.createLiteral(s);
        } catch (GraphElementFactoryException e) {
            exception = new ParserException(currentToken, "Could not create literal: " + s);
        }
    }

    private void createLiteral(String s, String language) {
        try {
            result = factory.createLiteral(s, language);
        } catch (GraphElementFactoryException e) {
            exception = new ParserException(currentToken, "Could not create literal: " + s + " lang: " + language);
        }
    }

    private void createLiteral(String s, URI datatype) {
        try {
            result = factory.createLiteral(s, datatype);
        } catch (GraphElementFactoryException e) {
            exception = new ParserException(currentToken, "Could not create literal: " + s + " datatype: " + datatype);
        }
    }
}
