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

package org.jrdf.persistence.lazy;

import org.jrdf.persistence.EntityManager;
import org.jrdf.persistence.PersistenceException;
import org.jrdf.persistence.RDFIntrospector;

import java.net.URI;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Peter Bednar
 * @author Jozef Wagner, http://wagjo.com/
 */
public class LazyList<E> extends AbstractList<E> implements LazyCollection {

    protected List elements;
    protected Class<E> resultClass;
    protected EntityManager manager;

    public LazyList(Collection<URI> elements, Class<E> resultClass,
        EntityManager manager) {
        this.resultClass = resultClass;
        this.manager = manager;
        this.elements = new ArrayList(elements);
    }

    public int size() {
        return elements.size();
    }

    public void add(int index, Object element) {
        elements.add(index, element);
    }

    public E remove(int index) {
        E prev = get(index);
        elements.remove(index);
        return prev;
    }

    public E get(int index) {
        Object elm = elements.get(index);
        if (elm == null) {
            return null;
        }
        if (resultClass != elm.getClass()) {
            try {
                elm = manager.find(resultClass, (URI) elm);
            } catch (PersistenceException e) {
                throw new IllegalStateException(e);
            }
            if (elm == null) {
                throw new NoSuchElementException();
            }
            elements.set(index, elm);
        }
        return (E) elm;
    }

    public Object set(int index, Object element) {
        Object prev = get(index);
        elements.set(index, element);
        return prev;
    }

    public Collection elements() {
        return elements;
    }

    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    public int indexOf(Object o) {
        if (o.getClass() != resultClass) {
            return -1;
        }
        int index = elements.indexOf(o);
        if (index == -1) {
            URI uri = RDFIntrospector.getURI(o);
            if (uri != null) {
                return elements.indexOf(uri);
            }
        }
        return index;
    }

}
