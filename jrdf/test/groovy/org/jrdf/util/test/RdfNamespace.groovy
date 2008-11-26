package org.jrdf.util.test

class RdfNamespace extends BuilderSupport {
    String namespace
    RdfBuilder builder

    def getProperty(String name) {
        builder."$namespace:$name"()
    }

    protected void setParent(Object name, Object value) {
        builder.setParent("$namespace:$name", o1)
    }

    protected void nodeCompleted(Object name, Object value) {
        builder.nodeCompleted("$namespace:$name", value)
    }

    protected Object createNode(Object name) {
        builder.createNode("$namespace:$name")
    }

    protected Object createNode(Object name, Object value) {
        builder.createNode("$namespace:$name", value)
    }

    protected Object createNode(Object name, Map attributes) {
        builder.createNode("$namespace:$name", attributes)
    }

    protected Object createNode(Object name, Map attributes, Object value) {
        builder.createNode("$namespace:$name", attributes, value)
    }
}