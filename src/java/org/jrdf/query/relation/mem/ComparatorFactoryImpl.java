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

package org.jrdf.query.relation.mem;

import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.datatype.LexicalComparatorImpl;
import org.jrdf.graph.datatype.SemanticLiteralComparator;
import org.jrdf.graph.datatype.SemanticLiteralComparatorImpl;
import org.jrdf.graph.local.BlankNodeComparator;
import org.jrdf.graph.local.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.NodeComparatorImpl;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.attributename.AttributeNameComparatorImpl;
import org.jrdf.query.relation.type.TypeComparator;
import org.jrdf.query.relation.type.TypeComparatorImpl;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;


public class ComparatorFactoryImpl implements ComparatorFactory {
    public AttributeComparator createAttributeComparator() {
        return new AttributeComparatorImpl(createTypeComparator(), createNameComparator());
    }

    public TupleComparator createTupleComparator() {
        return new TupleComparatorImpl(createNodeComparator(), createAttributeComparator());
    }

    public NodeComparator createNodeComparator() {
        NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
        BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(
            new LocalizedNodeComparatorImpl());
        SemanticLiteralComparator literalComparator = new SemanticLiteralComparatorImpl(new LexicalComparatorImpl());
        return new NodeComparatorImpl(typeComparator, literalComparator, blankNodeComparator);
    }

    private TypeComparator createTypeComparator() {
        return new TypeComparatorImpl(new NodeTypeComparatorImpl());
    }

    private AttributeNameComparator createNameComparator() {
        return new AttributeNameComparatorImpl();
    }
}
