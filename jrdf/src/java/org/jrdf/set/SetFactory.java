package org.jrdf.set;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Nov 19, 2007
 * Time: 10:30:13 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SetFactory {
    <T> Set<T> createSet(Class<T> clazz);

    void close();
}
