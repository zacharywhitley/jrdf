/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph.datatype;

import org.jrdf.graph.Literal;
import static org.jrdf.vocabulary.XSD.BOOLEAN;
import static org.jrdf.vocabulary.XSD.DECIMALS;
import static org.jrdf.vocabulary.XSD.STRINGS;
import static org.jrdf.util.EqualsUtil.hasSuperClassOrInterface;

public class DatatypeUtilImpl implements DatatypeUtil {

    // TODO AN Replace with code by type rather than silly if/then/else.
    public int compareTo(Literal literal1, Literal literal2) {
        if (areXSDDecimals(literal1, literal2)) {
            return equivalentXSDDecimal(literal1, literal2);
        } else if (areStringLiterals(literal1, literal2)) {
            return equivalentString(literal1, literal2);
        } else if (areBooleanLiterals(literal1, literal2)) {
            return equivalentBoolean(literal1, literal2);
        } else {
            return literal1.getDatatypeURI().compareTo(literal2.getDatatypeURI());
        }
    }

    public int equivalentXSDDecimal(Literal literal1, Literal literal2) {
        if (hasSuperClassOrInterface(XSDDecimal.class, literal1.getValue()) &&
            hasSuperClassOrInterface(XSDDecimal.class, literal1.getValue())) {
            XSDDecimal value1 = (XSDDecimal) literal1.getValue();
            XSDDecimal value2 = (XSDDecimal) literal2.getValue();
            return value1.getAsBigDecimal().compareTo(value2.getAsBigDecimal());
        } else {
            StringValue value1 = (StringValue) literal1.getValue();
            StringValue value2 = (StringValue) literal2.getValue();
            return value1.compareTo(value2);
        }
    }

    public int equivalentString(Literal literal1, Literal literal2) {
        return literal1.getLexicalForm().compareTo(literal2.getLexicalForm());
    }

    public int equivalentBoolean(Literal literal1, Literal literal2) {
        BooleanValue value1 = (BooleanValue) literal1.getValue();
        BooleanValue value2 = (BooleanValue) literal2.getValue();
        return value1.compareTo(value2);
    }

    private boolean areXSDDecimals(Literal literal1, Literal literal2) {
        return DECIMALS.contains(literal1.getDatatypeURI()) && DECIMALS.contains(literal2.getDatatypeURI());
    }

    private boolean areStringLiterals(Literal literal1, Literal literal2) {
        return STRINGS.contains(literal1.getDatatypeURI()) && STRINGS.contains(literal2.getDatatypeURI());
    }

    private boolean areBooleanLiterals(Literal literal1, Literal literal2) {
        return literal1.getDatatypeURI().equals(BOOLEAN) && literal2.getDatatypeURI().equals(BOOLEAN);
    }
}
