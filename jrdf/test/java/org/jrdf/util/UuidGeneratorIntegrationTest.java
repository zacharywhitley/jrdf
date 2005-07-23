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
 * Integration test for {@link org.jrdf.util.UuidGenerator}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class UuidGeneratorIntegrationTest extends TestCase {
  // FIXME: Remove classname as string and instead use UuidGenerator.class.getName() (to aid with refactoring).
  private static final Class[] NO_PARAMS = null;
  private static final Object[] NO_ARGS = (Object[]) null;
  private static final String CLASS_NAME = "UuidGenerator";
  private static final String FULL_CLASS_NAME = "org.jrdf.util." + CLASS_NAME;
  private static final Object UUID_GENERATOR_INSTANCE = null;
  private static final int NUM_THREADS = 10;
  private static final int NUM_CLASSLOADERS = 10;
  private static final int NUM_UUIDS_TO_GENERATE = UuidGeneratorUnitTest.NUM_UIDS /
      UuidGeneratorIntegrationTest.NUM_THREADS;
  private static final String GENERATE_UUID_METHOD_NAME = "generateUuid";

  public void testMultipleClassLoadersGenerateUniqueUuids() throws Exception {
    URL[] uidClassUrls = new URL[]{
      ClassLoader.getSystemClassLoader().getResource(CLASS_NAME)};
    for (int i = 0; i < NUM_CLASSLOADERS; i++) {
      Class<?> uuidGeneratorClass = new URLClassLoader(uidClassUrls).loadClass(
          FULL_CLASS_NAME);
      checkUuidGenerator(uuidGeneratorClass);
    }
  }

  private void checkUuidGenerator(Class uuidGenerator) throws Exception {
    Method generateUuidMethod = uuidGenerator.getMethod(
        GENERATE_UUID_METHOD_NAME, NO_PARAMS);
    generateUuids(generateUuidMethod);
  }

  private void generateUuids(Method generateUID) throws Exception {
    Set<String> uuids = new HashSet<String>(UuidGeneratorUnitTest.NUM_UIDS);
    for (int i = 0; i < UuidGeneratorUnitTest.NUM_UIDS; i++) {
      String uuid = invokeGenerateUuidMethod(generateUID);
      UuidGeneratorUnitTest.checkUuidIsUnique(uuids, uuid);
      uuids.add(uuid);
    }
  }

  private String invokeGenerateUuidMethod(Method generateUuidMethod)
      throws Exception {
    return (String) generateUuidMethod.invoke(UUID_GENERATOR_INSTANCE, NO_ARGS);
  }

  public static void testMultipleThreadsGenerateUniqueUuids() throws Exception {
    Set<String> uuids = new HashSet<String>(UuidGeneratorUnitTest.NUM_UIDS);
    List<Thread> threadList = new ArrayList<Thread>();
    for (int i = 0; i < NUM_THREADS; i++) {
      Thread thread = startThread(uuids);
      threadList.add(thread);
    }
    waitForThreadCompletion(threadList);
  }

  private static Thread startThread(Set<String> uuids) {
    Thread currentThread = new Thread(
        new UuidPopulator(uuids, NUM_UUIDS_TO_GENERATE));
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
