module com.routingengine
{
    requires transitive com.google.gson;
    requires org.junit.jupiter.api;
    exports com.routingengine;
    exports com.routingengine.json;
    exports com.routingengine.server;
    exports com.routingengine.client;
}