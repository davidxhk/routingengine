package com.routingengine.methods;


public abstract class AbstractSupportRequestAdminMethod extends AbstractSupportRequestMethod
{
    @Override
    protected final boolean requiresAdminRights()
    {
        return true;
    }
}
