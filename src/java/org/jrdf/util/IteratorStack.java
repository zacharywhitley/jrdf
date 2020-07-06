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
package org.jrdf.util;

import java.util.Iterator;
import java.util.Stack;

/**
 * Wraps an Iterator to provide a stack interface to it's contents.
 *
 * @author TurnerRX
 */
public class IteratorStack<T> implements Iterator<T> {
    private Iterator<T> iter;
    private Stack<T> stack = new Stack<T>();

    /**
     * Creates a new stack that wraps the Iterator.
     *
     * @param newIter Iterator
     */
    public IteratorStack(Iterator<T> newIter) {
        if (newIter == null) {
            throw new IllegalArgumentException("Iterator is null.");
        }
        this.iter = newIter;
        pushNextItemOntoStack();
    }

    /*--------------------------------------------------------------------------
     * "Stack" methods.
     */

    /**
     * Returns the next item on the stack without removing it. This will either
     * be the last item that was "pushed" onto the stack, or next item in the
     * Iterator if the stack does not contain any "pushed" items.
     *
     * @return T item
     */
    public T peek() {
        if (stack.isEmpty()) {
            pushNextItemOntoStack();
        }
        return stack.peek();
    }

    /**
     * Removes the next item from the stack and returns it. This will either be
     * the last item that was "pushed" onto the stack, or next item in the
     * Iterator if the stack does not contain any "pushed" items.
     *
     * @return T item
     */
    public T pop() {
        if (stack.isEmpty()) {
            pushNextItemOntoStack();
        }
        return stack.pop();
    }

    /**
     * Pushes the item onto the stack.
     *
     * @param item to be place on top of the stack
     */
    public void push(T item) {
        stack.push(item);
    }

    private void pushNextItemOntoStack() {
        if (iter.hasNext()) {
            push(iter.next());
        }
    }

    /*--------------------------------------------------------------------------
     * Iterator methods.
     */

    public boolean hasNext() {
        return iter.hasNext() || !stack.isEmpty();
    }

    // TODO AN Does this break the iterator contract - and not throw NoSuchElementException - rather it will throw an
    // EmptyStackException.
    public T next() {
        return pop();
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("remove() not implemented on: " + getClass().getName());
    }

}