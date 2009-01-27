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

import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TypedNodeVisitor;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.index.nodepool.Localizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Create a string represetation of a molecule that follows basic NTriples escaping.  This is to be used primarily
 * for serialization or similar purposes.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class MoleculeToText implements MoleculeHandler, TypedNodeVisitor {
    private static final String BLANK_NODE_PREFIX = "_:";
    private static final String SPACE = " ";
    private static final String MOLECULE_START = "[";
    private static final String MOLECULE_END = "]";
    private static final String LF = "\n";
    private static final String FULL_STOP = ".";
    private Map<BlankNode, Long> visitedBlankNodes = new HashMap<BlankNode, Long>();
    private long currentId;
    private StringBuilder builder;
    private String nodeAsString;
    private int level;
    private Localizer localizer;

    public MoleculeToText(StringBuilder newBuilder) {
        this.builder = newBuilder;
    }

    public MoleculeToText(StringBuilder newBuilder, Localizer localizer) {
        this(newBuilder);
        this.localizer = localizer;
    }

    public void handleTriple(Triple triple) {
        builder.append(LF);
        printIndentation();
        appendNode(triple.getSubject());
        builder.append(SPACE);
        appendNode(triple.getPredicate());
        builder.append(SPACE);
        appendNode(triple.getObject());
        builder.append(SPACE);
        builder.append(FULL_STOP);
    }

    private void appendNode(Node node) {
        node.accept(this);
        builder.append(nodeAsString);
    }

    public void handleEmptyMolecules() {
    }

    public void handleStartContainsMolecules(Set<Molecule> newMolecules) {
        builder.append(LF);
        printIndentation();
        builder.append(MOLECULE_START);
        level++;
    }

    private void printIndentation() {
        for (int index = 0; index < level; index++) {
            builder.append(SPACE).append(SPACE);
        }
    }

    public void handleEndContainsMolecules(Set<Molecule> newMolecules) {
        builder.append(LF);
        level--;
        printIndentation();
        builder.append(MOLECULE_END);
    }

    public void visitBlankNode(BlankNode blankNode) {
        long nodeId;
        if (localizer != null) {
            try {
                nodeId = localizer.localize(blankNode);
            } catch (GraphException e) {
                throw new RuntimeException("Cannot get ID for blank node: " + blankNode.toString());
            }
        } else if (visitedBlankNodes.keySet().contains(blankNode)) {
            nodeId = visitedBlankNodes.get(blankNode);
        } else {
            currentId++;
            visitedBlankNodes.put(blankNode, currentId);
            nodeId = currentId;
        }
        nodeAsString = BLANK_NODE_PREFIX + "a" + nodeId;
    }

    public void visitURIReference(URIReference uriReference) {
        nodeAsString = "<" + uriReference.getURI() + ">";
    }

    public void visitLiteral(Literal literal) {
        nodeAsString = literal.getEscapedForm();
    }

    public void visitNode(Node node) {
    }

    public void visitResource(Resource resource) {
    }
}