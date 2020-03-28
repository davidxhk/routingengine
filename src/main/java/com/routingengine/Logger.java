package com.routingengine;

import java.sql.Timestamp;


public class Logger
{
    public static synchronized void log(String message)
    {
        String logMessage = new Timestamp(System.currentTimeMillis()).toString()
                + " " + Thread.currentThread().getName() + ": " + message;
        
        System.out.println(logMessage);
    }
}
