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

package org.jrdf.graph.local.mem;

import org.jrdf.graph.Container;
import org.jrdf.graph.ObjectNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The base class for the implementation of Bag and Alternative.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractUnorderedContainer implements Container {

    /**
     * The hashmap containing the elements.
     */
    protected final Map<Long, ObjectNode> elements = new HashMap<Long, ObjectNode>();

    /**
     * Counter used to generate keys to add to the hashmap.
     */
    protected long key;


    public boolean add(ObjectNode o) {
        elements.put(key++, o);
        return true;
    }

    public boolean remove(Object o) {
        Iterator<Map.Entry<Long, ObjectNode>> iter = elements.entrySet().iterator();
        boolean found = false;

        // Removes the first entry in the map that matches the given object.
        while (!found && iter.hasNext()) {
            Map.Entry<Long, ObjectNode> entry = iter.next();
            if (o.equals(entry.getValue())) {
                elements.remove(o);
                found = true;
            }
        }

        return found;
    }

    public boolean contains(Object o) {
        return elements.values().contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return elements.values().containsAll(c);
    }

    public boolean addAll(Collection<? extends ObjectNode> c) throws IllegalArgumentException {
        Iterator<? extends ObjectNode> iter = c.iterator();
        boolean modified = false;
        while (iter.hasNext()) {
            ObjectNode obj = iter.next();
            boolean added = add(obj);
            modified = modified || added;
        }
        return modified;
    }

    public boolean removeAll(Collection<?> c) {
        Iterator<?> iter = c.iterator();
        boolean modified = false;
        while (iter.hasNext()) {
            boolean removed = remove(iter.next());
            modified = modified || removed;
        }
        return modified;
    }

    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<?> iter = iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (!c.contains(obj)) {
                modified = true;
                remove(obj);
            }
        }
        return modified;
    }

    public Iterator<ObjectNode> iterator() {
        return elements.values().iterator();
    }

    public Object[] toArray() {
        return elements.values().toArray();
    }

    @SuppressWarnings({ "unchecked" })
    public <T> T[] toArray(T[] a) {
        return (T[]) elements.values().toArray();
    }

    public int size() {
        return elements.values().size();
    }

    public boolean isEmpty() {
        return elements.values().isEmpty();
    }

    public void clear() {
        key = 0L;
        elements.clear();
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }
}