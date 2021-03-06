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
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.constants.NullaryAttribute;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.type.NodeType;

import java.io.Serializable;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class AttributeBinding extends TupleBinding<Attribute> implements Serializable {
    private static final long serialVersionUID = -6106464996541411484L;
    private static final int NORMAL_ATTRIBUTE = 0;
    private static final int NULLARY_ATTRIBUTE = 1;
    private TupleBinding<AttributeName> nameBinding = new AttributeNameBinding();
    private TupleBinding<NodeType> typeBinding = new NodeTypeBinding();

    public Attribute entryToObject(TupleInput tupleInput) {
        final byte b = tupleInput.readByte();
        Attribute attribute;
        if (b == NORMAL_ATTRIBUTE) {
            AttributeName name = nameBinding.entryToObject(tupleInput);
            NodeType type = typeBinding.entryToObject(tupleInput);
            attribute = new AttributeImpl(name, type);
        } else if (b == NULLARY_ATTRIBUTE) {
            attribute = NullaryAttribute.NULLARY_ATTRIBUTE;
        } else {
            throw new IllegalArgumentException("Cannot read class type: " + b);
        }
        return attribute;
    }

    public void objectToEntry(Attribute attribute, TupleOutput tupleOutput) {
        if (attribute instanceof AttributeImpl) {
            tupleOutput.writeByte(NORMAL_ATTRIBUTE);
            nameBinding.objectToEntry(attribute.getAttributeName(), tupleOutput);
            typeBinding.objectToEntry(attribute.getType(), tupleOutput);
        } else if (attribute instanceof NullaryAttribute) {
            tupleOutput.writeByte(NULLARY_ATTRIBUTE);
        }
    }
}
