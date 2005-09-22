/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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

package org.jrdf.util.test.instantiate;

import org.jrdf.graph.mem.BlankNodeImpl;
import org.jrdf.graph.mem.LiteralImpl;
import org.jrdf.graph.mem.URIReferenceImpl;
import org.jrdf.graph.mem.index.LongIndexMem;
import org.jrdf.graph.AnyNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.util.test.ReflectTestUtil;
import org.jrdf.query.relation.constants.FalseNode;
import org.jrdf.query.relation.constants.TrueNode;
import org.jrdf.query.DefaultAnswer;

/**
 * Instantiates instances of objects for use in testing.
 * @author Tom Adams
 * @version $Id$
 */
public final class ArnoldTheInstantiator {

    private static final Class<BlankNodeImpl> CLASS_BLANK_NODE_IMPL = BlankNodeImpl.class;
    private static final Class<LiteralImpl> CLASS_LITERAL_IMPL = LiteralImpl.class;
    private static final Class<LongIndexMem> CLASS_LONG_INDEX_MEM = LongIndexMem.class;
    private static final Class<URIReferenceImpl> CLASS_URI_REFERENCE_IMPL = URIReferenceImpl.class;
    private static final Class<FalseNode> CLASS_FALSE_NODE = FalseNode.class;
    private static final Class<TrueNode> CLASS_TRUE_NODE = TrueNode.class;
    private static final Class<AnyNode> CLASS_ANY_NODE = AnyNode.class;
    private static final Class<AnySubjectNode> CLASS_ANY_SUBJECT_NODE = AnySubjectNode.class;
    private static final Class<AnyPredicateNode> CLASS_ANY_PREDICATE_NODE = AnyPredicateNode.class;
    private static final Class<AnyObjectNode> CLASS_ANY_OBJECT_NODE = AnyObjectNode.class;
    private static final Class<DefaultAnswer> CLASS_DEFAULT_ANSWER = DefaultAnswer.class;

    // TODO: Improve this nest of ifs below
    public Object instantiate(Class<?> cls) {
        if (cls.equals(CLASS_BLANK_NODE_IMPL)) return new BlankNodeImplInstantiator().instantiate();
        if (cls.equals(CLASS_LITERAL_IMPL)) return new LiteralImplInstantiator().instantiate();
        if (cls.equals(CLASS_LONG_INDEX_MEM)) return new LongIndexMemInstantiator().instantiate();
        if (cls.equals(CLASS_URI_REFERENCE_IMPL)) return new URIReferenceImplInstantiator().instantiate();
        if (cls.equals(CLASS_FALSE_NODE)) return new FalseNodeInstantiator().instantiate();
        if (cls.equals(CLASS_TRUE_NODE)) return new TrueNodeInstantiator().instantiate();
        if (cls.equals(CLASS_ANY_NODE)) return new AnyNodeInstantiator().instantiate();
        if (cls.equals(CLASS_ANY_SUBJECT_NODE)) return new AnySubjectNodeInstantiator().instantiate();
        if (cls.equals(CLASS_ANY_PREDICATE_NODE)) return new AnyPredicateNodeInstantiator().instantiate();
        if (cls.equals(CLASS_ANY_OBJECT_NODE)) return new AnyObjectNodeInstantiator().instantiate();
        if (cls.equals(CLASS_DEFAULT_ANSWER)) return new DefaultAnswerInstantiator().instantiate();
        return newInstance(cls);
    }

    private Object newInstance(Class<?> cls) {
        return ReflectTestUtil.newInstance(cls);
    }
}
