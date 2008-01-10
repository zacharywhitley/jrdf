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

package org.jrdf.parser.rdfxml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A collection of XML attributes.
 */
class Atts {

    /**
     * List containing Att objects.
     */
    private List<Att> attributes;
    private static final int ATTRIBUTE_SIZE = 4;
    private static final int INITIAL_BUILDER_SIZE = 512;

    /**
     * Creates a new <tt>Atts</tt> object.
     */
    Atts() {
        this(ATTRIBUTE_SIZE);
    }

    /**
     * Creates a new <tt>Atts</tt> object.
     *
     * @param size The initial size of the array for storing attributes.
     */
    Atts(int size) {
        attributes = new ArrayList<Att>(size);
    }

    /**
     * Adds an attribute.
     */
    public void addAtt(Att att) {
        attributes.add(att);
    }

    /**
     * Get an iterator on the attributes.
     *
     * @return an Iterator over Att objects.
     */
    public Iterator<Att> iterator() {
        return attributes.iterator();
    }

    /**
     * Gets the attribute with the specified QName.
     *
     * @param qName The QName of an attribute.
     * @return The attribute with the specified QName, or
     *         <tt>null</tt> if no such attribute could be found.
     */
    public Att getAtt(String qName) {
        for (Att att : attributes) {
            if (att.getQName().equals(qName)) {
                return att;
            }
        }

        return null;
    }

    /**
     * Gets the attribute with the specified namespace and local name.
     *
     * @param namespace The namespace of an attribute.
     * @param localName The local name of an attribute.
     * @return The attribute with the specified namespace and local
     *         name, or <tt>null</tt> if no such attribute could be found.
     */
    public Att getAtt(String namespace, String localName) {
        for (Att att : attributes) {
            if (att.getLocalName().equals(localName) &&
                att.getNamespace().equals(namespace)) {
                return att;
            }
        }

        return null;
    }

    /**
     * Removes the attribute with the specified QName and returns it.
     *
     * @param qName The QName of an attribute.
     * @return The removed attribute, or <tt>null</tt> if no attribute
     *         with the specified QName could be found.
     */
    public Att removeAtt(String qName) {
        for (int i = 0; i < attributes.size(); i++) {
            Att att = attributes.get(i);

            if (att.getQName().equals(qName)) {
                attributes.remove(i);
                return att;
            }
        }

        return null;
    }

    /**
     * Removes the attribute with the specified namespace and local
     * name and returns it.
     *
     * @param namespace The namespace of an attribute.
     * @param localName The local name of an attribute.
     * @return The removed attribute, or <tt>null</tt> if no attribute
     *         with the specified namespace and local name could be found.
     */
    public Att removeAtt(String namespace, String localName) {
        for (int i = 0; i < attributes.size(); i++) {
            Att att = attributes.get(i);

            if (att.getLocalName().equals(localName) &&
                att.getNamespace().equals(namespace)) {
                attributes.remove(i);
                return att;
            }
        }

        return null;
    }

    /**
     * Returns the number of attributes contained in this object.
     * @return size of attributes list
     */
    public int size() {
        return attributes.size();
    }

    /**
     * Produces a String-representation of this object.
     * @return string representation of Atts
     */
    public String toString() {
        StringBuilder result = new StringBuilder(INITIAL_BUILDER_SIZE);
        result.append("Atts[");
        for (Att att : attributes) {
            result.append(att.getQName());
            result.append("=");
            result.append(att.getValue());
            result.append("; ");
        }
        result.append("]");
        return result.toString();
    }
}
