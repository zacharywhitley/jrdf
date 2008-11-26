package org.jrdf.util.test

class RdfNamespace {
    String namespace
    RdfBuilder builder

    def getProperty(String name) {
        builder."$namespace:$name"()
    }
}