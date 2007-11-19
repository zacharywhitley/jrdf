package org.jrdf.set;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredSortedKeySet;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.map.BlankNodeBinding;
import org.jrdf.map.DirectoryHandler;
import org.jrdf.map.LongListBinding;
import org.jrdf.map.NodeBinding;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class StoredSetHandlerImpl implements StoredSetHandler {
    private Map<Class<?>, TupleBinding> binding = new HashMap<Class<?>, TupleBinding>();
    private final DirectoryHandler handler;

    public StoredSetHandlerImpl(DirectoryHandler handler) {
        this.handler = handler;
        binding.put(String.class, new StringBinding());
        binding.put(Long.class, new LongBinding());
        binding.put(LinkedList.class, new LongListBinding());
        binding.put(BlankNode.class, new BlankNodeBinding());
        binding.put(Node.class, new NodeBinding());
    }

    public Environment setUpEnvironment() throws DatabaseException {
        File dir = handler.getDir();
        dir.mkdirs();
        EnvironmentConfig env = new EnvironmentConfig();
        env.setTransactional(true);
        env.setAllowCreate(true);
        return new Environment(dir, env);
    }

    public DatabaseConfig setUpDatabaseConfig(boolean transactional) {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(transactional);
        dbConfig.setAllowCreate(true);
        return dbConfig;
    }

    public StoredClassCatalog setupCatalog(Environment env, String classCatalogString,  DatabaseConfig dbConfig)
        throws DatabaseException {
        Database catalogDb = env.openDatabase(null, classCatalogString, dbConfig);
        return new StoredClassCatalog(catalogDb);
    }

    public Database setupDatabase(Environment env, String dbName, DatabaseConfig dbConfig) throws DatabaseException {
        return env.openDatabase(null, dbName, dbConfig);
    }

    @SuppressWarnings({ "unchecked" })
    public <T> Set<T> createSet(Database database, Class<T> clazz) {
        EntryBinding keyBinding = getBinding(clazz);
        return new StoredSortedKeySet(database, keyBinding, true);
    }

    private EntryBinding getBinding(Class<?> clazz) {
        if (binding.keySet().contains(clazz)) {
            return binding.get(clazz);
        } else {
            throw new IllegalArgumentException("Cannot retrieve binding for class: " + clazz.getClass());
        }
    }

}
