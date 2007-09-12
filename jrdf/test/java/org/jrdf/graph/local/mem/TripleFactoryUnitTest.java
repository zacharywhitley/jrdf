/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.local.mem;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.AbstractTripleFactoryUnitTest;
import org.jrdf.graph.Alternative;
import org.jrdf.graph.Bag;
import org.jrdf.graph.Collection;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Sequence;
import org.jrdf.graph.TripleFactoryException;
import org.jrdf.vocabulary.RDF;

/**
 * Implementation of {@link AbstractTripleFactoryUnitTest} test
 * case.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public class TripleFactoryUnitTest extends AbstractTripleFactoryUnitTest {

    /**
     * Constructs a new test with the given name.
     *
     * @param name the name of the test
     */
    public TripleFactoryUnitTest(String name) {
        super(name);
    }

    /**
     * Create a graph implementation.
     *
     * @return A new GraphImplUnitTest.
     */
    public Graph newGraph() throws Exception {
        return TestJRDFFactory.getFactory().getNewGraph();
    }

    /**
     * Hook for test runner to obtain a test suite from.
     *
     * @return The test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(TripleFactoryUnitTest.class);
        suite.addTest(new TripleFactoryUnitTest("testReification"));
        suite.addTest(new TripleFactoryUnitTest("testCollections"));
        suite.addTest(new TripleFactoryUnitTest("testAlternative"));
        suite.addTest(new TripleFactoryUnitTest("testBag"));
        suite.addTest(new TripleFactoryUnitTest("testSequence"));
        return suite;
    }

    /**
     * Default test runner.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) throws Exception {
        TestRunner.run(suite());
    }

    public PredicateNode getReifySubject() throws TripleFactoryException {
        try {
            return elementFactory.createURIReference(RDF.SUBJECT);
        }
        catch (GraphElementFactoryException gefe) {
            throw new TripleFactoryException(gefe);
        }
    }

    public PredicateNode getReifyPredicate() throws TripleFactoryException {
        try {
            return elementFactory.createURIReference(RDF.PREDICATE);
        }
        catch (GraphElementFactoryException gefe) {
            throw new TripleFactoryException(gefe);
        }
    }

    public PredicateNode getReifyObject() throws TripleFactoryException {
        try {
            return elementFactory.createURIReference(RDF.OBJECT);
        }
        catch (GraphElementFactoryException gefe) {
            throw new TripleFactoryException(gefe);
        }
    }

    public PredicateNode getRdfType() throws TripleFactoryException {
        try {
            return elementFactory.createURIReference(RDF.TYPE);
        }
        catch (GraphElementFactoryException gefe) {
            throw new TripleFactoryException(gefe);
        }
    }

    public ObjectNode getRdfStatement() throws TripleFactoryException {
        try {
            return elementFactory.createURIReference(RDF.STATEMENT);
        }
        catch (GraphElementFactoryException gefe) {
            throw new TripleFactoryException(gefe);
        }
    }

    public Collection createCollection(ObjectNode[] objects) {
        Collection collection = new CollectionImpl();

        for (ObjectNode object : objects) {
            collection.add(object);
        }

        return collection;
    }

    public Alternative createAlternative(ObjectNode[] objects) {
        Alternative alternative = new AlternativeImpl();

        for (ObjectNode object : objects) {
            alternative.add(object);
        }

        return alternative;
    }

    public Bag createBag(ObjectNode[] objects) {
        Bag bag = new BagImpl();

        for (ObjectNode object : objects) {
            bag.add(object);
        }

        return bag;
    }

    public Sequence createSequence(ObjectNode[] objects) {
        Sequence sequence = new SequenceImpl();

        for (ObjectNode object : objects) {
            sequence.add(object);
        }

        return sequence;
    }
}
