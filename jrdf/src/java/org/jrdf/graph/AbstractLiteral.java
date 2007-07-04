/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph;

// Java 2 standard

import org.jrdf.graph.datatype.DatatypeFactoryImpl;
import org.jrdf.graph.datatype.StringValue;
import org.jrdf.graph.datatype.Value;
import static org.jrdf.util.EqualsUtil.hasSuperClassOrInterface;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;
import org.jrdf.util.EscapeUtil;

import java.io.Serializable;
import java.net.URI;

/**
 * A base implementation of an RDF {@link Literal}.
 *
 * @author Andrew Newman
 * @author Simon Raboczi
 * @version $Revision$
 */
public abstract class AbstractLiteral implements Literal, Serializable {

    /**
     * Allow newer compiled version of the stub to operate when changes
     * have not occurred with the class.
     * NOTE : update this serialVersionUID when a method or a public member is
     * deleted.
     */
    private static final long serialVersionUID = 2589574733270452078L;

    /**
     * The lexical form of the literal.
     */
    protected Value value;

    /**
     * The language code of the literal.
     */
    protected String language;

    /**
     * RDF datatype URI, <code>null</code> for untyped literal.
     */
    protected URI datatypeURI;

    /**
     * Construct a plain literal.
     *
     * @param newLexicalForm the text part of the literal
     * @throws IllegalArgumentException if <var>newLexicalForm</var> is <code>null</code>
     */
    protected AbstractLiteral(String newLexicalForm) {

        // Validate "newLexicalForm" parameter
        if (null == newLexicalForm) {
            throw new IllegalArgumentException("Null \"newLexicalForm\" parameter");
        }

        // Initialize fields
        value = new StringValue(newLexicalForm);
        language = "";
        datatypeURI = null;
    }

    /**
     * Construct a literal with language.
     *
     * @param newLexicalForm the text part of the literal
     * @param newLanguage    the language code, possibly the empty string but not
     *                       <code>null</code>
     * @throws IllegalArgumentException if <var>lexicalForm</var> or
     *                                  <var>lang</var> are <code>null</code>
     */
    protected AbstractLiteral(String newLexicalForm, String newLanguage) {

        // Validate "lexicalForm" parameter
        if (null == newLexicalForm) {
            throw new IllegalArgumentException("Null \"lexicalForm\" parameter");
        }

        // Validate "language" parameter
        if (null == newLanguage) {
            throw new IllegalArgumentException("Null \"language\" parameter");
        }

        // Initialize fields
        value = new StringValue(newLexicalForm);
        language = newLanguage;
        datatypeURI = null;
    }

    /**
     * Construct a datatyped literal.
     *
     * @param newLexicalForm the text part of the literal
     * @param newDatatypeURI the URI for a datatyped literal
     * @throws IllegalArgumentException if <var>lexicalForm</var> or
     *                                  <var>datatype</var> are <code>null</code>
     */
    protected AbstractLiteral(String newLexicalForm, URI newDatatypeURI) {

        // Validate "lexicalForm" parameter
        if (null == newLexicalForm) {
            throw new IllegalArgumentException("Null \"lexicalForm\" parameter");
        }

        // Validate "datatype" parameter
        if (null == newDatatypeURI) {
            throw new IllegalArgumentException("Null \"datatype\" parameter");
        }

        // Initialize fields
        value = new DatatypeFactoryImpl().createValue(newLexicalForm, newDatatypeURI);
        language = null;
        datatypeURI = newDatatypeURI;
    }

    public String getLexicalForm() {
        return value.getLexicalForm();
    }

    public Value getValue() {
        return value;
    }

    public String getLanguage() {
        return language;
    }

    /**
     * Whether the literal is well formed XML.
     *
     * @return whether the literal is well formed XML.
     */
    public boolean isWellFormedXML() {
        return value.isWellFormedXml();
    }

    /**
     * Returns the URI of the RDF datatype of this resource, or <code>null</code>
     * for a plain literal.
     *
     * @return the URI of the RDF datatype of this resource, or <code>null</code>
     *         for a plain literal.
     */
    public URI getDatatypeURI() {
        return datatypeURI;
    }

    /**
     * Accept a call from a TypedNodeVisitor.
     *
     * @param visitor the object doing the visiting.
     */
    public void accept(TypedNodeVisitor visitor) {
        visitor.visitLiteral(this);
    }

    public int compareTo(Literal literal) {
        if (language != null) {
            if (language.length() == 0 && datatypeURI == null) {
                return comparePlainLiteral(literal);
            } else {
                return compareLanguageLiteral(literal);
            }
        } else {
            return compareDatatypeLiteral(literal);
        }
    }

    private int comparePlainLiteral(Literal literal) {
        return getLexicalForm().compareTo(literal.getLexicalForm());
    }

    private int compareLanguageLiteral(Literal literal) {
        return getLanguage().compareTo(literal.getLanguage());
    }

    private int compareDatatypeLiteral(Literal literal) {
        //return datatypeUtil.compareTo(this, literal);
        return getValue().compareTo(literal.getValue());
    }

    @Override
    public int hashCode() {
        int hashCode = getValue().hashCode();

        if (null != getDatatypeURI()) {
            hashCode ^= getDatatypeURI().hashCode();
        }

        if (null != getLanguage()) {
            hashCode ^= getLanguage().hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        if (!hasSuperClassOrInterface(Literal.class, obj)) {
            return false;
        }
        return determineEqualityFromFields((Literal) obj);
    }

    /**
     * Provide a legible representation of a literal, following the N-Triples
     * format defined in
     * <a href="http://www.w3.org/TR/2004/REC-rdf-testcases-20040210/#ntrip_strings">&sect;3.2</a>
     * of the <a href="http://www.w3.org/">
     * <acronym title="World Wide Web Consortium">W3C</acronym></a>'s
     * <a href="http://www.w3.org/TR/2004/REC-rdf-testcases-20040210">RDF Test
     * Cases</a> Recommendation.
     * <p/>
     * Well-formed Unicode surrogate pairs in the lexical form are escaped as a
     * single 8-digit hexadecimal <code>\U</code> escape sequence rather than a
     * pair of 4-digit <code>&x5C;u</code> sequences representing the surrogates.
     *
     * @return this instance in N-Triples format
     */
    public String getEscapedForm() {
        String escaped = EscapeUtil.escape(getLexicalForm());
        return '\"' + escaped + '\"' + appendType();
    }

    /**
     * Returns the lexical form.
     *
     * @return the lexical form.
     */
    public String toString() {
        return '\"' + getEscapedLexicalForm() + '\"' + appendType();
    }

    public String getEscapedLexicalForm() {
        return getLexicalForm().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\\\"");
    }

    private boolean determineEqualityFromFields(Literal tmpLiteral) {
        // Ensure that the lexical form is equal character by character.
        if (valuesEqual(tmpLiteral)) {
            return checkLiteralEquality(tmpLiteral);
        }
        return false;
    }

    private boolean checkLiteralEquality(Literal tmpLiteral) {
        boolean returnValue = false;

        // If datatypes are null and languages are equal by value.
        if (dataTypesNull(tmpLiteral) && languagesEqual(tmpLiteral)) {
            returnValue = true;
        // If datatype URIs are not null and equal by their string values.
        } else if (dataTypesEqual(tmpLiteral)) {
            returnValue = true;
        }
        return returnValue;
    }

    private boolean valuesEqual(Literal tmpLiteral) {
        return getValue().equals(tmpLiteral.getValue());
    }

    private boolean dataTypesNull(Literal tmpLiteral) {
        return ((null == getDatatypeURI()) && (null == tmpLiteral.getDatatypeURI()));
    }

    private boolean languagesEqual(Literal tmpLiteral) {
        return getLanguage().equals(tmpLiteral.getLanguage());
    }

    private boolean dataTypesEqual(Literal tmpLiteral) {
        URI tmpLiteralDatatype = tmpLiteral.getDatatypeURI();
        return (null != getDatatypeURI()) && (null != tmpLiteralDatatype) &&
            getDatatypeURI().toString().equals(tmpLiteralDatatype.toString());
    }

    /**
     * Appends the datatype URI or language code of a literal.
     *
     * @return String the datatype URI in the form ^^<->, or language code @- or
     *         an empty string.
     */
    private String appendType() {
        String appendString = "";
        if (null != getDatatypeURI()) {
            appendString = "^^<" + getDatatypeURI() + '>';
        } else if (!"".equals(getLanguage())) {
            appendString = '@' + getLanguage();
        }

        return appendString;
    }
}
