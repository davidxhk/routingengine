package com.routingengine;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;


public class InetEntity
{
    private final UUID uuid;
    private volatile InetAddress address;
    
    public InetEntity()
    {
        this(UUID.randomUUID(), null);
    }
    
    public InetEntity(UUID uuid)
    {
        this(uuid, null);
    }
    
    public InetEntity(InetAddress address)
    {
        this();
        
        setAddress(address);
    }
    
    public InetEntity(String uuidString, String address)
    {
        if (uuidString == null)
            throw new IllegalArgumentException("uuid missing");
        
        try {
            this.uuid = UUID.fromString(uuidString);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("uuid invalid");
        }
        
        if (address == null)
            setLoopbackAddress();
        
        else
            setAddress(address);
    }
    
    public InetEntity(UUID uuid, InetAddress address)
    {
        if (uuid == null)
            throw new IllegalArgumentException("uuid missing");
        
        this.uuid = uuid;
        
        if (address == null)
            setLoopbackAddress();
        
        else
            setAddress(address);
    }
    
    public UUID getUUID()
    {
        return uuid;
    }
    
    public InetAddress getAddress()
    {
        return address;
    }
    
    private void setLoopbackAddress()
    {
        address = InetAddress.getLoopbackAddress();
    }
    
    public void setAddress(String address)
    {
        if (address == null)
            throw new IllegalArgumentException("address missing");
        
        try {
            setAddress(InetAddress.getByName(address));
        }
        
        catch (UnknownHostException exception) {
            throw new IllegalArgumentException("address invalid");
        }
    }
    
    public void setAddress(InetAddress address)
    {
        if (address == null)
            throw new IllegalArgumentException("address missing");
        
        this.address = address;
    }
}
