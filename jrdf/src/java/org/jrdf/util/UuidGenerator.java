/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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

//Java 2 standard packages

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.rmi.dgc.VMID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class that generates an Unique identifier.
 *
 * @author Robert Turner
 * @author Tom Adams
 */
public final class UuidGenerator {
    // FIXME: Split this into a generator and UUID class.
    // FIXME: Can we make this the default UUID generator, and create another one based on the RFC What about the Java
    // UUID Generator?
    // FIXME: What format of UUID's should these give back? The standard one? Short form and canonical form (do when a
    // sep. UUID class).
    private static final String DIGEST_ALGORITHM = "MD5";
    private static final int SINGLE_DIGIT = 0x10;
    private static final int INT_OFFSET = 0xFF;
    private static String ipAddress;
    private static String vmID;
    private static long uidCounter;
    private static long callTime;

    private UuidGenerator() {
    }

    /**
     * Generates an Unique Identifier using the current time and the machines' IP address.
     *
     * @return A UUID.
     */
    public static synchronized String generateUuid() {
        // FIXME: Strongly-type returned Uuid.
        // FIXME: Does this need to throw exception?
        return getUniqueId(getSeed());
    }

    // FIXME: Refactor
    private static synchronized String getUniqueId(char[] seed) {
        byte[] seedDigest = computeDigest(seed);
        String uid = toHexString(seedDigest);
        // FIXME: Why do we need this? Will it ever happen?
        if (null == uid) {
            throw new RuntimeException("Failed to generate valid UUID: UUID was null");
        }
        return uid;
    }

    private static String toHexString(byte[] seedDigest) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < seedDigest.length; ++i) {
            int currentInt = seedDigest[i] & INT_OFFSET;
            if (currentInt < SINGLE_DIGIT) {
                buffer.append('0');
            }
            buffer.append(Integer.toHexString(currentInt));
        }
        return buffer.toString();
    }

    private static byte[] computeDigest(char[] chars) {
        ByteBuffer buffer = ByteBuffer.allocate(chars.length * 2);
        for (int i = 0; i < chars.length; i++) {
            buffer.putChar(chars[i]);
        }
        return getDigester().digest(buffer.array());
    }

    private static MessageDigest getDigester() {
        try {
            return MessageDigest.getInstance(DIGEST_ALGORITHM);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to get " + DIGEST_ALGORITHM + " digest algorithm");
        }
    }

    private static synchronized char[] getSeed() {
        StringBuffer seed = new StringBuffer();
        initialiseSeed(seed);
        return convertToChars(seed);
    }

    private static void initialiseSeed(StringBuffer seed) {
        seed.append(getIpAddress());                // location in universe (cyberspace). A JVM running on a particular machine.
        seed.append(getJvmId());
        seed.append(getTime());                     // time (current millisecond)
        seed.append(getRandom());                   // random, allows multiple new UUIDs per millisecond. (validated by List)
        seed.append(UuidGenerator.class.getName()); // another Class could be generating UUIDs the same way within the same JVM
        seed.append(getCount());                    // two random numbers could (possibly) be generated in the same millisecond
        seed.append(getClassLoaderId());            // more than one UuidGenerator class may be loaded by different ClassLoaders
    }

    private static char[] convertToChars(StringBuffer seed) {
        char[] chars = new char[seed.length()];
        seed.getChars(0, seed.length(), chars, 0);
        return chars;
    }

    private static synchronized String getRandom() {
        long random = new SecureRandom().nextLong();
        return "" + random;
    }

    private static synchronized String getTime() {
        callTime = System.currentTimeMillis();
        return "" + callTime;
    }

    private static synchronized long getCount() {
        return ++uidCounter;
    }

    private static synchronized String getIpAddress() {
        try {
            if (null == ipAddress) {
                ipAddress = InetAddress.getLocalHost().getHostAddress();
            }
            return ipAddress;
        }
        catch (UnknownHostException uhe) {
            throw new RuntimeException("Unable to determine IP Address of machine", uhe);
        }
    }

    private static synchronized String getJvmId() {
        if (null == vmID) {
            vmID = new VMID().toString();
        }
        return vmID;
    }

    /**
     * If multiple UuidGenerators are loaded by different class loaders, they
     * will be operating independantly of each other (ie. multiple web
     * applications within an application server), It is (remotely) possible
     * that multiple UuidGenerator may duplicate their count and random numbers
     * within the same millisecond.
     */
    private static synchronized int getClassLoaderId() {
        // FIXME: Why is this not cached liek the others? Why are the others cached?
        return System.identityHashCode(UuidGenerator.class.getClassLoader());
    }
}
