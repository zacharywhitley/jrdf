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

package org.jrdf.graph.global.molecule;

import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.TypedNodeVisitor;

import java.util.UUID;

public class NodePatternImpl implements NodePattern {
    private int id;
    private String string;
    private Node node;
    private static final String NP_PREFIX = "NodePattern#";


    public NodePatternImpl() {
        string = NP_PREFIX + UUID.randomUUID().toString();
        id = string.hashCode();
        node = null;
    }

    public NodePatternImpl(Node node) {
        if (node == null) {
            string = NP_PREFIX + UUID.randomUUID().toString();
            id = string.hashCode();
            this.node = null;
        } else {
            string = node.toString();
            id = node.hashCode();
            this.node = node;
        }
    }

    public int hashCode() {
        return id;
    }

    public boolean matches(Node node) {
        if (node == null) {
            return false;
        } else if (this.node != null && !this.isAnyNode() && !this.isAnyNode(node)) {
            return this.node.equals(node);
        } else {
            return true;
        }

    }

    public boolean isAnyNode() {
        return isAnyNode(node);
    }

    public boolean isAnyNode(Node node) {
        boolean result = false;
        if (node != null) {
            if (node == AnySubjectNode.ANY_SUBJECT_NODE ||
                    node == AnyPredicateNode.ANY_PREDICATE_NODE ||
                    node == AnyObjectNode.ANY_OBJECT_NODE) {
                result = true;
            }
        }
        return result;
    }

    public void accept(TypedNodeVisitor visitor) {
        // do nothing;
    }

    @Override
    public String toString() {
        if (node == null) {
            return string;
        } else {
            return NP_PREFIX + node.toString();
        }
    }

    public boolean equals(Object pattern) {
        if (pattern == null || !NodePatternImpl.isNodePattern(pattern)) {
            return false;
        }
        return this.hashCode() == pattern.hashCode();
    }

    public static boolean isNodePattern(Object node) {
        if (node == null) {
            return false;
        } else {
            return NodePattern.class.isAssignableFrom(node.getClass());
        }
    }
}
