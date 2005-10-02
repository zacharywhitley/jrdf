/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.mem.index.operation;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.index.LongIndex;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Just a spike please ignore.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class BasicOperations {
    // TODO AN Refactor and bring in the AbstractGraphHandler operation.

    public static void copyEntriesToIndex(LongIndex index1, LongIndex newIndex) throws GraphException {
        Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> subjectIterator = index1.iterator();
        while (subjectIterator.hasNext()) {
            Map.Entry<Long, Map<Long, Set<Long>>> entry = subjectIterator.next();
            Long subject = entry.getKey();
            Map<Long, Set<Long>> predicateMap = entry.getValue();
            Set<Map.Entry<Long, Set<Long>>> predicateObjectEntries = predicateMap.entrySet();
            addPredicates(predicateObjectEntries, newIndex, subject);
        }
    }

    private static void addPredicates(Set<Map.Entry<Long, Set<Long>>> predicateObjectEntries, LongIndex newIndex,
            Long subject) throws GraphException {
        for (Map.Entry<Long, Set<Long>> predicateObjectSet : predicateObjectEntries) {
            Long predicate = predicateObjectSet.getKey();
            Set<Long> objectSet = predicateObjectSet.getValue();
            addObjects(objectSet, newIndex, subject, predicate);
        }
    }

    private static void addObjects(Set<Long> objectSet, LongIndex newIndex, Long subject, Long predicate) throws
            GraphException {
        for (Long object : objectSet) {
            newIndex.add(subject, predicate, object);
        }
    }

    public static void removeEntriesFromIndex(LongIndex index1, LongIndex newIndex) {
        Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> subjectIterator = index1.iterator();
        while (subjectIterator.hasNext()) {
            Map.Entry<Long, Map<Long, Set<Long>>> entry = subjectIterator.next();
            Long subject = entry.getKey();
            Map<Long, Set<Long>> newIndexPredicateMap = newIndex.getSubIndex(subject);
            removePredicates(newIndexPredicateMap, entry);
        }
    }

    private static void removePredicates(Map<Long, Set<Long>> newIndexPredicateMap, Map.Entry<Long, Map<Long,
            Set<Long>>> entry) {
        if (newIndexPredicateMap != null) {
            Map<Long, Set<Long>> predicateMap = entry.getValue();
            Set<Map.Entry<Long, Set<Long>>> predicateObjectEntries = predicateMap.entrySet();
            for (Map.Entry<Long, Set<Long>> predicateObjectSet : predicateObjectEntries) {
                Long predicate = predicateObjectSet.getKey();
                Set<Long> newIndexObjectSet = newIndexPredicateMap.get(predicate);
                removeObjects(newIndexObjectSet, predicateObjectSet);
            }
        }
    }

    private static void removeObjects(Set<Long> newIndexObjectSet, Map.Entry<Long, Set<Long>> predicateObjectSet) {
        if (newIndexObjectSet != null) {
            Set<Long> objectSet = predicateObjectSet.getValue();
            for (Long object : objectSet) {
                newIndexObjectSet.remove(object);
            }
        }
    }
}
