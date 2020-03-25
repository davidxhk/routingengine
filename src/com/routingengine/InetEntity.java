package com.routingengine;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;


public class InetEntity
{
  private final UUID uuid;
  private volatile InetAddress address;
  public static final long TIMEOUT_MILLIS = 30000L;
  
  private InetEntity()
  {
    uuid = UUID.randomUUID();
  }
  
  public InetEntity(InetAddress address)
  {
    this();
    
    setAddress(address);
  }
  
  public InetEntity(String address)
  {
    this();
    
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
  
  public void setAddress(String address)
  {
    try {
      setAddress(InetAddress.getByName(address));
    }
    
    catch (UnknownHostException exception) {
      throw new IllegalArgumentException("address invalid");
    }
  }
  
  public void setAddress(InetAddress address)
  {
    this.address = address;
  }
}