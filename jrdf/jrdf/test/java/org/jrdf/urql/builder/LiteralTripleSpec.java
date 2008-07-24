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

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.urql.parser.node.ALetterLexicalCharacter;
import org.jrdf.urql.parser.node.ALexicalValue;
import org.jrdf.urql.parser.node.ALiteralObjectTripleElement;
import org.jrdf.urql.parser.node.AQuotedLiteralLiteralValue;
import org.jrdf.urql.parser.node.ARdfLiteralLiteral;
import org.jrdf.urql.parser.node.AResource;
import org.jrdf.urql.parser.node.AResourceResourceTripleElement;
import org.jrdf.urql.parser.node.ATriple;
import org.jrdf.urql.parser.node.AUntypedLiteralRdfLiteral;
import org.jrdf.urql.parser.node.PLexicalCharacter;
import org.jrdf.urql.parser.node.PLexicalValue;
import org.jrdf.urql.parser.node.PLiteralValue;
import org.jrdf.urql.parser.node.PObjectTripleElement;
import org.jrdf.urql.parser.node.PResource;
import org.jrdf.urql.parser.node.PResourceTripleElement;
import org.jrdf.urql.parser.node.TEndurl;
import org.jrdf.urql.parser.node.TLetter;
import org.jrdf.urql.parser.node.TQuote;
import org.jrdf.urql.parser.node.TStarturl;
import org.jrdf.urql.parser.node.TUrlchar;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public final class LiteralTripleSpec implements TripleSpec {
    private URI subjectUri;
    private URI predicateUri;
    private String literal;


    public LiteralTripleSpec(URI subjectUri, URI predicateUri, String literal) {
        this.subjectUri = subjectUri;
        this.predicateUri = predicateUri;
        this.literal = literal;
    }

    public Attribute[] asAttributes() {
        List<Attribute> attributes = new ArrayList<Attribute>();
        Attribute subjectAtt = new AttributeImpl(new PositionName("SUBJECT1"), new SubjectNodeType());
        attributes.add(subjectAtt);
        Attribute predciateAtt = new AttributeImpl(new PositionName("PREDICATE1"), new PredicateNodeType());
        attributes.add(predciateAtt);
        Attribute objectAtt = new AttributeImpl(new PositionName("OBJECT1"), new ObjectNodeType());
        attributes.add(objectAtt);
        return attributes.toArray(new Attribute[]{});
    }

    public ATriple getTriple() {
        PResourceTripleElement subjectElement = createResourceTripleElement(subjectUri);
        PResourceTripleElement predicateElement = createResourceTripleElement(predicateUri);
        PObjectTripleElement objectElement = createLiteralTripleElement(literal);
        return new ATriple(subjectElement, predicateElement, objectElement);
    }

    private PObjectTripleElement createLiteralTripleElement(String object) {
        List<PLexicalCharacter> restOfChars = new ArrayList<PLexicalCharacter>();
        for (int i = 1; i < object.length() - 1; i++) {
            restOfChars.add(new ALetterLexicalCharacter(new TLetter(object.substring(i, i + 1))));
        }
        PLexicalValue strand = new ALexicalValue();
        PLiteralValue literalValue = new AQuotedLiteralLiteralValue(new TQuote("'"), strand, new TQuote("'"));
        return new ALiteralObjectTripleElement(new ARdfLiteralLiteral(new AUntypedLiteralRdfLiteral(literalValue)));
    }

    private AResourceResourceTripleElement createResourceTripleElement(URI uri) {
        String s = uri.toString();
        List<TUrlchar> restOfChars = new ArrayList<TUrlchar>();
        for (int i = 1; i < s.length() - 1; i++) {
            restOfChars.add(new TUrlchar(s.substring(i, i + 1)));
        }
        PResource resource = new AResource(new TStarturl(), restOfChars, new TEndurl());
        return new AResourceResourceTripleElement(resource);
    }

}
