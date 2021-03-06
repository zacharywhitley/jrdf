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

package org.jrdf.graph.local;

// JRDF objects

import org.jrdf.graph.AbstractBlankNode;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.global.GlobalizedBlankNode;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RDF blank node. Note that blank nodes are deliberately devoid of external indentifying attributes.
 * <p/>
 * Blank nodes, can either be subjects or objects.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @version $Revision$
 */
public class BlankNodeImpl extends AbstractBlankNode implements GlobalizedBlankNode {
    private static final int PRIME = 31;
    private static final Pattern PATTERN = Pattern.compile("(.+)\\#(.+)");

    /**
     * Allow newer compiled version of the stub to operate when changes
     * have not occurred with the class.
     * NOTE : update this serialVersionUID when a method or a public member is
     * deleted.
     */
    private static final long serialVersionUID = 1573129076314000518L;

    /**
     * The internal identifier for this node.
     */
    private Long id;
    /**
     * Globally Unique Identifier.
     */
    private String uid;

    /**
     * The constructor for this node.  Package scope so that only NodeFactory and
     * static methods can call it.
     *
     * @param newUid String Globally Unique Identifier for external communication.
     * @param newId  The id to be used for this node.
     */
    public BlankNodeImpl(String newUid, Long newId) {
        id = newId;
        uid = newUid;
    }

    /**
     * Retrieves an internal identifier for this node.
     *
     * @return A numeric identifier for thisa node.
     */
    public Long getId() {
        return id;
    }

    /**
     * Retrieves a Globally Unique Identifier for this node.
     *
     * @return A global String identifier for this node.
     */
    public String getUID() {
        return uid;
    }

    /**
     * Returns a hash-code value for this BlankNode.  While the implementation
     * is not defined, if there is a blank node identifier then it should be
     * the hash code generated from this.  Hash code generation should follow
     * the normal contract.
     *
     * @return a hash-code value for this blank node.
     */
    @Override
    public int hashCode() {
        int hash = PRIME + id.hashCode();
        return hash * PRIME + uid.hashCode();
    }

    /**
     * While the internal structure of a BlankNode is not defined equality between
     * two nodes should be able to be determined.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        try {
            GlobalizedBlankNode tmpNode = (GlobalizedBlankNode) obj;
            return id.equals(tmpNode.getId()) && uid.equals(tmpNode.getUID());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    /**
     * Returns the String value of this BlankNode as:
     * uid#id (eg. 29fbf7ba364f1425dda058737d764603#69)
     *
     * @return String
     */
    public String toString() {
        return uid + "#" + id;
    }

    /**
     * Parses a String in the format of:
     * uid#id (eg. 29fbf7ba364f1425dda058737d764603#69) and creates a new BlankNodeImpl from it.
     * <p/>
     * Should only be applied to a value previously returned by toString()
     *
     * @param nodeString String previously returned by toString()
     * @return BlankNodeImpl
     * @throws IllegalArgumentException
     */
    public static BlankNode valueOf(String nodeString) throws IllegalArgumentException {
        Matcher matcher = PATTERN.matcher(nodeString);
        if (matcher.matches() && matcher.groupCount() == 2) {
            return new BlankNodeImpl(matcher.group(1), Long.valueOf(matcher.group(2)));
        }
        throw new IllegalArgumentException("String: " + nodeString + " is not of the format: uid#id");
    }
}
