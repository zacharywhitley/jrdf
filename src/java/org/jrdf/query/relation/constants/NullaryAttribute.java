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

package org.jrdf.query.relation.constants;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.NodeTypeVisitor;
import org.jrdf.query.relation.attributename.AttributeName;

import java.io.Serializable;
import java.util.Set;
import java.util.Collections;

/**
 * Class description goes here.
*/
public final class NullaryAttribute implements Attribute, Serializable {
    /**
     * Nullary Attribute.
     */
    public static final Attribute NULLARY_ATTRIBUTE = new NullaryAttribute();
    private static final long serialVersionUID = 1808216129525892253L;

    private NullaryAttribute() {
    }

    public AttributeName getAttributeName() {
        return new NullaryAttributeName();
    }

    public NodeType getType() {
        return new NullaryNodeType();
    }

    /**
     * Test whether a given attribute is nullary attribute.
     * @param attribute
     * @return true if the given attribute is this attribute or attribute and type are nullary.
     */
    public static boolean isNullaryAttribute(Attribute attribute) {
        return NULLARY_ATTRIBUTE == attribute ||
               attribute.getAttributeName() instanceof NullaryAttributeName ||
               attribute.getType() instanceof NullaryNodeType;
    }

    private static final class NullaryAttributeName implements AttributeName, Serializable {
        private static final long serialVersionUID = 1808216129525892252L;

        private NullaryAttributeName() {
        }

        public String getLiteral() {
            return "NULL";
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

    private static final class NullaryNodeType implements NodeType, Serializable {
        private static final long serialVersionUID = 1808216129525892251L;

        private NullaryNodeType() {
        }

        public String getName() {
            return "Null";
        }

        public Set<? extends NodeType> composedOf() {
            return Collections.emptySet();
        }

        public void accept(NodeTypeVisitor visitor) {
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

}
