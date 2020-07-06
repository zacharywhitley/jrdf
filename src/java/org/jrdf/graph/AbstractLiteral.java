/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph;

import org.jrdf.graph.datatype.DatatypeFactory;
import org.jrdf.graph.datatype.DatatypeFactoryImpl;
import org.jrdf.graph.datatype.DatatypeValue;
import org.jrdf.util.EscapeUtil;

import java.io.Serializable;
import java.net.URI;

import static org.jrdf.graph.NullURI.NULL_URI;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

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
     * Allos the creation of data types.
     */
    private final transient DatatypeFactory datatypeFactory = DatatypeFactoryImpl.getInstance();

    /**
     * The lexical form of the literal.
     */
    protected DatatypeValue value;

    /**
     * The language code of the literal.
     */
    protected String language;

    /**
     * RDF datatype URI, <code>NullURI</code> for untyped literal.
     */
    protected URI datatypeURI;

    /**
     * Cached version of escaped form
     */
    protected String escapedForm;

    protected AbstractLiteral() {
    }

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
        value = datatypeFactory.createValue(newLexicalForm);
        language = "";
        datatypeURI = NULL_URI;
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
        value = datatypeFactory.createValue(newLexicalForm);
        language = newLanguage;
        datatypeURI = NULL_URI;
    }

    /**
     * Construct a datatyped literal.
     *
     * @param newLexicalForm the text part of the literal
     * @param newDatatypeURI the URI for a datatyped literal
     * @throws IllegalArgumentException if <var>lexicalForm</var> or <var>datatype</var> are <code>null</code>
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
        value = datatypeFactory.createValue(newDatatypeURI, newLexicalForm);
        language = "";
        datatypeURI = newDatatypeURI;
    }

    /**
     * Construct a datatype literal based on an existing Java class.
     *
     * @param newObject the object to use to construct the literal from.  Uses a map of registered classes to
     *   creator objects.
     * @throws IllegalArgumentException if the class is not registered to convert to a datatype.
     */
    protected AbstractLiteral(Object newObject) {
        checkNotNull(newObject);
        if (datatypeFactory.hasClassRegistered(newObject.getClass())) {
            value = datatypeFactory.createValue(newObject);
            language = "";
            datatypeURI = datatypeFactory.getObjectDatatypeURI(newObject);
        } else {
            throw new IllegalArgumentException("Class not registered with datatype factory: " + newObject.getClass());
        }
    }

    public String getLexicalForm() {
        return value.getLexicalForm();
    }

    public Object getValue() {
        return value.getValue();
    }

    public DatatypeValue getDatatypeValue() {
        return value;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isDatatypedLiteral() {
        return datatypeURI != NULL_URI;
    }

    public boolean isLanguageLiteral() {
        return language.length() > 0;
    }

    public boolean isPlainLiteral() {
        return datatypeURI == NULL_URI && language.length() == 0;
    }

    /**
     * Whether the literal is well formed XML.
     *
     * @return whether the literal is well formed XML.
     */
    public boolean isWellFormedXML() {
        return value.isWellFormedXML();
    }

    /**
     * Returns the URI of the RDF datatype of this resource, or <code>NO_DATATYPE</code> for a plain literal.
     *
     * @return the URI of the RDF datatype of this resource, or <code>NO_DATATYPE</code> for a plain literal.
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

    @Override
    public int hashCode() {
        // This is to allow same values but different data types to still be equal.
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        try {
            return getEscapedForm().equals(((Literal) obj).getEscapedForm());
        } catch (ClassCastException cce) {
            return false;
        }
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
        if (escapedForm == null) {
            StringBuffer buffer = new StringBuffer(EscapeUtil.escape(getLexicalForm()));
            buffer.insert(0, "\"");
            buffer.append("\"");
            appendType(buffer);
            escapedForm = buffer.toString();
        }
        return escapedForm;
    }

    /**
     * Returns the lexical form.
     *
     * @return the lexical form.
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer('\"' + getEscapedLexicalForm() + '\"');
        appendType(buffer);
        return buffer.toString();
    }

    public String getEscapedLexicalForm() {
        return getLexicalForm().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\\\"");
    }

    /**
     * Appends the datatype URI or language code of a literal.
     *
     * @param buffer The buffer to append the relevant datatype to.
     */
    private void appendType(StringBuffer buffer) {
        if (!NULL_URI.equals(datatypeURI)) {
            buffer.append("^^<").append(datatypeURI).append('>');
        } else if (language.length() > 0) {
            buffer.append('@').append(language);
        }
    }
}
