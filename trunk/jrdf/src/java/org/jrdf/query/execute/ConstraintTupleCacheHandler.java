package org.jrdf.query.execute;

import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.AttributeName;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Nov 27, 2008
 * Time: 10:08:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ConstraintTupleCacheHandler {
    void addToCache(AttributeName name, ValueOperation tuple, long time);

    Set<ValueOperation> getCachedValues(AttributeName name);

    void clear();

    void clear(AttributeName name);

    void setTimeStamp(long time);
}
