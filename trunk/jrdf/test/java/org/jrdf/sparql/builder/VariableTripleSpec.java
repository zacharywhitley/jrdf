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

package org.jrdf.sparql.builder;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import static org.jrdf.sparql.builder.TripleSpecHelper.createAVariable;
import static org.jrdf.sparql.builder.TripleSpecHelper.*;
import org.jrdf.sparql.parser.node.AResourceResourceTripleElement;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.AVariableObjectTripleElement;
import org.jrdf.sparql.parser.node.PObjectTripleElement;
import org.jrdf.sparql.parser.node.PResourceTripleElement;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public final class VariableTripleSpec implements TripleSpec {
    private URI subjectUri;
    private URI predicateUri;
    private String variableName;

    public VariableTripleSpec(URI newSubjectUri, URI newPredicateUri, String newVariableName) {
        this.subjectUri = newSubjectUri;
        this.predicateUri = newPredicateUri;
        this.variableName = newVariableName;
    }

    public Attribute[] asAttributes() {
        List<Attribute> attributes = new ArrayList<Attribute>();
        Attribute subjectAtt = new AttributeImpl(new PositionName("SUBJECT1"), new SubjectNodeType());
        attributes.add(subjectAtt);
        Attribute predciateAtt = new AttributeImpl(new PositionName("PREDICATE1"), new PredicateNodeType());
        attributes.add(predciateAtt);
        Attribute objectAtt = new AttributeImpl(new VariableName(variableName), new ObjectNodeType());
        attributes.add(objectAtt);
        return attributes.toArray(new Attribute[]{});
    }

    public ATriple getTriple() {
        PResourceTripleElement subjectElement = createResourceTripleElement(subjectUri);
        PResourceTripleElement predicateElement = createResourceTripleElement(predicateUri);
        PObjectTripleElement objectElement = new AVariableObjectTripleElement(createAVariable(variableName));
        return new ATriple(subjectElement, predicateElement, objectElement);
    }

    private AResourceResourceTripleElement createResourceTripleElement(URI uri) {
        return new AResourceResourceTripleElement(createResource(uri));
    }
}

