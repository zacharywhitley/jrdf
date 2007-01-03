package org.jrdf.util.test.instantiate;

public class LongInstantiator implements Instantiator {
    public Object instantiate() {
        return new Long(123);
    }
}
