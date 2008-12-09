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

package org.jrdf.parser;

/**
 * The interface to configure RDF parsers.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public interface ParserConfiguration extends StatementHandlerConfiguration {

    /**
     * Constant indicating that datatypes semantics should be ignored.
     */
    int DT_IGNORE = 10;

    /**
     * Constant indicating that values of datatyped literals should be
     * verified.
     */
    int DT_VERIFY = 20;

    /**
     * Constant indicating that values of datatyped literals should be
     * normalized to their canonical representation.
     */
    int DT_NORMALIZE = 30;

    /**
     * Sets the ParseErrorListener that will be notified of any errors
     * that this parser finds during parsing.
     *
     * @param el the error listener.
     */
    void setParseErrorListener(ParseErrorListener el);

    /**
     * Sets the ParseLocationListener that will be notified of the parser's
     * progress during the parse process.
     *
     * @param ll the parser location listener.
     */
    void setParseLocationListener(ParseLocationListener ll);

    /**
     * Sets the NamespaceListener that will be notified of any namespace
     * declarations that the parser finds during parsing.
     *
     * @param nl the namespace listener.
     */
    void setNamespaceListener(NamespaceListener nl);

    /**
     * Sets whether the parser should verify the data it parses (default value
     * is <tt>true</tt>).
     *
     * @param verifyData true to verify the data parsed in.
     */
    void setVerifyData(boolean verifyData);

    // TODO AN Determine whether this should be kept.

    /**
     * Set whether the parser should preserve bnode identifiers specified
     * in the source (default is <tt>false</tt>).
     *
     * @param preserveBNodeIds true to presever blank node identifier.
     */
    void setPreserveBNodeIds(boolean preserveBNodeIds);

    /**
     * Sets the parser in a mode to parse stand-alone RDF documents. In
     * stand-alone RDF documents, the enclosing <tt>rdf:RDF</tt> root element is
     * optional if this root element contains just one element (e.g.
     * <tt>rdf:Description</tt>.
     *
     * @param standAloneDocs true in standad alone mode.
     */
    void setParseStandAloneDocuments(boolean standAloneDocs);

    /**
     * Sets whether the parser should stop immediately if it finds an error
     * in the data (default value is <tt>true</tt>).
     *
     * @param stopAtFirstError true if an error should stop parsing.
     */
    void setStopAtFirstError(boolean stopAtFirstError);

    /**
     * Sets the datatype handling mode. There are three modes for
     * handling datatyped literals: <em>ignore</em>, <em>verify</em>
     * and <em>normalize</em>. If set to <em>ignore</em>, no special
     * action will be taken to handle datatyped literals. If set to
     * <em>verify</em> (the default value), any literals with known
     * (XML Schema built-in) datatypes are checked to see if their
     * values are valid. If set to <em>normalize</em>, the literal
     * values are not only checked, but also normalized to their
     * canonical representation. The default value is <em>verify</em>.
     *
     * @param datatypeHandling One of the constants
     *                         <tt>DT_IGNORE</tt>, <tt>DT_VERIFY</tt> or
     *                         <tt>DT_NORMALIZE</tt>.
     * @see #DT_IGNORE
     * @see #DT_VERIFY
     * @see #DT_NORMALIZE
     */
    void setDatatypeHandling(int datatypeHandling);
}
