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

package org.jrdf.writer.mem;

import org.hamcrest.Matchers;
import org.jrdf.graph.Graph;
import org.jrdf.util.test.ParameterDefinition;
import org.jrdf.vocabulary.RDF;
import org.jrdf.writer.RdfNamespaceMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAssertions;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;

public class MemRdfNamespaceMapUnitTest {
    private MemRdfNamespaceMap subject;

    @Before
    public void createSubject() {
        subject = new MemRdfNamespaceMap();
    }

    @Test
    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(RdfNamespaceMap.class, MemRdfNamespaceMap.class);
    }

    @Test
    public void testBadParams() throws Exception {
        checkMethodNullAssertions(subject, "load", new ParameterDefinition(new String[]{"graph"},
            new Class<?>[]{Graph.class}));
        checkMethodNullAssertions(subject, "addNamespace", new ParameterDefinition(
            new String[]{"namespace", "partialUri"}, new Class<?>[]{String.class, String.class}));
        checkMethodNullAssertions(subject, "getQName", new ParameterDefinition(
            new String[]{"uri"}, new Class<?>[]{String.class}));
    }

    @Test
    public void testDefaultNamespaceIsRdfMapping() throws Exception {
        Set<Map.Entry<String, String>> nameEntries = subject.getNameEntries();
        assertThat(nameEntries, Matchers.hasSize(1));
        Map.Entry<String, String> nameUriEntry = nameEntries.iterator().next();
        assertThat(nameUriEntry.getKey(), equalTo("rdf"));
        assertThat(nameUriEntry.getValue(), equalTo(RDF.BASE_URI.toString()));
    }
}
