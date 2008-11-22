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

package org.jrdf.graph.local.index.nodepool;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.BlankNodeImpl;
import org.jrdf.graph.local.LiteralImpl;
import org.jrdf.graph.local.LiteralMutableId;
import org.jrdf.graph.local.URIReferenceImpl;
import org.jrdf.parser.ntriples.parser.LiteralMatcher;

import java.io.Serializable;
import java.net.URI;

// TODO N3 Changes - this and RegexLiteralMatcher - sharing regex pattern.
public class StringNodeMapperImpl implements StringNodeMapper, Serializable {
    private static final long serialVersionUID = 6290485805443126422L;
    private static final String PATTERN = "\\\"([\\t\\r\\n\\x20-\\x7E]*)\\\"" +
        "(" +
        "((\\@(\\p{Lower}+(\\-a-z0-9]+)*))|(\\^\\^\\<([\\x20-\\x7E]+)\\>))?" +
        ").*";
    private LiteralMatcher literalMatcher;
    private String currentString;

    private StringNodeMapperImpl() {
    }

    public StringNodeMapperImpl(LiteralMatcher newLiteralMatcher) {
        literalMatcher = newLiteralMatcher;
        literalMatcher.setPattern(PATTERN);
    }

    public String convertToString(Node node) {
        if (node != ANY_SUBJECT_NODE && node != ANY_PREDICATE_NODE && node != ANY_OBJECT_NODE) {
            node.accept(this);
            return currentString;
        } else {
            return null;
        }
    }

    public BlankNode convertToBlankNode(String string) {
        return BlankNodeImpl.valueOf(string);
    }

    public URIReference convertToURIReference(String string, Long nodeId) {
        return new URIReferenceImpl(URI.create(string), nodeId);
    }

    public Literal convertToLiteral(String string, Long nodeId) {
        String[] strings = literalMatcher.parse(string);
        String lexicalForm = strings[0];
        String language = strings[1];
        String datatype = strings[2];
        Literal literal;
        if (language != null) {
            literal = new LiteralImpl(lexicalForm, language);
        } else if (datatype != null) {
            literal = new LiteralImpl(lexicalForm, URI.create(datatype));
        } else {
            literal = new LiteralImpl(lexicalForm);
        }
        ((LiteralMutableId) literal).setId(nodeId);
        return literal;
    }

    public void visitBlankNode(BlankNode blankNode) {
        currentString = blankNode.toString();
    }

    public void visitURIReference(URIReference uriReference) {
        currentString = uriReference.getURI().toString();
    }

    public void visitLiteral(Literal literal) {
        currentString = literal.getEscapedForm();
    }

    public void visitNode(Node node) {
        illegalNode(node);
    }

    public void visitResource(Resource resource) {
        illegalNode(resource);
    }

    private void illegalNode(Node node) {
        throw new IllegalArgumentException("Failed to convert node: " + node);
    }
}
