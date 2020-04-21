module com.routingengine
{
    exports com.routingengine.websocket;
    exports com.routingengine;
    exports com.routingengine.server;
    exports com.routingengine.examples;
    exports com.routingengine.json;
    exports com.routingengine.client;
    exports com.routingengine.methods;
    
    requires transitive com.google.gson;
    requires java.logging;
    requires java.sql;
    requires junit;
    requires org.junit.jupiter.api;
    requires org.junit.platform.runner;
    requires org.junit.platform.suite.api;
    
    opens com.routingengine.methods;
}