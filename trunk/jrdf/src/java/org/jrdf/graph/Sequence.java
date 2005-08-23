/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph;

// Java 2 standard packages

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * A Sequence is a group of statements that are kept in an orderd list.  The
 * order in which the objects go in dictates how they are returned in a First
 * In, First Out manner.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public interface Sequence <ObjectNode> extends Container<ObjectNode>,
    List<ObjectNode> {

    /**
     * {@inheritDoc}
     *
     * @param o Add an ObjectNode.
     */
    void add(int index, ObjectNode o);

    /**
     * {@inheritDoc}
     */
    boolean addAll(int index, Collection<? extends ObjectNode> c);

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     */
    ObjectNode get(int index);

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the given object is not the correct
     *   type, ObjectNode.
     */
    int indexOf(Object o) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the given object is not the correct
     *   type, ObjectNode.
     */
    int lastIndexOf(Object o) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    ListIterator<ObjectNode> listIterator();

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    ListIterator<ObjectNode> listIterator(int index);

    /**
     * Removes the first element in the list (in a FIFO manner).
     *
     * @return the ObjectNode that was at the start of the list.
     */
    ObjectNode remove();

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return the ObjectNode at that position.
     */
    ObjectNode remove(int index);

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the given object is not the correct
     *   type, ObjectNode.
     */
    ObjectNode set(int index, ObjectNode element) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     *
     * @param fromIndex {@inheritDoc}
     * @param toIndex {@inheritDoc}
     * @return {@inheritDoc}
     */
    List<ObjectNode> subList(int fromIndex, int toIndex);
}
