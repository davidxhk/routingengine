package com.routingengine.methods;


public abstract class AbstractAgentAdminMethod extends AbstractAgentMethod
{
    @Override
    protected final boolean requiresAdminRights()
    {
        return true;
    }
}
