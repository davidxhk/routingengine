module com.routingengine
{
    // Features
    exports com.routingengine;          // Core logic of the routing engine
    exports com.routingengine.methods;  // Methods that are used by the routing engine
    exports com.routingengine.server;   // Server program for the routing engine
    exports com.routingengine.client;   // Client interface to communicate with the routing engine server
    exports com.routingengine.json;     // JSON tools and protocol that will be useful for development
    
    // Dependencies
    requires transitive com.google.gson;
    
    // For testing purposes
    requires org.junit.jupiter.api;
    requires org.junit.platform.suite.api;
    requires org.junit.platform.runner;
}