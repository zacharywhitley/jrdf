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
package org.jrdf.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.util.EscapeUtil.escape;

/**
 * Unit test for {@link EscapeUtil}.
 *
 * @author Andrew Newman
 */
public class EscapeUtilUnitTest {

    @Test
    public void surrogateEscaping() {
        // ISO 10646 Amendment 1
        // High surrogates from D800...DBFF, low surrogates from DC00...DFFF.
        assertThat(escape("\uD800\uDC00").toString(), equalTo("\\U00010000"));
        assertThat(escape("\uD800\uDC01").toString(), equalTo("\\U00010001"));
        assertThat(escape("\uD801\uDC01").toString(), equalTo("\\U00010401"));
        assertThat(escape("\uD840\uDC00").toString(), equalTo("\\U00020000"));
        assertThat(escape("\uD880\uDC00").toString(), equalTo("\\U00030000"));
        assertThat(escape("\uDBC0\uDC00").toString(), equalTo("\\U00100000"));
        assertThat(escape("\uDBFF\uDFFF").toString(), equalTo("\\U0010FFFF"));
    }

    @Test
    public void surrogateBoundaryEscaping() {
        // ISO 10646 Amendment 1
        // High surrogates from D800...DBFF, low surrogates from DC00...DFFF.
        assertThat(escape("\uD799\uDC00").toString(), equalTo("\\uD799\\uDC00"));
        assertThat(escape("\uD799\uDC01").toString(), equalTo("\\uD799\\uDC01"));
        assertThat(escape("\uD800\uDBFF").toString(), equalTo("\\uD800\\uDBFF"));
        assertThat(escape("\uD801\uDBFF").toString(), equalTo("\\uD801\\uDBFF"));
        assertThat(escape("\uDC00\uDFFF").toString(), equalTo("\\uDC00\\uDFFF"));
        assertThat(escape("\uDBFF\uE000").toString(), equalTo("\\uDBFF\\uE000"));
    }

    @Test
    public void exampleCodePoints() {
        assertThat(escape("\uD84C\uDFB4").toString(), equalTo("\\U000233B4"));
        assertThat(escape("\u2260").toString(), equalTo("\\u2260"));
        assertThat(escape("\u0071").toString(), equalTo("q"));
        assertThat(escape("\u030c").toString(), equalTo("\\u030C"));
    }

    @Test
    public void controlCharacters() {
        assertThat(escape("\\").toString(), equalTo("\\\\"));
        assertThat(escape("\"").toString(), equalTo("\\\""));
        assertThat(escape("\r").toString(), equalTo("\\r"));
        assertThat(escape("\n").toString(), equalTo("\\n"));
        assertThat(escape("\t").toString(), equalTo("\\t"));
    }
}