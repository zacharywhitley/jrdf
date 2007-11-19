package org.jrdf.set;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;

import java.util.Set;

public interface StoredSetHandler {
    Environment setUpEnvironment() throws DatabaseException;

    DatabaseConfig setUpDatabaseConfig(boolean transactional);

    StoredClassCatalog setupCatalog(Environment env, String classCatalogString, DatabaseConfig dbConfig)
        throws DatabaseException;

    Database setupDatabase(Environment env, String dbName, DatabaseConfig dbConfig) throws DatabaseException;

    <T> Set<T> createSet(Database database, Class<T> clazz);
}
