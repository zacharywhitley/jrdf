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

package org.jrdf.graph.global;

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.NodeComparator;

/**
 * A different implementation of TripleComparator.  Where the number of blank nodes in a triple makes it less than
 * triples without blank nodes.  Triples with the same number of blank nodes are then compared by node type.
 */
public class ReverseGroundedTripleComparatorImpl implements TripleComparator {
    private static final long serialVersionUID = 678535114447666636L;
    private static final int MAXIMUM_NUMBER_OF_GROUNDED_NODES = 3;
    private TripleComparator tripleComparator;

    private ReverseGroundedTripleComparatorImpl() {
    }

    public ReverseGroundedTripleComparatorImpl(TripleComparator newTripleComparator) {
        this.tripleComparator = newTripleComparator;
    }

    public int compare(Triple o1, Triple o2) {
        int result = compareTriples(o1, o2);
        if (result == 0) {
            result = tripleComparator.compare(o1, o2);
        }
        return result;
    }

    private int compareTriples(Triple o1, Triple o2) {
        int numberOfGroundedNodes1 = countGroundNodes(o1);
        int numberOfGroundedNodes2 = countGroundNodes(o2);
        if (numberOfGroundedNodes1 == numberOfGroundedNodes2) {
            return 0;
        } else if (numberOfGroundedNodes1 > numberOfGroundedNodes2) {
            return -1;
        } else {
            return 1;
        }
    }

    private int countGroundNodes(Triple o1) {
        int grounded = MAXIMUM_NUMBER_OF_GROUNDED_NODES;
        if (o1.getSubject() instanceof BlankNode) {
            grounded--;
        }
        if (o1.getObject() instanceof BlankNode) {
            grounded--;
        }
        return grounded;
    }

    public NodeComparator getNodeComparator() {
        return tripleComparator.getNodeComparator();
    }
}