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

package org.jrdf.example.performance;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.collection.MapFactory;
import org.jrdf.writer.BlankNodeRegistry;

public abstract class AbstractGraphPerformance implements GraphPerformance {
    private static final int NUMBER_OF_NODES_TO_ADD = 10000;
    private static final int NUMBER_OF_NODES_TO_FIND = 1000;
    private static final int NUMBER_OF_NODES_TO_UPDATE = 1000;
    private static final int NO_MILLISECONDS_IN_A_SECOND = 1000;
    private static final int NUMBER_OF_PREDICATES = 10;
    private static final int EXPECTED_ARGS = 3;
    private static final String SUBJECT_PREFIX = "http://foo";
    private static final String PREDICATE_PREFIX = "http://bar";
    private static final String OBJECT_PREFIX = "http://foo";

    public void testPerformance(String[] args) throws Exception {
        if (args.length != EXPECTED_ARGS) {
            testPerformance(0, 0, 0);
        } else {
            testPerformance(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        }
    }

    private void testPerformance(int numberToAdd, int numberToFind, int numberToUpdate) throws Exception {
        checkParameters(numberToAdd, numberToFind, numberToUpdate);
        Graph graph = getGraph();
        //new ParsePerformanceImpl(getMapFactory()).parse(graph, this);
        new AddPerformanceImpl(NUMBER_OF_PREDICATES, SUBJECT_PREFIX, PREDICATE_PREFIX, OBJECT_PREFIX).addPerformance(
            numberToAdd == 0 ? NUMBER_OF_NODES_TO_ADD : numberToAdd, graph, this);
        new WritePerformanceImpl().writePerformance(graph, this, getBlankNodeRegistry());
        new FindPerformanceImpl(numberToFind == 0 ? NUMBER_OF_NODES_TO_FIND : numberToFind, SUBJECT_PREFIX
        ).findPerformance(graph, this);
        new UpdatePerformanceImpl(numberToUpdate == 0 ? NUMBER_OF_NODES_TO_UPDATE : numberToUpdate, SUBJECT_PREFIX).
            updatePerformance(graph, this);
    }

    private void checkParameters(int numberToAdd, int numberToFind, int numberToUpdate) {
        if (numberToAdd != 0 && (numberToAdd < numberToFind || numberToAdd < numberToUpdate)) {
            throw new IllegalArgumentException("Can't find or update more than the number to add: " + numberToAdd);
        }
    }

    protected abstract Graph getGraph();

    protected abstract MapFactory getMapFactory();

    protected abstract BlankNodeRegistry getBlankNodeRegistry();

    public void outputResult(Graph graph, long startTime, String what) throws GraphException {
        long finishTime = System.currentTimeMillis();
        System.out.println("\n" + what);
        System.out.println("Triples: " + graph.getNumberOfTriples() + " Took: " + (finishTime - startTime) +
            " ms = " + ((finishTime - startTime) / NO_MILLISECONDS_IN_A_SECOND) + " s");
    }
}
