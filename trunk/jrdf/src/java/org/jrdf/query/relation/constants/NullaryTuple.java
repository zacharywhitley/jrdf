/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query.relation.constants;

import org.jrdf.JRDFFactory;
import org.jrdf.JRDFFactoryImpl;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.mem.AttributeValuePairComparatorImpl;
import org.jrdf.query.relation.mem.AttributeComparatorImpl;
import static org.jrdf.query.relation.constants.NullaryAttributeValuePair.NULLARY_ATTRIBUTE_VALUE_PAIR;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A class which simply contains the True Node constant.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class NullaryTuple implements Tuple, Serializable {

    private static final JRDFFactory FACTORY = JRDFFactoryImpl.getFactory();
    private static final long serialVersionUID = 1808216129525892255L;

    /**
     * The node which represents the boolean logic value "NULLARY_TUPLE".
     */
    public static final Tuple NULLARY_TUPLE = new NullaryTuple();
    private static final AttributeValuePair NULLARY_AVP = NULLARY_ATTRIBUTE_VALUE_PAIR;
    private static final Set<AttributeValuePair> NULLARY_AVP_SET = Collections.singleton(NULLARY_AVP);

    private NullaryTuple() {
    }

    private Object readResolve() throws ObjectStreamException {
        return NULLARY_TUPLE;
    }

    public Set<AttributeValuePair> getAttributeValues() {
        return NULLARY_AVP_SET;
    }

    public SortedSet<AttributeValuePair> getSortedAttributeValues() {
        AttributeValuePairComparator avpComparator = FACTORY.getNewAttributeValuePairComparator();
        SortedSet<AttributeValuePair> sortedPairs = new TreeSet<AttributeValuePair>(avpComparator);
        sortedPairs.addAll(NULLARY_AVP_SET);
        return sortedPairs;
    }
}