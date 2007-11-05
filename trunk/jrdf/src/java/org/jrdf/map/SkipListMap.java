package org.jrdf.map;

import net.metanotion.util.skiplist.BaseSkipList;
import net.metanotion.util.skiplist.SkipList;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SkipListMap<K, V> implements Map<K, V> {
    private SkipList keys;
    private SkipList values;

    public SkipListMap() {
        this.keys = new BaseSkipList(3);
        this.values = new BaseSkipList(3);
    }

    public SkipListMap(SkipList keys, SkipList values) {
        this.keys = keys;
        this.values = values;
    }

    public int size() {
        return keys.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(Object key) {
        return keys.get((Comparable) key) != null;
    }

    public boolean containsValue(Object value) {
        return values.get((Comparable) value) != null;
    }

    public V get(Object key) {
        Integer index = (Integer) keys.get((Comparable) key);
        if (index != null) {
            return (V) values.get(index);
        } else {
            return null;
        }
    }

    public V put(K key, V value) {
        Integer index = (Integer) keys.get((Comparable) key);
        V previousValue = null;
        if (index == null) {
            index = keys.size();
            keys.put((Comparable) key, index);
        } else {
            previousValue = (V) values.get(index);
        }
        values.put(index, value);
        return previousValue;
    }

    public V remove(Object key) {
        Integer index = (Integer) keys.get((Comparable) key);
        if (index == null) {
            return null;
        } else {
            keys.remove((Comparable) key);
            V previousValue = (V) values.get(index);
            values.remove(index);
            return previousValue;
        }
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        for (Entry<? extends K, ? extends V> entry : t.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        keys.clear();
        values.clear();
    }

    public Set<K> keySet() {
        return new SkipListSet(keys);
    }

    public Collection<V> values() {
        return new SkipListSet(values);
    }

    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
