package com.routingengine;

import com.google.gson.JsonObject;


public class User
{
  private String name;
  private String email;
  
  public User(String name, String email)
  {
    this.name = name;
    this.email = email;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getEmail()
  {
    return email;
  }
  
  public JsonObject toJson()
  {
    JsonObject jsonObject = new JsonObject();
    
    jsonObject.addProperty("name", name);
    jsonObject.addProperty("email", email);
    
    return jsonObject;
  }
}