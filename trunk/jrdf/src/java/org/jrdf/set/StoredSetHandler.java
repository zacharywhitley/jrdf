package org.jrdf.set;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Nov 19, 2007
 * Time: 10:54:40 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StoredSetHandler {
    Environment setUpEnvironment() throws DatabaseException;

    DatabaseConfig setUpDatabaseConfig(boolean transactional);

    StoredClassCatalog setupCatalog(Environment env, String classCatalogString, DatabaseConfig dbConfig)
        throws DatabaseException;

    Database setupDatabase(Environment env, String dbName, DatabaseConfig dbConfig) throws DatabaseException;

    <T> Set<T> createSet(Database database, Class<T> clazz);
}
