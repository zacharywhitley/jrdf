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

package org.jrdf;

import junit.framework.TestCase;
import org.jrdf.graph.local.GraphImpl;
import org.jrdf.graph.local.index.longindex.mem.LongIndexMem;
import org.jrdf.query.answer.AnswerType;
import org.jrdf.query.answer.DatatypeType;
import org.jrdf.query.answer.SparqlResultType;
import org.jrdf.query.relation.constants.NullaryTuple;
import org.jrdf.sparql.parser.lexer.LexerException;
import org.jrdf.sparql.parser.parser.ParserException;
import org.jrdf.util.NodeTypeEnum;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.SerializationTestUtil;
import org.jrdf.util.test.filter.JavaClassFileFilter;
import org.jrdf.util.test.filter.MarkedAsSerializableClassFilter;
import org.jrdf.util.test.filter.RecursiveFileFinder;
import org.jrdf.vocabulary.Vocabulary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Checks that all classes that claim to be {@link java.io.Serializable} can actually be serialized and contain a
 * <code>static final long serialVersionUID</code> field.
 *
 * @author Tom Adams
 * @version $Id$
 */
public final class SerializationIntegrationTest extends TestCase {
    private static final String PATH_ROOT = "/";
    private static final Class<Vocabulary> PRODUCTION_CLASS = Vocabulary.class;
    private static final String DOT = ".";
    private static final MarkedAsSerializableClassFilter FILTER_MARKED_AS_SERIALIZABLE =
        new MarkedAsSerializableClassFilter();

    public void testSerializationClaims() {
        File packageRoot = getLocation(PATH_ROOT);
        Collection<File> allClasses = new RecursiveFileFinder().findFiles(packageRoot, new JavaClassFileFilter());
        Collection<Class<?>> serializableClasses = getSerializableClasses(packageRoot, allClasses);
        checkSerializability(serializableClasses);
    }

    // FIXME TJA: Remove excluded classes once all classes serialize
    private void checkSerializability(Collection<Class<?>> serializables) {
        for (Class<?> cls : serializables) {
            if (!excuseFromSerializationCheck(cls)) {
                SerializationTestUtil.checkSerializability(cls);
            }
        }
    }

    // Note. We can't & don't check the serializability of interfaces.
    private boolean excuseFromSerializationCheck(Class<?> cls) {
        Collection<Class<?>> excludedClasses = getExcludedClasses();
        return excludedClasses.contains(cls) || ClassPropertiesTestUtil.isClassAnInterface(cls);
    }

    // FIXME TJA: Remove excluded classes once all classes serialize properly
    private Collection<Class<?>> getExcludedClasses() {
        Collection<Class<?>> excludedClasses = new ArrayList<Class<?>>();
        excludedClasses.add(GraphImpl.class);
        excludedClasses.add(LongIndexMem.class);
        excludedClasses.add(NullaryTuple.class);
        excludedClasses.add(NodeTypeEnum.class);
        excludedClasses.add(DatatypeType.class);
        excludedClasses.add(SparqlResultType.class);
        excludedClasses.add(LexerException.class);
        excludedClasses.add(ParserException.class);
        excludedClasses.add(AnswerType.class);
//        excludedClasses.add(SelectQueryImpl.class); // not sure why this doesn't work, it references Expression.ALL
        return excludedClasses;
    }

    private Collection<Class<?>> getSerializableClasses(File packageRoot, Collection<File> classes) {
        List<Class<?>> serializables = new ArrayList<Class<?>>();
        for (File file : classes) {
            Class<?> cls = getClass(packageRoot, file);
            if (isMarkedAsSerializable(cls)) {
                serializables.add(cls);
            }
        }
        return serializables;
    }

    private Class<?> getClass(File packageRoot, File classFile) {
        String className = getClassName(packageRoot, classFile);
        return getClassForname(className);
    }

    private String getClassName(File packageRoot, File cls) {
        try {
            String pathWithPackageRemoved = removePackagePrefix(packageRoot.getCanonicalPath(), cls.getCanonicalPath());
            return replaceFileSeperatorsWithDots(pathWithPackageRemoved);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> getClassForname(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String removePackagePrefix(String packagePath, String fullPath) {
        return fullPath.substring(packagePath.length(), fullPath.lastIndexOf(DOT));
    }

    private String replaceFileSeperatorsWithDots(String path) {
        String separator = File.separator;
        separator = separator.replaceAll("\\\\", "\\\\\\\\");
        String name = path.replaceAll(separator, DOT);
        if (name.startsWith(DOT)) {
            name = name.substring(1);
        }
        return name;
    }

    private boolean isMarkedAsSerializable(Class<?> cls) {
        return FILTER_MARKED_AS_SERIALIZABLE.accept(cls);
    }

    private static File getLocation(String path) {
        return new File(PRODUCTION_CLASS.getResource(path).getFile());
    }
}
