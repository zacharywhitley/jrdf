/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph;

import static org.jrdf.util.EqualsUtil.hasSuperClassOrInterface;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;

import java.io.Serializable;

/**
 * A base implementation of an RDF {@link Triple}.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class AbstractTriple implements Triple, Serializable {
    private static final long serialVersionUID = 8737092494833012690L;
    private static final int DEFAULT_HASH_VALUE = 0;

    /**
     * The internal representation of the triple - subject node.
     */
    protected SubjectNode subjectNode;

    /**
     * The internal representation of the triple - predicate node.
     */
    protected PredicateNode predicateNode;

    /**
     * The internal representation of the triple - object node.
     */
    protected ObjectNode objectNode;

    public SubjectNode getSubject() {
        return subjectNode;
    }

    public PredicateNode getPredicate() {
        return predicateNode;
    }

    public ObjectNode getObject() {
        return objectNode;
    }

    public boolean isGrounded() {
        return !AbstractBlankNode.isBlankNode(subjectNode) && !AbstractBlankNode.isBlankNode(objectNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        if (!hasSuperClassOrInterface(Triple.class, obj)) {
            return false;
        }
        return determineEqualityFromFields((Triple) obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getNodeHashCode(subjectNode) ^ getNodeHashCode(predicateNode) ^ getNodeHashCode(objectNode);
    }

    /**
     * Provide a legible representation of a triple.
     * <p>Currently, square brackets with toString values of the parts of the triple.</p>
     *
     * @return the string value of the subject, predicate and object in square
     *         brackets.
     */
    public String toString() {
        return "[" + subjectNode + ", " + predicateNode + ", " + objectNode + "]";
    }

    private boolean determineEqualityFromFields(Triple ref) {
        return nodesEqual(subjectNode, ref.getSubject()) && nodesEqual(predicateNode, ref.getPredicate()) &&
            nodesEqual(objectNode, ref.getObject());
    }

    private boolean nodesEqual(Node node1, Node node2) {
        if (bothNull(node1, node2)) {
            return true;
        }
        if (eitherNull(node1, node2)) {
            return false;
        }
        return node1.equals(node2);
    }

    // FIXME TJA: Move to utility class
    private boolean eitherNull(Node node1, Node node2) {
        return node1 == null || node2 == null;
    }

    // FIXME TJA: Move to utility class
    private boolean bothNull(Node node1, Node node2) {
        return (node1 == null && node2 == null);
    }

    private int getNodeHashCode(Node node) {
        if (node == null) {
            return DEFAULT_HASH_VALUE;
        }
        return node.hashCode();
    }
}
