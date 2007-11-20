package org.jrdf.util.bdb;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;

import java.util.SortedSet;
import java.util.Map;

public interface BdbEnvironmentHandler {
    Environment setUpEnvironment() throws DatabaseException;

    DatabaseConfig setUpDatabaseConfig(boolean transactional);

    StoredClassCatalog setupCatalog(Environment env, String classCatalogString, DatabaseConfig dbConfig)
        throws DatabaseException;

    Database setupDatabase(Environment env, String dbName, DatabaseConfig dbConfig) throws DatabaseException;

    <T> SortedSet<T> createSet(Database database, Class<T> clazz);

    <T, A, U extends A> Map<T, U> createMap(Database database, Class<T> clazz1, Class<A> clazz2);
}
