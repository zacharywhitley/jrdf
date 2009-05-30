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

package org.jrdf.util.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.jrdf.query.relation.type.BlankNodeType;
import org.jrdf.query.relation.type.LiteralNodeType;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.NodeTypeVisitor;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.ResourceNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.query.relation.type.URIReferenceNodeType;

import java.io.Serializable;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class NodeTypeBinding extends TupleBinding<NodeType> implements NodeTypeVisitor, Serializable {
    private static final long serialVersionUID = 331268501059754264L;
    private NodeType object;
    private TupleOutput tupleOutput;
    private static final int BLANK_NODE_TYPE = 0;
    private static final int LITERAL_NODE_TYPE = 1;
    private static final int URI_NODE_TYPE = 2;
    private static final int SUBJECT_NODE_TYPE = 3;
    private static final int PREDICATE_NODE_TYPE = 4;
    private static final int OBJECT_NODE_TYPE = 5;
    private static final int RESOURCE_NODE_TYPE = 6;

    public NodeType entryToObject(TupleInput tupleInput) {
        NodeType nodeType;
        byte b = tupleInput.readByte();
        switch (b) {
            case BLANK_NODE_TYPE:
                nodeType = new BlankNodeType();
                break;
            case LITERAL_NODE_TYPE:
                nodeType = new LiteralNodeType();
                break;
            case URI_NODE_TYPE:
                nodeType = new URIReferenceNodeType();
                break;
            case SUBJECT_NODE_TYPE:
                nodeType = new SubjectNodeType();
                break;
            case PREDICATE_NODE_TYPE:
                nodeType = new PredicateNodeType();
                break;
            case OBJECT_NODE_TYPE:
                nodeType = new ObjectNodeType();
                break;
            case RESOURCE_NODE_TYPE:
                nodeType = new ResourceNodeType();
                break;
            default:
                throw new IllegalArgumentException("Cannot read class type: " + b);
        }
        return nodeType;
    }

    public void objectToEntry(NodeType o, TupleOutput tupleOutput) {
        this.object = o;
        this.tupleOutput = tupleOutput;
        object.accept(this);
    }

    public void visitBlankNodeType(BlankNodeType node) {
        tupleOutput.writeByte(BLANK_NODE_TYPE);
    }

    public void visitLiteralNodeType(LiteralNodeType node) {
        tupleOutput.writeByte(LITERAL_NODE_TYPE);
    }

    public void visitURIReferenceNodeType(URIReferenceNodeType node) {
        tupleOutput.writeByte(URI_NODE_TYPE);
    }

    public void visitSubjectNodeType(SubjectNodeType node) {
        tupleOutput.writeByte(SUBJECT_NODE_TYPE);
    }

    public void visitPredicateNodeType(PredicateNodeType node) {
        tupleOutput.writeByte(PREDICATE_NODE_TYPE);
    }

    public void visitObjectNodeType(ObjectNodeType node) {
        tupleOutput.writeByte(OBJECT_NODE_TYPE);
    }

    public void visitResourceNodeType(ResourceNodeType node) {
        tupleOutput.writeByte(RESOURCE_NODE_TYPE);
    }
}
