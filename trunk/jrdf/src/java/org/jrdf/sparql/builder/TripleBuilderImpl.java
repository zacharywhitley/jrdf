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

package org.jrdf.drql.builder;

import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.mem.AttributeValuePairHelper;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.drql.parser.analysis.DepthFirstAdapter;
import org.jrdf.drql.parser.node.ATriple;
import org.jrdf.drql.parser.parser.ParserException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TripleBuilderImpl extends DepthFirstAdapter implements TripleBuilder {
    // FIXME TJA: Test drive out code to do with graphs, creating triples & resources, etc. into a utility.

    private static final NodeType SUBJECT_NODE_TYPE = new SubjectNodeType();
    private static final NodeType PREDICATE_NODE_TYPE = new PredicateNodeType();
    private static final NodeType OBJECT_NODE_TYPE = new ObjectNodeType();
    private static final NodeType[] TYPES = {SUBJECT_NODE_TYPE, PREDICATE_NODE_TYPE, OBJECT_NODE_TYPE};
    private final AttributeValuePairHelper avpHelper;
    private final Graph graph;
    private final SortedAttributeFactory sortedAttributeFactory;
    private List<AttributeValuePair> avp;
    private Map<String, String> prefixMap = new HashMap<String, String>();
    private ParserException exception;

    public TripleBuilderImpl(Graph graph, AttributeValuePairHelper avpHelper,
            SortedAttributeFactory sortedAttributeFactory) {
        this.avpHelper = avpHelper;
        this.graph = graph;
        this.sortedAttributeFactory = sortedAttributeFactory;
    }

    /**
     * Builds the given <var>tripleNode</var> into a local Triple.
     *
     * @return The local version of the given <var>tripleNode</var>
     */
    public List<AttributeValuePair> getTriples() throws ParserException {
        if (exception != null) {
            throw exception;
        } else {
            return avp;
        }
    }

    public void addPrefix(String identifier, String resource) {
        prefixMap.put(identifier, resource);
    }

    @Override
    public void caseATriple(ATriple node) {
        List<Attribute> heading = sortedAttributeFactory.createHeading(Arrays.asList(TYPES));
        try {
            AttributeValuePair subject = getElement(node.getSubject(), ANY_SUBJECT_NODE, SUBJECT_NODE_TYPE,
                    heading.get(0), graph);
            AttributeValuePair predicate = getElement(node.getPredicate(), ANY_PREDICATE_NODE, PREDICATE_NODE_TYPE,
                    heading.get(1), graph);
            AttributeValuePair object = getElement(node.getObject(), ANY_OBJECT_NODE, OBJECT_NODE_TYPE,
                    heading.get(2), graph);
            avp = avpHelper.createAvp(new AttributeValuePair[]{subject, predicate, object});
        } catch (ParserException e) {
            exception = e;
        }
    }

    private AttributeValuePair getElement(org.jrdf.drql.parser.node.Node node, Node graphNode, NodeType nodeType,
            Attribute attribute, Graph graph) throws ParserException {
        ElementBuilder analyser = new ElementBuilderImpl(nodeType, graphNode, attribute, graph, prefixMap);
        node.apply(analyser);
        return analyser.getElement();
    }
}
