package org.jrdf.query.execute;

import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.AttributeName;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version $id: $
 */

public class ConstraintTupleCacheHandlerImpl implements ConstraintTupleCacheHandler {
    private Map<AttributeName, Set<ValueOperation>> cache;
    private long timeStamp;

    public ConstraintTupleCacheHandlerImpl() {
        cache = new LinkedHashMap<AttributeName, Set<ValueOperation>>();
    }


    public void addToCache(AttributeName name, ValueOperation valueOperation, long time) {
        Set<ValueOperation> set = cache.get(name);
        if (time == timeStamp) {
            if (set == null) {
                set = new HashSet<ValueOperation>();
            }
            set.add(valueOperation);
            cache.put(name, set);
        } else {
            cache.remove(name);
            set = new HashSet<ValueOperation>();
            set.add(valueOperation);
            cache.put(name, set);
            timeStamp = time;
        }
    }

    public Set<ValueOperation> getCachedValues(AttributeName name) {
        return cache.get(name);
    }

    public void clear() {
        cache.clear();
    }

    public void clear(AttributeName name) {
        cache.put(name, null);
    }

    public void setTimeStamp(long time) {
        timeStamp = time;
    }
}
