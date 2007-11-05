package net.metanotion.util.skiplist;

public interface SkipList {
    void flush();

    void clear();

    int size();

    void put(Comparable key, Object val);

    Object remove(Comparable key);

    Object get(Comparable key);

    SkipIterator iterator();

    SkipIterator find(Comparable key);
}
