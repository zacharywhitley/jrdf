package org.jrdf.set;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.SortedSet;

public class BdbSetFactory implements SetFactory {
    private final StoredSetHandler handler;
    private final String databaseName;
    private Environment env;
    private Database database;
    private long setNumber;

    public BdbSetFactory(StoredSetHandler newHandler, String newDatabaseName) {
        checkNotNull(newHandler, newDatabaseName);
        this.handler = newHandler;
        this.databaseName = newDatabaseName;
    }

    public <T> SortedSet<T> createSet(Class<T> clazz) {
        try {
            setNumber++;
            env = handler.setUpEnvironment();
            DatabaseConfig dbConfig = handler.setUpDatabaseConfig(false);
            database = handler.setupDatabase(env, databaseName + setNumber, dbConfig);
            return handler.createSet(database, clazz);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            closeDatabase();
        } finally {
            closeEnvironment();
        }
    }

    private void closeDatabase() {
        try {
            if (database != null) {
                database.close();
            }
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeEnvironment() {
        try {
            if (env != null) {
                env.close();
            }
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
