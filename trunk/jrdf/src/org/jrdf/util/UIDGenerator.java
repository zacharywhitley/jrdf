/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The JRDF Project.  All rights reserved.
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
import java.net.*;
import java.rmi.dgc.*;
import java.util.*;
import java.security.*;
import java.nio.*;

/**
 * Utility class that generates a Globally Unique Identifier (UID).</p>
 *
 * Generates an Unique Identifier using the current time, space, class and a
 * random number. </p>
 *
 * All UIDS generated within the same millisecond are stored in a List and
 * validated for uniqueness. The List only exists for the duration of the
 * current millisecond.
 *
 * @created 2004-09-29
 *
 * @author <a href="mailto:robert.turner@tucanatech.com">Robert Turner</a>
 *
 * @version $Revision$
 *
 * @modified $Date$ by $Author$
 *
 * @company: <a href="http://www.tucanatech.com/">Tucana Technologies</a>
 *
 * @copyright &copy;2001 <a href="http://www.pisoftware.com/">Plugged In
 *   Software Pty Ltd</a>
 *
 * @licence <a href="{@docRoot}/../../LICENCE">Mozilla Public License v1.1</a>
 */
public class UIDGenerator {

  /** Computers IP address */
  private static String ipAddress = null;

  /** Unique Java Virtual Machine Identifier */
  private static String vmID = null;

  /** time the method was called (used to prevent duplicates) */
  private static long callTime = 0;

  /** time of last method call */
  private static long lastCallTime = 0;

  /** List of all UIDs that have been returned for the current millisecond. */
  private static List uidsThisMilliSecond = null;

  /**
   * Returns a Globally Unique Identifier.
   *
   * @throws Exception
   * @return String
   */
  public synchronized static String generateUID() throws Exception {

    String uniqueID = getUniqueID(getSeed());

    //is this a new millisecond
    if ( (lastCallTime != callTime)
        || (uidsThisMilliSecond == null)) {

      uidsThisMilliSecond = new ArrayList();
      uidsThisMilliSecond.add(uniqueID);
      lastCallTime = callTime;
    }
    else {

      //keep generating until a unique ID is found
      while (uidsThisMilliSecond.contains(uniqueID)) {

        uniqueID = getUniqueID(getSeed());
      }

      //add the result to be returned
      uidsThisMilliSecond.add(uniqueID);
    }

    return uniqueID;
  }

  /**
   * Returns a MD5 sum of the seed.
   *
   * @param seed char[]
   * @throws Exception
   * @return String
   */
  private synchronized static String getUniqueID(char[] seed) throws
      Exception {

    String uid = null;

    //digest the seed and convert to hex
    byte[] digested = digest(seed);
    StringBuffer buffer = new StringBuffer();
    int currentInt = 0;

    //convert each byte to an int (as hex)
    for (int i = 0; i < digested.length; ++i) {

      //conver to int
      currentInt = digested[i] & 0xFF;

      //is the int smaller than 16? (single digit hex)
      if (currentInt < 0x10) {

        buffer.append('0');
      }

      buffer.append(Integer.toHexString(currentInt));
    }

    uid = buffer.toString();

    //validate
    if (uid == null) {

      throw new Exception("Failed to generate UID.");
    }

    return uid;
  }

  /**
   * Returns an MD5 sum for the char []
   *
   * @param chars []
   * @throws Exception
   * @return byte[]
   */
  private static byte[] digest(char[] chars) throws Exception {

    //validate
    if (chars == null) {

      throw new IllegalArgumentException("Cannot get MD5 sum for null char [].");
    }

    try {

      MessageDigest digest = MessageDigest.getInstance("MD5");

      //add the chars to the buffer
      int bufferSize = chars.length * 2;
      ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
      for (int i = 0; i < chars.length; i++) {

        buffer.putChar(chars[i]);
      }

      //digest
      return digest.digest(buffer.array());
    }
    catch (NoSuchAlgorithmException algorithmException) {

      throw new Exception("Could not get MD5 algorithm.", algorithmException);
    }
  }

  /**
   * Returns an unique seed.
   *
   * @throws Exception
   * @return char []
   */
  private synchronized static char[] getSeed() throws Exception {

    StringBuffer seed = new StringBuffer();

    //location in universe (cyberspace). A JVM running on a particular machine.
    seed.append(getIP());
    seed.append(getJVMID());
    //time (current millisecond)
    seed.append(getTime());
    //random, allows multiple new UIDS per millisecond. (validated by List)
    seed.append(getRandom());
    //another Class could be generating UIDS the same way within the same JVM
    seed.append(UIDGenerator.class.getName());

    //conver to char []
    char[] chars = new char[seed.length()];
    seed.getChars(0, seed.length(), chars, 0);

    return chars;
  }

  /**
   * Returns a Random number/String.
   *
   * @return String
   */
  private synchronized static String getRandom() {

    long random = new SecureRandom().nextLong();

    return "" + random;
  }

  /**
   * Returns the current time in milliseconds.
   *
   * @return String
   */
  private synchronized static String getTime() {

    callTime = System.currentTimeMillis();

    return "" + callTime;
  }

  /**
   * Returns the IP address for this machine.
   *
   * @throws Exception
   * @return String
   */
  private synchronized static String getIP() throws Exception {

    try {

      //lazily obtain IP address
      if (ipAddress == null) {

        ipAddress = InetAddress.getLocalHost().getHostAddress();
      }

      return ipAddress;
    }
    catch (UnknownHostException hostException) {

      throw new Exception("Could not determine IP Address.", hostException);
    }
  }

  /**
   * Returns an Unique Identifier for this particular Java Virtual Machine.
   *
   * @throws Exception
   * @return String
   */
  private synchronized static String getJVMID() throws Exception {

    //lazily obtain JVM ID
    if (vmID == null) {

      vmID = new VMID().toString();
    }

    return vmID;
  }

}
