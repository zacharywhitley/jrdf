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

package org.jrdf.query.answer;

/**
 * A model for writing SPARQL answers.  Simply call writeFullDocument or call the individual sections.
 */
public interface AnswerWriter {
    /**
     * Returns true if there are more results to write.
     *
     * @return true if there are more results to write.
     */
    boolean hasMoreResults();

    /**
     * The start of the document - an XML header or initial JSON object.
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void writeStartDocument() throws Exception;

    /**
     * Writes the entire header - containing the link and all variables.
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void writeHead() throws Exception;

    /**
     * Contains the start of the results.
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void writeStartResults() throws Exception;

    /**
     * Writes out a single binding of value to variable.
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void writeResult() throws Exception;

    /**
     * Contains the end of the results.
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void writeEndResults() throws Exception;

    /**
     * The end of the document.
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void writeEndDocument() throws Exception;

    /**
     * Equivalent to calling writeStartDocument, writeHead, writeStartResults, for all results writeResult,
     * writeEndResults and writeEndDocument.
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void writeFullDocument() throws Exception;

    /**
     * Flushes to the underlying stream/writer.
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void flush() throws Exception;

    /**
     * Closes the underlying resources (streams, writers, etc).
     *
     * @throws Exception if there is an i/o or other exception.
     */
    void close() throws Exception;
}
