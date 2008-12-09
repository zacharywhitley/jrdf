/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.util.test.instantiate;

import org.jrdf.graph.AnyNode;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.local.index.longindex.mem.LongIndexMem;
import org.jrdf.graph.local.BlankNodeImpl;
import org.jrdf.graph.local.LiteralImpl;
import org.jrdf.graph.local.URIReferenceImpl;
import org.jrdf.query.answer.SelectAnswerImpl;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.relation.constants.NullaryTuple;
import org.jrdf.urql.parser.node.ATriple;
import org.jrdf.util.test.ReflectTestUtil;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Instantiates classes for use in testing.
 *
 * @author Tom Adams
 * @version $Id$
 */
public final class ArnoldTheInstantiator {

    private final Map<Class, Instantiator> instantiators = new HashMap<Class, Instantiator>();

    public ArnoldTheInstantiator() {
        addKnownInstantiators();
    }

    public Object instantiate(Class<?> cls) {
        if (instantiators.containsKey(cls)) {
            return instantiators.get(cls).instantiate();
        }
        return newInstance(cls);
    }

    private Object newInstance(Class<?> cls) {
        return ReflectTestUtil.newInstance(cls);
    }

    private void addKnownInstantiators() {
        instantiators.put(BlankNodeImpl.class, new BlankNodeImplInstantiator());
        instantiators.put(LiteralImpl.class, new LiteralImplInstantiator());
        instantiators.put(LongIndexMem.class, new LongIndexMemInstantiator());
        instantiators.put(URIReferenceImpl.class, new URIReferenceImplInstantiator());
        instantiators.put(NullaryTuple.class, new TrueNodeInstantiator());
        instantiators.put(AnyNode.class, new AnyNodeInstantiator());
        instantiators.put(AnySubjectNode.class, new AnySubjectNodeInstantiator());
        instantiators.put(AnyPredicateNode.class, new AnyPredicateNodeInstantiator());
        instantiators.put(AnyObjectNode.class, new AnyObjectNodeInstantiator());
        instantiators.put(SelectAnswerImpl.class, new AnswerInstantiator());
        instantiators.put(SingleConstraint.class, new ConstraintTripleInstantiator());
        instantiators.put(URL.class, new URLInstantiator());
        instantiators.put(URI.class, new URIInstantiator());
        instantiators.put(String.class, new StringInstantiator());
        instantiators.put(Long.class, new LongInstantiator());
        instantiators.put(ATriple.class, new ATripleInstantiator());
        instantiators.put(Set.class, new SetInstantiator());
        instantiators.put(Map.class, new MapInstantiator());
    }

    private static class URLInstantiator implements Instantiator {
        public Object instantiate() {
            try {
                return new java.net.URL("file:///this/is/anything");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private static class URIInstantiator implements Instantiator {
        public Object instantiate() {
            try {
                return new java.net.URI("http://www.slashdot.org");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class StringInstantiator implements Instantiator {
        public Object instantiate() {
            return "Hello";
        }
    }

    private static class ATripleInstantiator implements Instantiator {
        public Object instantiate() {
            return new ATriple();
        }
    }

    private static class SetInstantiator implements Instantiator {
        public Object instantiate() {
            return new HashSet();
        }
    }

    private static class MapInstantiator implements Instantiator {
        public Object instantiate() {
            return new HashMap();
        }
    }
}

