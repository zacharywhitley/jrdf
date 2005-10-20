/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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

package org.jrdf.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * Integration test for {@link UuidGenerator}.
 * @author Tom Adams
 * @version $Id$
 */
public final class UuidGeneratorIntegrationTest extends TestCase {

    private static final String UUID_GENERATOR_CLASS_NAME_FULL = UuidGenerator.class.getName();
    private static final String UUID_GENERATOR_CLASS_NAME_SIMPLE = UuidGenerator.class.getSimpleName();
    private static final String GENERATE_UUID_METHOD_NAME = "generateUuid";
    private static final Class[] NO_PARAMS = null;
    private static final Object[] NO_ARGS = (Object[]) null;
    private static final Object UUID_GENERATOR_INSTANCE = null;
    private static final int NUM_THREADS = 10;
    private static final int NUM_CLASSLOADERS = 10;
    private static final int NUM_UIDS = 5000;
    private static final int NUM_UUIDS_TO_GENERATE = NUM_UIDS / NUM_THREADS;

    public void testMultipleClassLoadersGenerateUniqueUuids() throws Exception {
        URL[] uidClassUrls = new URL[]{getResource(UUID_GENERATOR_CLASS_NAME_SIMPLE)};
        for (int i = 0; i < NUM_CLASSLOADERS; i++) {
            Class<?> uuidGeneratorClass = loadClass(uidClassUrls, UUID_GENERATOR_CLASS_NAME_FULL);
            checkUuidGenerator(uuidGeneratorClass);
        }
    }

    private Class<?> loadClass(URL[] classUrls, String className) throws ClassNotFoundException {
        URLClassLoader classLoader = getClassLoader(classUrls);
        return classLoader.loadClass(className);
    }

    private URLClassLoader getClassLoader(URL[] classUrls) {
        return new URLClassLoader(classUrls);
    }

    private URL getResource(String resourceName) {
        return ClassLoader.getSystemClassLoader().getResource(resourceName);
    }

    private void checkUuidGenerator(Class uuidGenerator) throws Exception {
        Method generateUuidMethod = uuidGenerator.getMethod(GENERATE_UUID_METHOD_NAME, NO_PARAMS);
        generateUuids(generateUuidMethod);
    }

    private void generateUuids(Method generateUID) throws Exception {
        Set<String> uuids = new HashSet<String>(NUM_UIDS);
        for (int i = 0; i < NUM_UIDS; i++) {
            String uuid = invokeGenerateUuidMethod(generateUID);
            UuidGeneratorUnitTest.checkUuidIsUnique(uuids, uuid);
            uuids.add(uuid);
        }
    }

    private String invokeGenerateUuidMethod(Method generateUuidMethod) throws Exception {
        return (String) generateUuidMethod.invoke(UUID_GENERATOR_INSTANCE, NO_ARGS);
    }

    public static void testMultipleThreadsGenerateUniqueUuids() throws Exception {
        Set<String> uuids = new HashSet<String>(NUM_UIDS);
        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < NUM_THREADS; i++) {
            Thread thread = startThread(uuids);
            threadList.add(thread);
        }
        waitForThreadCompletion(threadList);
    }

    private static Thread startThread(Set<String> uuids) {
        Thread currentThread = new Thread(new UuidPopulator(uuids, NUM_UUIDS_TO_GENERATE));
        currentThread.start();
        return currentThread;
    }

    private static void waitForThreadCompletion(List<Thread> threadList)
            throws InterruptedException {
        for (int i = 0; NUM_THREADS > i; i++) {
            threadList.get(i).join();
        }
    }
}
