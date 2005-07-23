package org.jrdf.util;

import java.util.Set;

import junit.framework.Assert;

final class UuidPopulator implements Runnable {
  private final Set<String> uuids;
  private int numUuidsToGenerate;

  public UuidPopulator(Set<String> uuids, int numUuidsToGenerate) {
    this.uuids = uuids;
    this.numUuidsToGenerate = numUuidsToGenerate;
  }

  // FIXME: This is very similar to UuidGeneratorUnitTest.generateUuids(int).
  public void run() {
    try {
      generateUuids();
    }
    catch (Exception exception) {
      throw new RuntimeException("Error occurred while testing concurrency.",
          exception);
    }
  }

  private void generateUuids() throws Exception {
    for (int i = 0; i < numUuidsToGenerate; i++) {
      String uuid = UuidGenerator.generateUuid();
      checkUuidUnique(uuid);
      uuids.add(uuid);
    }
  }

  // FIXME: Can we remove the synchronisation, or move this synch'd version into the unit test?
  private void checkUuidUnique(String uuid) {
    synchronized (UuidGeneratorIntegrationTest.class) {
      if (uuids.contains(uuid)) {
        Assert.fail("UUID " + uuid + " is not unique");
      }
    }
  }
}
