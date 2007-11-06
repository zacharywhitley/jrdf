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

package org.jrdf.map;

import net.metanotion.io.RAIFile;
import net.metanotion.io.Serializer;
import net.metanotion.io.block.BlockFile;
import net.metanotion.io.block.index.BSkipList;
import net.metanotion.io.data.IntBytes;
import net.metanotion.io.data.LongBytes;
import net.metanotion.io.data.StringBytes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListMapFactory implements MapFactory {
    private static final String USERNAME = System.getProperty("user.name");
    private static final File SYSTEM_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private Map<Class<?>, Serializer> classToSerializer = new HashMap<Class<?>, Serializer>();
    private final String name;
    private BlockFile keyBlockFile;
    private BlockFile valueBlockFile;

    public ListMapFactory(String name) {
        classToSerializer.put(Integer.class, new IntBytes());
        classToSerializer.put(Long.class, new LongBytes());
        classToSerializer.put(String.class, new StringBytes());
        this.name = name;
    }

    public <T, A, U extends A> Map<T, U> createMap(Class<T> clazz1, Class<A> clazz2) {
        try {
            keyBlockFile = getBlockFile("key");
            valueBlockFile = getBlockFile("value");
            Serializer indexSerializer = classToSerializer.get(Integer.class);
            Serializer keySerializer = classToSerializer.get(clazz1);
            Serializer valueSerializer = classToSerializer.get(clazz2);
            BSkipList.init(keyBlockFile, 2, 1000);
            BSkipList.init(valueBlockFile, 2, 1000);
            BSkipList keysList = new BSkipList(1000, keyBlockFile, 2, keySerializer, indexSerializer);
            BSkipList valuesList = new BSkipList(1000, valueBlockFile, 2, indexSerializer, valueSerializer);
            return new ListMap(keysList, valuesList);
//            return new ListMap(new BaseSkipList(1000), new BaseSkipList(1000));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T, A, U extends A> BlockFile getBlockFile(String type) throws IOException {
        File file = new File(getDir() + "/" + name + type);
        RAIFile raif = new RAIFile(file, true, true);
        return new BlockFile(raif, true);
    }

    public void close() {
        try {
            keyBlockFile.close();
            valueBlockFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private File getDir() {
        return new File(SYSTEM_TEMP_DIR, "jrdf_" + USERNAME);
    }
}
