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

package org.jrdf.graph.mem;

import org.jrdf.graph.*;
import org.jrdf.util.ClosableIterator;

import java.util.*;

/**
 * An iterator that iterates over a group with a two fixed nodes.
 * Relies on an internal iterator which iterates over all entries in
 * a set, found in a subIndex.
 * The itemIterator is used to indicate the current position.
 * It will always be set to return the next value until it reaches
 * the end of the group.
 *
 * @author Paul Gearon
 *
 * @version $Revision$
 */
public class TwoFixedIterator implements ClosableIterator {

  /** The iterator for the third index. */
  private Iterator itemIterator;

  /** The nodeFactory used to create the nodes to be returned in the triples. */
  private GraphElementFactoryImpl nodeFactory;

  /** The first fixed item. */
  private Long first;

  /** The second fixed item. */
  private Long second;

  /** The offset for the index. */
  private int var;


  /**
   * Constructor.  Sets up the internal iterators.
   */
  TwoFixedIterator(
      Map index, int var, Long newFirst, Long newSecond, GraphElementFactoryImpl nodeFactory
  ) {
    // store the node factory and other starting data
    this.nodeFactory = nodeFactory;
    this.first = newFirst;
    this.second = newSecond;
    this.var = var;
    // initialize the itemiterator to null
    itemIterator = null;
    // find the subIndex from the main index
    Map subIndex = (Map)index.get(first);
    // check that data exists
    if (subIndex != null) {
      // now find the set from the sub index map
      Set subGroup = (Set)subIndex.get(second);
      if (subGroup != null) {
        // get an interator for the set
        itemIterator = subGroup.iterator();
      }
    }
  }


  /**
   * Returns true if the iteration has more elements.
   *
   * @return <code>true</code> If there is an element to be read.
   */
  public boolean hasNext() {
    // confirm we still have an item iterator, and that it has data available
    return itemIterator != null && itemIterator.hasNext();
  }


  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration.
   * @throws NoSuchElementException iteration has no more elements.
   */
  public Object next() throws NoSuchElementException {
    if (itemIterator == null) throw new NoSuchElementException();
    // get the next item
    Long third = (Long)itemIterator.next();
    // build the triple
    return new TripleImpl(nodeFactory, var, first, second, third);
  }


  /**
   * Implemented for java.util.Iterator.  Not supported by this implementation.
   */
  public void remove() {
    throw new UnsupportedOperationException();
  }


  /**
   * Closes the iterator by freeing any resources that it current holds.
   * Nothing to be done for this class.
   * @return <code>true</code> indicating success.
   */
  public boolean close() {
    return true;
  }

}
