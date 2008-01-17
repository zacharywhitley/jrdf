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

package org.jrdf.graph.local.index.graphhandler;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.operation.mem.BasicOperations;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Turn this into delegation rather than inheritance?
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractGraphHandler implements GraphHandler {
    protected NodePool nodePool;
    private static final int STATEMENT_OFFSET = 5;

    public void reconstructIndices(LongIndex firstIndex, LongIndex secondIndex, LongIndex thirdIndex) throws
        GraphException {
        BasicOperations.reconstruct(firstIndex, secondIndex, thirdIndex);
    }

    /**
     * Debug method to see the current state of the first index.
     */
    public void dumpIndex(PrintStream out) {
        // TODO AN Now this is smaller test drive.
        Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator = getEntries();
        while (iterator.hasNext()) {
            printSubjects(out, iterator.next());
        }
    }

    private void printSubjects(PrintStream out, Map.Entry<Long, Map<Long, Set<Long>>> subjectEntry) {
        Map<Long, Set<Long>> secondIndex = subjectEntry.getValue();

        String subject = subjectEntry.getKey().toString();
        Iterator<Map.Entry<Long, Set<Long>>> predIterator = secondIndex.entrySet().iterator();

        out.print(subject + " --> ");

        if (!predIterator.hasNext()) {
            out.println("X");
        } else {
            int sWidth = subject.length() + STATEMENT_OFFSET;
            printPredicates(out, predIterator, createSpaces(sWidth));
        }
    }

    private void printPredicates(PrintStream out, Iterator<Map.Entry<Long, Set<Long>>> predIterator, String spaces) {
        int numberOfPredicates = 0;
        while (predIterator.hasNext()) {
            Map.Entry<Long, Set<Long>> predicateEntry = predIterator.next();

            String predicate = predicateEntry.getKey().toString();
            Iterator<Long> objIterator = predicateEntry.getValue().iterator();

            if (++numberOfPredicates > 1) {
                out.print(spaces);
            }
            out.print(predicate + " --> ");

            if (!objIterator.hasNext()) {
                out.println("X");
            } else {
                int pWidth = predicate.length() + STATEMENT_OFFSET;
                printObjects(out, objIterator, spaces + createSpaces(pWidth));
            }
        }
    }

    private void printObjects(PrintStream out, Iterator<Long> objIterator, String spaces) {
        if (objIterator.hasNext()) {
            out.println(objIterator.next());
            while (objIterator.hasNext()) {
                out.println(spaces + objIterator.next());
            }
        }
    }

    private String createSpaces(int numberOfSpaces) {
        StringBuilder space = new StringBuilder(numberOfSpaces);
        space.setLength(numberOfSpaces);
        for (int c = 0; c < numberOfSpaces; c++) {
            space.setCharAt(c, ' ');
        }
        return space.toString();
    }
}