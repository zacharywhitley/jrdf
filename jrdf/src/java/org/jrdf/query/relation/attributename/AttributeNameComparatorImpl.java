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

package org.jrdf.query.relation.attributename;

/**
 * Compares attribute names.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public final class AttributeNameComparatorImpl implements AttributeNameComparator {
    private static final long serialVersionUID = -1713813222274855404L;
    private static final int AFTER = 1;
    private static final int BEFORE = -1;
    private static final int EQUAL = 0;

    public int compare(AttributeName attName1, AttributeName attName2) {
        ifNullThrowException(attName1, attName2);

        int result = compareAttributeNames(attName1, attName2);
        if (result == EQUAL) {
            result = compareByLiteralValue(attName1, attName2);
        }
        return result;
    }

    private void ifNullThrowException(AttributeName attName1, AttributeName attName2) {
        if (attName1 == null || attName2 == null) {
            throw new NullPointerException();
        }
    }

    private int compareAttributeNames(AttributeName attribute, AttributeName attribute1) {
        boolean attIsVariable = attributeIsVariableName(attribute);
        boolean att2IsVariable = attributeIsVariableName(attribute1);
        if (isSameNameType(attIsVariable, att2IsVariable)) {
            return EQUAL;
        } else if (attIsVariable) {
            return AFTER;
        } else {
            return BEFORE;
        }
    }

    private int compareByLiteralValue(AttributeName attributeName, AttributeName attributeName1) {
        String attLit1 = attributeName.getLiteral();
        String attLit2 = attributeName1.getLiteral();
        int result = attLit1.compareTo(attLit2);
        if (result > EQUAL) {
            return AFTER;
        } else if (result < EQUAL) {
            return BEFORE;
        }
        return result;
    }

    private boolean isSameNameType(boolean attIsVariable, boolean att2IsVariable) {
        boolean bothVariables = attIsVariable && att2IsVariable;
        boolean bothNoVariables = !attIsVariable && !att2IsVariable;
        return bothVariables || bothNoVariables;
    }

    private boolean attributeIsVariableName(AttributeName attribute) {
        return attribute instanceof VariableName;
    }
}
