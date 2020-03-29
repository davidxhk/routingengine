package com.routingengine;

import static java.util.logging.Logger.getAnonymousLogger;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class Logger
{
    private static final java.util.logging.Logger LOGGER = getAnonymousLogger();
    private static final String LOGFILE = "logfile.txt";
    
    static
    {
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.INFO);
        
        Formatter formatter = new Formatter()
        {
            @Override
            public String format(LogRecord record)
            {
                return record.getMessage() + "\n";
            }
        };
        
        ConsoleHandler consoleHandler = new ConsoleHandler()
        {
            @Override
            protected synchronized void setOutputStream(OutputStream out)
                throws SecurityException
            {
                super.setOutputStream(System.out);
            }
        };
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(formatter);
        LOGGER.addHandler(consoleHandler);
            
        try {
            FileHandler fileHandler = new FileHandler(LOGFILE, true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        }
        
        catch (IOException exception) { }
    }
    
    public static synchronized void log(String message)
    {
        String logMessage = new Timestamp(System.currentTimeMillis()).toString()
                + " " + Thread.currentThread().getName() + ": " + message;
        
        LOGGER.info(logMessage);
    }
}
