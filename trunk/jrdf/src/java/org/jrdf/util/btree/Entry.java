/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */

package org.jrdf.util.btree;

import java.util.Map;
import java.util.Set;

public class Entry implements Map.Entry<Long, Map<Long, Set<Long>>> {
    private final Long key;
    private final Map<Long, Set<Long>> values;

    public Entry(Long key, Map<Long, Set<Long>> values) {
        this.key = key;
        this.values = values;
    }

    public Long getKey() {
        return key;
    }

    public Map<Long, Set<Long>> getValue() {
        return values;
    }

    public Map<Long, Set<Long>> setValue(Map<Long, Set<Long>> value) {
        throw new UnsupportedOperationException("Cannot set values - read only");
    }

    public String toString() {
        return "Key: " + key + " Entries: " + values;
    }
}
