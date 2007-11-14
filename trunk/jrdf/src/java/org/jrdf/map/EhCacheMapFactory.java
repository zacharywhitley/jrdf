package org.jrdf.map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.jcache.JCache;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import java.util.Map;

public class EhCacheMapFactory implements MapFactory {
    private static final int NUMBER_OF_IN_MEMORY_OBJECTS = 100;
    private static final int TIME_TO_IDLE = 10;
    private static final int TIME_TO_LIVE = 10;
    private static final int EXPIRY_THREAD_TIME = 3600;
    private final DirectoryHandler dirHandler = new TempDirectoryHandler();
    private final String databaseName;
    private Cache cache;
    private CacheManager ehManager;
    private long mapNumber;

    public EhCacheMapFactory(String newDatabaseName) {
        databaseName = newDatabaseName;
        ehManager = new CacheManager();
    }

    public <T, A, U extends A> Map<T, U> createMap(Class<T> clazz1, Class<A> clazz2) {
        mapNumber++;
        Ehcache ehcache = new Cache(databaseName + mapNumber, NUMBER_OF_IN_MEMORY_OBJECTS,
            MemoryStoreEvictionPolicy.LFU, true, dirHandler.getDir().toString(), true, TIME_TO_LIVE, TIME_TO_IDLE,
            true, EXPIRY_THREAD_TIME, null);
        ehManager.addCache(ehcache);
        cache = ehManager.getCache(databaseName + mapNumber);
        return (Map<T, U>) new JCache(cache);
    }

    public void close() {
        ehManager.shutdown();
    }
}
