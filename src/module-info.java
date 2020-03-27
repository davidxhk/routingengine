module com.routingengine
{
    // Features
    exports com.routingengine.server;
    exports com.routingengine.client;
    exports com.routingengine.json;   // JSON tools and protocol that will be useful for development
    
    // Dependencies
    requires transitive com.google.gson;
    
    // For testing purposes
    requires org.junit.jupiter.api;
    requires org.junit.platform.suite.api;
    requires org.junit.platform.runner;
    opens com.routingengine.test.methods;
}