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

package org.jrdf.util.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.graph.local.LiteralImpl;
import org.jrdf.graph.local.LocalizedNode;
import org.jrdf.graph.local.URIReferenceImpl;

import java.io.Serializable;

public class NodeBinding extends TupleBinding implements Serializable {
    private static final long serialVersionUID = 2361309903891433676L;
    private static final byte BLANK_NODE = 0;
    private static final byte URI_REFERENCE = 1;
    private static final byte LITERAL = 2;
    private StringNodeMapper mapper = new StringNodeMapperFactoryImpl().createMapper();

    public Object entryToObject(TupleInput tupleInput) {
        Object object;
        byte b = tupleInput.readByte();
        String str = tupleInput.readString();
        if (b == BLANK_NODE) {
            object = mapper.convertToBlankNode(str);
        } else if (b == URI_REFERENCE) {
            object = mapper.convertToURIReference(str, tupleInput.readLong());
        } else if (b == LITERAL) {
            object = mapper.convertToLiteral(str, tupleInput.readLong());
        } else {
            throw new IllegalArgumentException("Cannot read class type: " + b);
        }
        return object;
    }

    public void objectToEntry(Object object, TupleOutput tupleOutput) {
        if (BlankNode.class.isAssignableFrom(object.getClass())) {
            writeBlankNode(object, tupleOutput);
        } else if (URIReferenceImpl.class.isAssignableFrom(object.getClass())) {
            writeURIReference(object, tupleOutput);
        } else if (LiteralImpl.class.isAssignableFrom(object.getClass())) {
            writeLiteral(object, tupleOutput);
        } else {
            throw new IllegalArgumentException("Cannot persist class of type: " + object.getClass());
        }
    }

    private void writeBlankNode(Object object, TupleOutput tupleOutput) {
        tupleOutput.writeByte(BLANK_NODE);
        tupleOutput.writeString(mapper.convertToString((Node) object));
    }

    private void writeURIReference(Object object, TupleOutput tupleOutput) {
        tupleOutput.writeByte(URI_REFERENCE);
        tupleOutput.writeString(mapper.convertToString((Node) object));
        tupleOutput.writeLong(((LocalizedNode) object).getId());
    }

    private void writeLiteral(Object object, TupleOutput tupleOutput) {
        tupleOutput.writeByte(LITERAL);
        tupleOutput.writeString(mapper.convertToString((Node) object));
        tupleOutput.writeLong(((LocalizedNode) object).getId());
    }
}
