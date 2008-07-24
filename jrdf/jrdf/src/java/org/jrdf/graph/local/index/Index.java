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

package org.jrdf.graph.local.index;

import org.jrdf.graph.GraphException;
import org.jrdf.util.ClosableIterator;

public interface Index<T> {

    /**
     * Adds a triple to a single index.  This method defines the internal structure.
     *
     * @param node The nodes to add.
     * @throws GraphException If there was an error adding the statement.
     */
    void add(T... node) throws GraphException;

    /**
     * Removes a triple from a single index.
     *
     * @param node the nodes to remove.
     * @throws GraphException If there was an error revoking the statement, for example if it didn't exist.
     */
    void remove(T... node) throws GraphException;

    /**
     * Removes all triples from this index.
     */
    void clear();

    /**
     * Returns an iterator which contains all the elements in the graph as an array of distinct longs.
     *
     * @return an iterator which contains all the elements in the graph as an array of distinct longs.
     */
    ClosableIterator<T[]> iterator();

    /**
     * Returns an array of results from the index.  For example, a given subject id is given and the array of predicates
     * and objects.
     *
     * @param first the entry to find.
     * @return an array containing the results.
     */
    ClosableIterator<T[]> getSubIndex(T first);

    /**
     * Returns an array of results from the index.  For example, a given subject and predicate ids  the array of
     * objects is returned.
     *
     * @param first the entry to find.
     * @param second the entry to find.
     * @return an array containing the results.
     */
    ClosableIterator<T> getSubSubIndex(T first, T second);

    /**
     * Returns true if the value given exists in the index.
     *
     * @param first the key to search for.
     * @return true if the key exsts.
     */
    boolean contains(T first);

    /**
     * Removes the given entry of long to set of longs with the given entry.  For example, a given subject id is
     * given and it will remove all the associated predicate and objects for that subject.
     *
     * @param first the entry set to remove.
     * @return true if the entry set was non-null.
     */
    boolean removeSubIndex(T first);

    /**
     * Returns the number of triples in the index.
     *
     * @return the number of triples in the index.
     */
    long getSize();

    void close();
}