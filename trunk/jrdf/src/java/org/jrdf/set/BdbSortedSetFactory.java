package org.jrdf.set;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

public class BdbSortedSetFactory implements SortedSetFactory {
    private final BdbEnvironmentHandler handler;
    private final String databaseName;
    private Environment env;
    private List<Database> databases = new ArrayList<Database>();
    private long setNumber;

    public BdbSortedSetFactory(BdbEnvironmentHandler newHandler, String newDatabaseName) {
        checkNotNull(newHandler, newDatabaseName);
        this.handler = newHandler;
        this.databaseName = newDatabaseName;
    }

    public <T> SortedSet<T> createSet(Class<T> clazz) {
        try {
            setNumber++;
            env = handler.setUpEnvironment();
            DatabaseConfig dbConfig = handler.setUpDatabaseConfig(false);
            Database database = handler.setupDatabase(env, databaseName + setNumber, dbConfig);
            databases.add(database);
            final SortedSet<T> set = handler.createSet(database, clazz);
            set.clear();
            return set;
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> SortedSet<T> createSet(Class<T> clazz, Comparator<? super T> comparator) {
        throw new UnsupportedOperationException("Bdb implementation does not support comparators");
    }

    public void close() {
        try {
            setNumber = 0;
            closeDatabase();
        } finally {
            closeEnvironment();
        }
    }

    private void closeDatabase() {
        try {
            if (!databases.isEmpty()) {
                for (Database database : databases) {
                    database.close();
                }
            }
            databases.clear();
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
