package com.routingengine;

import com.google.gson.JsonObject;


public class User
{
    private String name;
    private String email;
    
    public User(String name, String email)
    {
        if (name == null)
            throw new IllegalArgumentException("name missing");
        
        this.name = name;
        
        if (email == null)
            throw new IllegalArgumentException("email missing");
        
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
