/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.graph.trie;

import org.ardverk.collection.KeyAnalyzer;
import org.ardverk.collection.StringKeyAnalyzer;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.local.index.nodepool.LocalStringNodeMapperFactory;
import org.jrdf.graph.util.StringNodeMapper;

public final class PredicateNodeKeyAnalyzer implements KeyAnalyzer<PredicateNode> {
    private static final long serialVersionUID = 4605044508960821424L;
    private KeyAnalyzer<String> keyAnalyzer = new StringKeyAnalyzer();
    private StringNodeMapper mapper = new LocalStringNodeMapperFactory().createMapper();

    /**
     * Static instance - there is only one.
     */
    public static final KeyAnalyzer<? super PredicateNode> INSTANCE = new PredicateNodeKeyAnalyzer();

    private PredicateNodeKeyAnalyzer() {
    }

    public int bitsPerElement() {
        return keyAnalyzer.bitsPerElement();
    }

    public int lengthInBits(PredicateNode predicateNode) {
        return keyAnalyzer.lengthInBits(toString(predicateNode));
    }

    public boolean isBitSet(PredicateNode predicateNode, int bitIndex, int lengthInBits) {
        return keyAnalyzer.isBitSet(toString(predicateNode), bitIndex, lengthInBits);
    }

    public int bitIndex(PredicateNode key, int offsetInBits, int lengthInBits, PredicateNode other,
        int otherOffsetInBits, int otherLengthInBits) {
        return keyAnalyzer.bitIndex(toString(key), offsetInBits, lengthInBits, toString(other), otherOffsetInBits,
            otherLengthInBits);
    }

    public boolean isPrefix(PredicateNode prefix, int offsetInBits, int lengthInBits, PredicateNode key) {
        return keyAnalyzer.isPrefix(toString(prefix), offsetInBits, lengthInBits, toString(key));
    }

    public int compare(PredicateNode o1, PredicateNode o2) {
        return keyAnalyzer.compare(toString(o1), toString(o2)) * -1;
    }

    private String toString(PredicateNode node) {
        return node == null ? null : mapper.convertToString(node);
    }
}
