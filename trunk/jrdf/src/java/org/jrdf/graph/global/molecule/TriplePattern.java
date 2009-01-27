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

package org.jrdf.graph.global.molecule;

import org.jrdf.graph.AbstractTriple;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Node;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

public class TriplePattern extends AbstractTriple {
    private static final long serialVersionUID = -1930410091981788384L;

    private NodePattern subject;
    private NodePattern predicate;
    private NodePattern object;

    private TriplePattern() {
    }

    public TriplePattern(Node sbj, Node pre, Node obj) {
        subject = createNodePattern(sbj);
        predicate = createNodePattern(pre);
        object = createNodePattern(obj);
    }

    private NodePattern createNodePattern(Node sbj) {
        NodePattern pattern;
        if ((Object) sbj instanceof NodePattern) {
            pattern = (NodePattern) sbj;
        } else {
            pattern = new NodePatternImpl(sbj);
        }
        return pattern;
    }

    /*public TriplePattern(SubjectNode sbj, PredicateNode pre, ObjectNode obj) {
        subject = new NodePatternImpl(sbj);
        predicate = new NodePatternImpl(pre);
        object = new NodePatternImpl(obj);
    }*/

    public TriplePattern(NodePattern sbj, NodePattern pre, NodePattern obj) {
        subject = sbj;
        predicate = pre;
        object = obj;
    }

    @Override
    public SubjectNode getSubject() {
        return subject;
    }

    @Override
    public PredicateNode getPredicate() {
        return predicate;
    }

    @Override
    public ObjectNode getObject() {
        return object;
    }

    public boolean equals(TriplePattern triple) {
        boolean result;
        if (triple == null) {
            result = false;
        } else {
            return subject.equals(triple.getSubject()) &&
                    predicate.equals(triple.getPredicate()) &&
                    object.equals(triple.getObject());
        }
        return result;
    }

    public static boolean checkTriplesNotNull(TriplePattern pattern) {
        return pattern != null && pattern.getSubject() != null &&
                pattern.getPredicate() != null && pattern.getObject() != null;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return "[" + subject.toString() + ", " + predicate.toString() + ", " + object.toString() + "]";
    }

    public boolean matches(Triple triple) {
        if (predicatesMatch(triple)) {
            if (subjectsMatch(triple)) {
                return objectsMatch(triple);
            }
        }
        return false;
    }

    private boolean subjectsMatch(Triple triple) {
        return subject.equals(ANY_SUBJECT_NODE) || subject.equals(triple.getSubject());
    }

    private boolean predicatesMatch(Triple triple) {
        return predicate.equals(ANY_PREDICATE_NODE) || predicate.equals(triple.getPredicate());
    }

    private boolean objectsMatch(Triple triple) {
        return object.equals(ANY_OBJECT_NODE) || object.equals(triple.getObject());
    }
}
